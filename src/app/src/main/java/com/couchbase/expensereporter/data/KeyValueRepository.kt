package com.couchbase.expensereporter.data

interface KeyValueRepository {

    fun reportDatabaseName(): String
    fun reportDatabaseLocation(): String?

    suspend fun count(): Int
    suspend fun get(currentUser: String): Map<String, Any>
    suspend fun save(data: Map<String, Any>) : Boolean
}