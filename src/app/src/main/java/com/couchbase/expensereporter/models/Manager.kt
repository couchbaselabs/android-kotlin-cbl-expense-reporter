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
    val givenName: String = "",
    val surName: String = "",
    val email: String = "",
    val title: String = "",
    var department: String = "") {

    fun toJson(): String {
        return Json.encodeToString(this)
    }
}

