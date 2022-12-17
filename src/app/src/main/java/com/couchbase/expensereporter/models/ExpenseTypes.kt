package com.couchbase.expensereporter.models

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ExpenseTypeDao(
    val item: ExpenseTypes
)

@Keep
@Serializable
data class ExpenseTypes (
    val documentId: String,
    val documentType: String,
    val expenseTypes: List<ExpenseType>
)

@Keep
@Serializable
data class ExpenseType(val name: String,
                       val subTypes: List<String>)