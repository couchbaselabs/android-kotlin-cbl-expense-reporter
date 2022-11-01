package com.couchbase.expensereporter.data.manager

import android.content.Context
import android.util.Log
import com.couchbase.lite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.Exception

import com.couchbase.expensereporter.data.DatabaseProvider
import com.couchbase.expensereporter.models.ManagerDao
import com.couchbase.expensereporter.models.Manager

class ManagerRepositoryDb(context: Context)
    : ManagerRepository {
    private val databaseProvider: DatabaseProvider = DatabaseProvider.getInstance(context)

    override val managerDatabaseName: () -> String? = {
        databaseProvider.startingDatabase?.name
    }
    override val managerDatabaseLocation: () -> String? = {
        databaseProvider.startingDatabase?.path
    }

    override suspend fun getByDepartmentTitle(department: String, title: String?): List<Manager> {
        return withContext(Dispatchers.IO) {
            var items = mutableListOf<Manager>()
            try {
                val db = databaseProvider.startingDatabase
                db?.let { database ->
                    var queryString = "SELECT * FROM _ AS item WHERE documentType=\"manager\" AND department=\$department"

                    val parameters = Parameters()  // <2>
                    parameters.setValue("department", department)

                    title?.let { // <3>
                        queryString += " AND title=\$title"
                        parameters.setValue("title", it)
                    }

                    val query = database.createQuery(queryString) // <4>
                    query.parameters = parameters; // <5>

                    query.execute()
                        .allResults()
                        .forEach { item ->  // <6>
                            val json = item.toJSON()
                            val manager = Json.decodeFromString<ManagerDao>(json).item
                            items.add(manager)
                        }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext items
        }
    }

    override suspend fun get(): List<Manager> {
        return withContext(Dispatchers.IO) {
            var items = mutableListOf<Manager>()
            try {
                val db = databaseProvider.startingDatabase
                db?.let { database ->
                    database
                        .createQuery("SELECT * FROM _ AS item WHERE documentType=\"manager\"")
                        .execute()
                        .allResults()
                        .forEach { item -> //<2>
                            val json = item.toJSON()
                            val manager = Json.decodeFromString<ManagerDao>(json).item
                            items.add(manager)
                        }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext items
        }
    }

    override suspend fun count(): Int {
        return withContext(Dispatchers.IO) {
            var count = 0
            try {
                val db = databaseProvider.startingDatabase
                db?.let { database ->
                    val query =
                        database.createQuery("SELECT COUNT(*) AS count FROM _ AS item WHERE documentType=\"manager\"") // <1>
                    val results = query.execute().allResults() // <2>
                    count = results[0].getInt("count") // <3>
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext count
        }
    }
}