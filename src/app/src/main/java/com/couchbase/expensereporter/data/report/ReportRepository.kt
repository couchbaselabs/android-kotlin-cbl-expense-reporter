package com.couchbase.expensereporter.data.report

import com.couchbase.expensereporter.models.Manager
import com.couchbase.expensereporter.models.Report
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    val databaseName: String

    suspend fun get(documentId: String): Report
    fun getDocuments(): Flow<List<Report>>
    suspend fun save(document: Report) : Boolean
    suspend fun delete(documentId: String): Boolean
    suspend fun count(): Int
    suspend fun updateManager(documentId: String, manager: Manager)
    suspend fun loadSampleData()
}