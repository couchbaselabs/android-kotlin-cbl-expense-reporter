package com.couchbase.expensereporter.models

data class User (
    var username: String = "",
    var password: String = "",
    var department: String = ""
)