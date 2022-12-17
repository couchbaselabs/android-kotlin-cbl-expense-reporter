package com.couchbase.expensereporter.data.manager

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

class ManagerRepositoryDb(private val databaseProvider: DatabaseProvider)
    : ManagerRepository {

    override val managerDatabaseName: String?
        get() = databaseProvider.startingDatabase?.name

    override val managerDatabaseLocation: String?
        get() = databaseProvider.startingDatabase?.path

    override suspend fun getByDepartmentTitle(department: String, title: String?): List<Manager> {
        return withContext(Dispatchers.IO) {
            var items = mutableListOf<Manager>()
            try {
                val db = databaseProvider.startingDatabase
                db?.let { database ->
                    var queryString = StringBuilder("SELECT * FROM _ AS item WHERE documentType=\"manager\" AND lower(department) LIKE ('%' || \$department || '%')")

                    val parameters = Parameters()
                    parameters.setValue("department", department.lowercase())

                    title?.let { titleScope ->
                        if (titleScope.isNotBlank()) {
                            queryString.append(" AND lower(title) LIKE ('%' || \$title || '%')")
                            parameters.setValue("title", titleScope.lowercase())
                        }
                    }

                    val query = database.createQuery(queryString.toString())
                    query.parameters = parameters

                    var results = query.execute()
                        .allResults()

                    results
                        .forEach { item ->
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
                    val results = query.execute().allResults()
                    count = results[0].getInt("count")
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext count
        }
    }
}