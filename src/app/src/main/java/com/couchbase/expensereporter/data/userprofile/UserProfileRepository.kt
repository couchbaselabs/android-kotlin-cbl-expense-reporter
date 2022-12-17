package com.couchbase.expensereporter.data.userprofile

import com.couchbase.expensereporter.data.DatabaseProvider
import com.couchbase.expensereporter.data.KeyValueRepository
import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.MutableDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserProfileRepository(
    var databaseProvider: DatabaseProvider) : KeyValueRepository {

    override fun reportDatabaseName(): String {
        return databaseProvider.currentReportDatabaseName
    }

    override fun reportDatabaseLocation(): String? {
        return databaseProvider.reportDatabase?.path
    }

    override suspend fun get(currentUser: String): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            val results = HashMap<String, Any>()
            results["email"] = currentUser as Any

            val database = databaseProvider.reportDatabase
            database?.let { db ->
                val documentId = getCurrentUserDocumentId(currentUser)
                val doc = db.getDocument(documentId)
                if (doc != null) {
                    if (doc.contains("givenName")) {
                        results["givenName"] = doc.getString("givenName") as Any
                    }
                    if (doc.contains("surname")) {
                        results["surname"] = doc.getString("surname") as Any
                    }
                    if (doc.contains("jobTitle")) {
                        results["jobTitle"] = doc.getString("jobTitle") as Any
                    }
                    if (doc.contains("department")) {
                        results["department"] = doc.getString("department") as Any
                    }
                    if (doc.contains("imageData")) {
                        results["imageData"] = doc.getBlob("imageData") as Any
                    }
                    if (doc.contains("documentType")){
                        results["documentType"] = doc.getString("documentType") as Any
                    }
                }
            }
            return@withContext results
        }
    }

    override suspend fun save(data: Map<String, Any>): Boolean {
        return withContext(Dispatchers.IO) {
            val email = data["email"] as String
            val documentId = getCurrentUserDocumentId(email)
            val mutableDocument = MutableDocument(documentId, data)
            try {
                val database = databaseProvider.reportDatabase
                database?.save(mutableDocument)
            } catch (e: CouchbaseLiteException) {
                android.util.Log.e(e.message, e.stackTraceToString())
                return@withContext false
            }
            return@withContext true
        }
    }

    override suspend fun count(): Int {
        return withContext(Dispatchers.IO) {
            val database = databaseProvider.reportDatabase
            database?.let { db ->
                val query = "SELECT COUNT(*) AS count FROM _ WHERE documentType=\"user\""
                val results = db.createQuery(query).execute().allResults()
                return@withContext results[0].getInt("count")
            }
            return@withContext 0
        }
    }

    suspend fun delete(documentId: String): Boolean {
        return withContext(Dispatchers.IO) {
            var result = false
            val database = databaseProvider.reportDatabase
            database?.let { db ->
                val document = db.getDocument(documentId)
                document?.let {
                    db.delete(it)
                    result = true
                }
            }
            return@withContext result
        }
    }

    private fun getCurrentUserDocumentId(currentUser: String): String {
        return "user::${currentUser}"
    }
}