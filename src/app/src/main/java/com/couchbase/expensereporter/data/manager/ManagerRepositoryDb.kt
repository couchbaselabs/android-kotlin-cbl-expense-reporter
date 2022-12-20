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
            val items = mutableListOf<Manager>()
            try {
                val db = databaseProvider.startingDatabase
                db?.let { database ->
                    val queryString = StringBuilder("SELECT * FROM _ AS item WHERE documentType=\"manager\" AND lower(department) LIKE ('%' || \$parameterDepartment || '%')")

                    val parameters = Parameters()
                    parameters.setValue("parameterDepartment", department.lowercase())

                    title?.let { titleScope ->
                        if (titleScope.isNotEmpty()) {
                            queryString.append(" AND lower(jobTitle) LIKE ('%' || \$parameterJobTitle || '%')")
                            parameters.setValue("parameterJobTitle", titleScope.lowercase())
                        }
                    }
                    queryString.append(" ORDER BY surname, givenName")

                    val query = database.createQuery(queryString.toString())
                    query.parameters = parameters

                    val results = query.execute()
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
            val items = mutableListOf<Manager>()
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