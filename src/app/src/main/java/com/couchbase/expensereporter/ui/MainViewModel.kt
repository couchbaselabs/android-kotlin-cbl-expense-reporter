package com.couchbase.expensereporter.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.couchbase.expensereporter.data.DatabaseProvider
import com.couchbase.expensereporter.services.AuthenticationService


class MainViewModel(
    private val authService: AuthenticationService,
    private val databaseProvider: DatabaseProvider)
    :ViewModel() {

    val startDatabase: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO) {
            databaseProvider.initializeDatabases(authService.getCurrentUser())
        }
    }

    val closeDatabase: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO) {
            //replicatorService.stopReplication()
            databaseProvider.closeDatabases()
        }
    }
}