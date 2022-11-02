package com.couchbase.expensereporter.models

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Keep
@Serializable
data class ManagerDao(var item: Manager)

@Keep
@Serializable
data class Manager (
    val managerId: String = "",
    val givenName: String = "",
    val surname: String = "",
    val email: String = "",
    val gender: String = "",
    val jobTitle: String = "",
    val department: String = "",
    val documentType: String = "manager") {

    fun toJson(): String {
        return Json.encodeToString(this)
    }
}

