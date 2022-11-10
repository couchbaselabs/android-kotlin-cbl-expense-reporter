package com.couchbase.expensereporter.data.expense

import com.couchbase.expensereporter.models.StandardExpense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun getExpenses(reportId: String): Flow<List<StandardExpense>>?
    suspend fun get(reportId: String, expenseId: String): StandardExpense
    suspend fun save(document: StandardExpense)
    suspend fun delete(documentId: String) : Boolean
    suspend fun count(): Int
}