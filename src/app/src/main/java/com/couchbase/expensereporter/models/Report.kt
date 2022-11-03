package com.couchbase.expensereporter.models

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

@Keep
@Serializable
data class ReportDao(var item: Report)

@Keep
@Serializable
class Report(
    val reportId: String = "",
    var name: String = "",
    val description: String = "",
    val isComplete: Boolean = false,
    var documentType: String = "report",
    val reportDate: Long = 0,
    val amount: Double = 0.00,
    var status: String = "Draft",
    val department: String = "",
    val createdBy: String = "",
    val approvalManager: Manager? = null,
) {

    fun getReportDateString():String {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = reportDate
        return formatter.format(calendar.time)
    }


    fun copy(
        reportId: String = this.reportId,
        name: String = this.name,
        description: String = this.description,
        isComplete: Boolean = this.isComplete,
        documentType: String = this.documentType,
        reportDate: Long = this.reportDate,
        amount: Double = this.amount,
        status: String = this.status,
        department: String = this.department,
        createdBy: String = this.createdBy,
        approvalManager: Manager? = this.approvalManager
    ) = Report(
        reportId,
        name,
        description,
        isComplete,
        documentType,
        reportDate,
        amount,
        status,
        department,
        createdBy,
        approvalManager
    )

    fun toJson(): String {
        return Json.encodeToString(this)
    }
}


