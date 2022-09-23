package com.couchbase.expensereporter.models

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

@Keep
@Serializable
data class StandardExpenseDao(var item: StandardExpense)

@Keep
@Serializable
class StandardExpense(
    val expenseId: String = "",
    val reportId: String = "",
    val description: String = "",
    val date: Long = 0,
    val expenseType: String = "",
    val documentType:String = "expense",
    val amount: Double = 0.00) {

    @kotlinx.serialization.Transient
    lateinit var dateVisible: Date

    init {
        if (date > 0) {
            dateVisible = Date(date)
        }
    }

    fun copy(
        expenseId: String = this.expenseId,
        reportId: String = this.reportId,
        description: String = this.description,
        date: Long = this.date,
        expenseType: String = this.expenseType,
        documentType: String = this.documentType,
        amount: Double = this.amount
    ) = StandardExpense(
        expenseId,
        reportId,
        description,
        date,
        expenseType,
        documentType,
        amount)


    fun toJson(): String {
        return Json.encodeToString(this)
    }
}