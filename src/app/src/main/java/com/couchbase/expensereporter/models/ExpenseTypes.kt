package com.couchbase.expensereporter.models

data class ExpenseTypesDao(
    val items: ExpenseTypes
)

data class ExpenseTypes (
    val documentId: String,
    val documentType: String,
    val expenseTypes: List<ExpenseType>
)

data class ExpenseType(val name: String,
                       val subTypes: List<String>)