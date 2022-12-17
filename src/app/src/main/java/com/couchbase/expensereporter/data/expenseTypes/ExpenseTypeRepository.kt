package com.couchbase.expensereporter.data.expenseTypes

import com.couchbase.expensereporter.models.ExpenseTypes

interface ExpenseTypeRepository {
    suspend fun get(): List<ExpenseTypes>
    suspend fun count(): Int
}