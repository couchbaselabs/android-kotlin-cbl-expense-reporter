package com.couchbase.expensereporter.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import java.lang.ref.WeakReference

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.couchbase.expensereporter.data.DatabaseProvider
import com.couchbase.expensereporter.services.AuthenticationService


class MainViewModel(
    private val authService: AuthenticationService,
    val context: WeakReference<Context>)
    :ViewModel() {

    val startDatabase: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO) {
            context.get()?.let {
                DatabaseProvider.getInstance(it).initializeDatabases(authService.getCurrentUser())
            }
        }
    }

    val closeDatabase: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO) {
            context.get()?.let {
                //replicatorService.stopReplication()
                DatabaseProvider.getInstance(it).closeDatabases()
            }
        }
    }
}