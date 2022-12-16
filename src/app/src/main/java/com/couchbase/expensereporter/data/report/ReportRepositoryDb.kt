package com.couchbase.expensereporter.data.report

import android.content.Context
import android.util.Log
import com.couchbase.expensereporter.data.DatabaseProvider
import com.couchbase.expensereporter.models.Manager
import com.couchbase.expensereporter.models.Report
import com.couchbase.expensereporter.models.ReportDao
import com.couchbase.expensereporter.services.AuthenticationService
import com.couchbase.lite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ReportRepositoryDb (
    private val databaseProvider: DatabaseProvider,
    private val authenticationService: AuthenticationService)
    : ReportRepository {

    override val databaseName: String
        get() = databaseProvider.currentReportDatabaseName

    override suspend fun get(documentId: String): Report {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseProvider.reportDatabase
                db?.let { database ->
                    val doc = database.getDocument(documentId)
                    doc?.let { document ->
                        val json = document.toJSON()
                        json?.let { reportJson ->
                            return@withContext (Json.decodeFromString<Report>(reportJson))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            val user = authenticationService.getCurrentUser()
            return@withContext(
                    Report(
                        reportId = documentId,
                        reportDate = System.currentTimeMillis(),
                        department = user.department,
                        createdBy = user.username,
                        documentType = "report",
                        status = "Draft",
                        isComplete = false
                    )
                )
        }
    }

    override suspend fun getDocuments(): Flow<List<Report>> {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseProvider.reportDatabase
                // NOTE - the as method is a also a keyword in Kotlin, so it must be escaped using
                // `as` - this will probably break auto complete suggestion, so it will act like the where
                // method isn't available  work around is to do your entire statement without the as
                // function call and add that in last
                db?.let { database ->
                    val query = QueryBuilder        // <1>
                        .select(SelectResult.all()) // <2>
                        .from(DataSource.database(database).`as`("item")) // <3>
                        .where( //4
                            Expression.property("documentType")
                                .equalTo(Expression.string("report")) // <4>
                        ) // <4>

                    // create a flow to return the results dynamically as needed - more information on
                    // CoRoutine Flows can be found at
                    // https://developer.android.com/kotlin/flow
                    val flow = query        // <1>
                        .queryChangeFlow()  // <1>
                        .map { qc -> mapQueryChangeToReport(qc) } // <2>
                        .flowOn(Dispatchers.IO)  // <3>
                    query.execute()  // <4>
                    return@withContext flow  // <5>
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext  flow { }
        }
    }

    private fun mapQueryChangeToReport(queryChange: QueryChange): List<Report> {
        val documents = mutableListOf<Report>() // 1
        try {
            queryChange.results?.let { results ->  // 2
                results.forEach { result ->      // 3
                    val json = result.toJSON()     // 4
                    val document =
                        Json.decodeFromString<ReportDao>(json).item   // 5
                    documents.add(document) // 6
                }
            }
        }catch(e: Exception){
            Log.e(e.message, e.stackTraceToString())
        }
        return documents // 7
    }

    override suspend fun save(document: Report) :Boolean {
        return withContext(Dispatchers.IO) {
            var result = false
            try {
                val db = databaseProvider.reportDatabase
                db?.let { database ->
                    val encoder = Json { encodeDefaults = true }
                    val json = encoder.encodeToString(document)
                    val doc = MutableDocument(document.reportId, json)
                    database.save(doc)
                    result = true
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext result
        }
    }

    override suspend fun delete(documentId: String): Boolean {
        return withContext(Dispatchers.IO) {
            var result = false
            try {
                val db = databaseProvider.reportDatabase
                db?.let { database ->
                    val document = database.getDocument(documentId)
                    document?.let { doc ->
                        db.delete(doc)
                        result = true
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext result
        }
    }

    override suspend fun count(): Int {
        return withContext(Dispatchers.IO) {
            var count = 0
            try {
                val db = databaseProvider.reportDatabase
                db?.let { database ->
                    val query = QueryBuilder  // 1
                        .select(
                            SelectResult.expression(com.couchbase.lite.Function.count(Expression.string("*")))
                                .`as`("count") ) // 2
                        .from(DataSource.database(database)) //3
                        .where(
                            Expression.property("documentType").equalTo(Expression.string("report")) ) // 4
                    val results = query.execute().allResults() // 5
                    count = results[0].getInt("count") // 6
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext count
        }
    }

    override suspend fun updateManager(documentId: String, manager: Manager) {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseProvider.reportDatabase
                val document = get(documentId)
                var updateDoc = document.copy(approvalManager = manager)
                db?.let { database ->
                    val json = Json.encodeToString(updateDoc)
                    val doc = MutableDocument(updateDoc.reportId, json)
                    database.save(doc)
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
        }
    }

    override suspend fun loadSampleData() {
        TODO("Not yet implemented")
    }

}