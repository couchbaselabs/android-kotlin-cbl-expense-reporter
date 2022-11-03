package com.couchbase.expensereporter.data.expense

import android.content.Context
import android.util.Log
import com.couchbase.expensereporter.data.DatabaseProvider
import com.couchbase.expensereporter.models.StandardExpense
import com.couchbase.expensereporter.services.AuthenticationService
import com.couchbase.lite.*
import com.couchbase.lite.Function
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class ExpenseRepositoryDb(private val context: Context,
                          private val authenticationService: AuthenticationService) : ExpenseRepository {
    private val databaseProvider: DatabaseProvider = DatabaseProvider.getInstance(context)

    override suspend fun getExpenses(reportId: String): Flow<List<StandardExpense>>? {
        return withContext(Dispatchers.IO){
            try {

            }catch(e: Exception){
                Log.e(e.message, e.stackTraceToString())
            }

            return@withContext flow { }
        }
    }

    override suspend fun get(reportId: String, expenseId: String): StandardExpense {
        return withContext(Dispatchers.IO) {
            try {
                val db = DatabaseProvider.getInstance(context).reportDatabase
                db?.let { database ->
                    val doc = database.getDocument(reportId)
                    doc?.let { document ->
                        val json = document.toJSON()
                        json?.let { expenseJson ->
                            return@withContext (
                                    Json.decodeFromString<StandardExpense>(expenseJson))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }

            return@withContext StandardExpense(
                expenseId = expenseId,
                reportId = reportId,
                documentType = "expense",
            )
        }
    }

    override suspend fun save(document: StandardExpense) {
        return withContext(Dispatchers.IO) {
            try {
                val db = DatabaseProvider.getInstance(context).reportDatabase
                db?.let { database ->
                    val json = Json.encodeToString(document)
                    val doc = MutableDocument(document.reportId, json)
                    database.save(doc)
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
        }
    }

    override suspend fun count(): Int {
        return withContext(Dispatchers.IO){
            var resultCount = 0
            try{
                val db = databaseProvider.reportDatabase
                db?.let { database ->
                    val query = QueryBuilder
                        .select(SelectResult.expression(Function.count(Expression.string("*"))).`as`("count"))
                        .from(DataSource.database(database))
                        .where(Expression.property("documentType").equalTo(Expression.string("expenseType")))
                    val results = query.execute().allResults()
                    resultCount = results[0].getInt("count")
                }
            } catch(e: Exception){
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext resultCount
        }
    }
}