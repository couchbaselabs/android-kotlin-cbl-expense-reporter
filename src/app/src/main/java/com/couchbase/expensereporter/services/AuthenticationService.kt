package com.couchbase.expensereporter.services

import androidx.lifecycle.LiveData
import com.couchbase.expensereporter.models.User

interface AuthenticationService {
    val currentUser: LiveData<User?>
    fun getCurrentUser() : User
    fun authenticatedUser(username: String, password: String) : Boolean
    fun logout()
}