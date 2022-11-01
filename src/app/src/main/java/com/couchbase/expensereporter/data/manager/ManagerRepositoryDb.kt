package com.couchbase.expensereporter.data.manager

import android.content.Context
import android.util.Log
import com.couchbase.lite.*
import com.couchbase.lite.Function
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
        TODO("Not yet implemented")
    }

    override suspend fun get(): List<Manager> {
        TODO("Not yet implemented")
    }

    override suspend fun count(): Int {
        TODO("Not yet implemented")
    }


}