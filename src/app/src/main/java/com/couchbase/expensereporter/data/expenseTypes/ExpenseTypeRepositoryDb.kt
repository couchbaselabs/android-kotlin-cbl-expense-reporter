package com.couchbase.expensereporter.data.expenseTypes

import android.util.Log
import com.couchbase.expensereporter.data.DatabaseProvider
import com.couchbase.expensereporter.models.ExpenseTypeDao
import com.couchbase.expensereporter.models.ExpenseTypes
import com.couchbase.lite.DataSource
import com.couchbase.lite.Expression
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ExpenseTypeRepositoryDb(
    private val databaseProvider: DatabaseProvider
) : ExpenseTypeRepository {

    override suspend fun get(): List<ExpenseTypes> {
        return withContext(Dispatchers.IO) {
            var expenseResults = mutableListOf<ExpenseTypes>()
            try {
                val db = databaseProvider.startingDatabase
                db?.let { database ->
                    val query = QueryBuilder
                        .select(SelectResult.all())
                        .from(DataSource.database(database).`as`("item") )
                        .where(
                            Expression.property("documentType")
                                .equalTo(Expression.string("expenseTypes"))
                        )
                    val results = query.execute().allResults()
                    results.forEach { result ->
                        val json = result.toJSON()
                        val item = Json.decodeFromString<ExpenseTypeDao>(json).item
                        expenseResults.add(item)
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext expenseResults
        }
    }

    override suspend fun count(): Int {
        return withContext(Dispatchers.IO) {
            var resultCount = 0
            try {
                val db = databaseProvider.startingDatabase
                db?.let { database ->
                    val query = QueryBuilder
                        .select(SelectResult.all())
                        .from(DataSource.database(database).`as`("item") )
                        .where(
                            Expression.property("documentType")
                                .equalTo(Expression.string("expenseTypes"))
                        )
                    val results = query.execute().allResults()
                    results.forEach { result ->
                        val json = result.toJSON()
                        val dao = Json.decodeFromString<ExpenseTypeDao>(json).item
                        resultCount = dao.expenseTypes.count()
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext resultCount
        }
    }
}