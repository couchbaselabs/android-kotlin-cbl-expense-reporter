package com.couchbase.expensereporter.data.manager

import com.couchbase.expensereporter.models.Manager

interface ManagerRepository {
    val managerDatabaseName: () -> String?
    val managerDatabaseLocation:() -> String?

    suspend fun getByDepartmentTitle(
        department: String,
        title: String?): List<Manager>

    suspend fun get(): List<Manager>

    suspend fun count(): Int
}