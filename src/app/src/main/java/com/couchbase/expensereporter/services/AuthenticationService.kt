package com.couchbase.expensereporter.services

import com.couchbase.expensereporter.models.User

interface AuthenticationService {
    fun getCurrentUser() : User
    fun authenticatedUser(username: String, password: String) : Boolean
    fun logout()
}