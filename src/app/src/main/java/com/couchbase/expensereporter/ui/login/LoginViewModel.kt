package com.couchbase.expensereporter.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.expensereporter.data.DatabaseProvider
import com.couchbase.expensereporter.data.replicator.ReplicatorProvider
import com.couchbase.expensereporter.services.AuthenticationService
import com.couchbase.lite.ReplicatorActivityLevel
import com.couchbase.lite.ReplicatorStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authenticationService: AuthenticationService,
    private val databaseProvider: DatabaseProvider
) : ViewModel() {

    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username
    val onUsernameChanged: (String) -> Unit = { newValue ->
        _isError.value = false
        _username.value = newValue
    }

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password
    val onPasswordChanged: (String) -> Unit = { newValue ->
        _isError.value = false
        _password.value = newValue
    }

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    fun login(): Boolean {
        _username.value?.let { uname ->
            _password.value?.let { pwd ->
                if (authenticationService.authenticatedUser(username = uname, password = pwd)) {
                    _isError.value = false
                    val currentUser = authenticationService.getCurrentUser()
                    viewModelScope.launch(Dispatchers.IO) {
                        //initialize database if needed
                        databaseProvider.initializeDatabases(currentUser)
                    }
                    return true
                }
            }
        }
        _isError.value = true
        return false
    }
}