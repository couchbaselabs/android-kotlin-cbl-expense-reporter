package com.couchbase.expensereporter.data.report

import android.util.Log
import com.couchbase.expensereporter.data.DatabaseProvider
import com.couchbase.expensereporter.models.Manager
import com.couchbase.expensereporter.models.Report
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

class ReportRepositoryDb(
    private val databaseProvider: DatabaseProvider,
    private val authenticationService: AuthenticationService
) : ReportRepository {

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
            return@withContext (
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
                db?.let { database ->
                    //QUERY WITHOUT JOIN
                    val query = database.createQuery("SELECT item.reportId, item.name, item.description, item.isComplete, item.documentType, item.reportDate, item.updatedDate, item.status, item.department, item.createdBy, item.approvalManager, 0.0 as amount FROM _ AS item WHERE item.documentType=\"report\"")

                  /* QUERYBUIDER VERSION OF JOIN
                    val query = QueryBuilder.select(
                        SelectResult.expression(Expression.property("reportId").from("item")),
                        SelectResult.expression(Expression.property("name").from("item")),
                        SelectResult.expression(Expression.property("description").from("item")),
                        SelectResult.expression(Expression.property("isComplete").from("item")),
                        SelectResult.expression(Expression.property("documentType").from("item")),
                        SelectResult.expression(Expression.property("reportDate").from("item")),
                        SelectResult.expression(Expression.property("updatedDate").from("item")),
                        SelectResult.expression(Expression.property("status").from("item")),
                        SelectResult.expression(Expression.property("department").from("item")),
                        SelectResult.expression(Expression.property("createdBy").from("item")),
                        SelectResult.expression(Expression.property("approvalManager").from("item")),
                        SelectResult.expression(Function.sum(Expression.property("amount").from("e"))).`as`("amount")
                        )
                        .from(DataSource.database(database).`as`("item"))
                        .join(Join.join(DataSource.database(database).`as`("e"))
                            .on(Expression.property("reportId").from("item").equalTo(Expression.property("reportId").from("e"))))
                        .where(
                            Expression.property("documentType").from("item")
                                .equalTo(Expression.string("report"))
                                .and(
                                    Expression.property("documentType").from("e")
                                        .equalTo(Expression.string("expense"))
                                )
                        )
                    */

                    //SQL++ VERSION OF THE JOIN
                    //val query = database.createQuery("SELECT item.reportId, item.name, item.description, item.isComplete, item.documentType, item.reportDate, item.updatedDate, item.status, item.department, item.createdBy, item.approvalManager, SUM(e.amount) as amount FROM _ AS item LEFT JOIN _ AS e ON item.reportId = e.reportId WHERE item.documentType=\"report\" AND e.documentType=\"expense\"")

                    // create a flow to return the results dynamically as needed - more information on
                    // CoRoutine Flows can be found at
                    // https://developer.android.com/kotlin/flow
                    val flow = query
                        .queryChangeFlow()
                        .map { qc -> mapQueryChangeToReport(qc) }
                        .flowOn(Dispatchers.IO)

                    Log.d("**JOIN QUERY**",query.explain())
                    query.execute()
                    return@withContext flow
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext flow { }
        }
    }

    private fun mapQueryChangeToReport(queryChange: QueryChange): List<Report> {
        val documents = mutableListOf<Report>()
        try {
            queryChange.results?.let { results ->
                results.forEach { result ->
                    val json = result.toJSON()
                    val document =
                        Json.decodeFromString<Report>(json)
                    if (!document.reportId.isNullOrBlank()) {
                        documents.add(document)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(e.message, e.stackTraceToString())
        }
        return documents
    }

    override suspend fun save(document: Report): Boolean {
        return withContext(Dispatchers.IO) {
            var result = false
            try {
                val db = databaseProvider.reportDatabase
                db?.let { database ->
                    val updateDoc = document.copy(updatedDate = System.currentTimeMillis())
                    val encoder = Json { encodeDefaults = true }
                    val json = encoder.encodeToString(updateDoc)
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
                    val query =
                        database.createQuery("SELECT COUNT(*) AS count FROM _ as item WHERE documentType=\"report\"")
                    val results = query.execute().allResults()
                    count = results[0].getInt("count")
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
                val updateDoc = document.copy(approvalManager = manager)
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