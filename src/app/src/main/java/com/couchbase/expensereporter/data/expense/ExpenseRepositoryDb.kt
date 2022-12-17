package com.couchbase.expensereporter.data.expense

import android.content.Context
import android.util.Log
import com.couchbase.expensereporter.data.DatabaseProvider
import com.couchbase.expensereporter.models.ExpenseTypeDao
import com.couchbase.expensereporter.models.StandardExpense
import com.couchbase.expensereporter.models.StandardExpenseDao
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

class ExpenseRepositoryDb(
    private val databaseProvider: DatabaseProvider
) : ExpenseRepository {

    override suspend fun getExpenses(reportId: String): Flow<List<StandardExpense>>? {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseProvider.reportDatabase
                // NOTE - the as method is a also a keyword in Kotlin, so it must be escaped using
                // `as` - this will probably break intellisense, so it will act like the where
                // method isn't available  work around is to do your entire statement without the as
                // function call and add that in last
                db?.let { database ->
                    val query = QueryBuilder
                        .select(SelectResult.all())
                        .from(DataSource.database(database).`as`("item"))
                        .where(
                            Expression.property("documentType")
                                .equalTo(Expression.string("expense"))
                                .and(
                                    Expression.property("reportId")
                                        .equalTo(Expression.string(reportId))
                                )
                        )
                    // create a flow to return the results dynamically as needed - more information on
                    // CoRoutine Flows can be found at
                    // https://developer.android.com/kotlin/flow
                    val flow = query
                        .queryChangeFlow()
                        .map { qc -> mapExpenses(qc) }
                        .flowOn(Dispatchers.IO)
                    query.execute()
                    return@withContext flow
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext flow { }
        }
    }

    private fun mapExpenses(queryChange: QueryChange): List<StandardExpense> {
        val expenses = mutableListOf<StandardExpense>()
        queryChange.results?.let { results ->
            results.forEach { result ->
                val json = result.toJSON()
                val expense = Json.decodeFromString<StandardExpenseDao>(json).item
                expenses.add(expense)
            }
        }
        return expenses
    }

    override suspend fun get(reportId: String, documentId: String): StandardExpense {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseProvider.reportDatabase
                db?.let { database ->
                    val doc = database.getDocument(documentId)
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
                expenseId = documentId,
                reportId = reportId,
                date = System.currentTimeMillis(),
                documentType = "expense",
            )
        }
    }

    override suspend fun save(document: StandardExpense) {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseProvider.reportDatabase
                db?.let { database ->

                    val json = Json.encodeToString(document)
                    val doc = MutableDocument(document.expenseId, json)
                    database.save(doc)
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
        }
    }

    override suspend fun count(): Int {
        return withContext(Dispatchers.IO) {
            var resultCount = 0
            try {
                val db = databaseProvider.reportDatabase
                db?.let { database ->
                    val query = QueryBuilder
                        .select(
                            SelectResult.expression(Function.count(Expression.string("*")))
                                .`as`("count")
                        )
                        .from(DataSource.database(database))
                        .where(
                            Expression.property("documentType")
                                .equalTo(Expression.string("expense"))
                        )
                    val results = query.execute().allResults()
                    resultCount = results[0].getInt("count")
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext resultCount
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
}