package com.couchbase.expensereporter.data.expenseTypes

import com.couchbase.expensereporter.models.ExpenseTypes
import com.couchbase.expensereporter.models.StandardExpense
import kotlinx.coroutines.flow.Flow

interface ExpenseTypeRepository {
    suspend fun get(): List<ExpenseTypes>
    suspend fun count(): Int
}