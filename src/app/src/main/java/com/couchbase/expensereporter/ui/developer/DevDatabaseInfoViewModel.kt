package com.couchbase.expensereporter.ui.developer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.expensereporter.data.KeyValueRepository
import com.couchbase.expensereporter.data.manager.ManagerRepository
import com.couchbase.expensereporter.data.report.ReportRepository
import com.couchbase.expensereporter.services.AuthenticationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DevDatabaseInfoViewModel(
    private val userProfileRepository: KeyValueRepository,
    private val reportRepository: ReportRepository,
    private val managerRepository: ManagerRepository,
    private val authenticationService: AuthenticationService
)
    : ViewModel() {
    private var currentUser = authenticationService.getCurrentUser()
    var reportDatabaseName = mutableStateOf(userProfileRepository.reportDatabaseName())
    var reportDatabaseLocation =
        mutableStateOf(userProfileRepository.reportDatabaseLocation())
    var startingDatabaseName = mutableStateOf(managerRepository.managerDatabaseName)
    var startingDatabaseLocation = mutableStateOf(managerRepository.managerDatabaseLocation)
    var currentDepartment = mutableStateOf("")
    var currentUsername = mutableStateOf("")
    var numberOfUserProfiles = mutableStateOf(0)
    var numberOfReports = mutableStateOf(0)
    var numberOfExpenses = mutableStateOf(0)
    var numberOfManagers = mutableStateOf(0)
    init {
       viewModelScope.launch{
           updateUserProfileInfo()
           updateUserProfileCount()
           updateReportCount()
           updateManagerCount()
       }
    }

    private suspend fun updateUserProfileInfo() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                currentUsername.value = currentUser.username
                currentDepartment.value = currentUser.department

            }
        }
    }

    private suspend fun updateUserProfileCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = userProfileRepository.count()  // <1>
            if (count > 0) {
                withContext(Dispatchers.Main) {
                    numberOfUserProfiles.value = count
                }
            }
        }
    }

    private suspend fun updateManagerCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = managerRepository.count() // <1>
            if (count > 0){
                withContext(Dispatchers.Main){
                    numberOfManagers.value = count
                }
            }
        }
    }

    private suspend fun updateReportCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = reportRepository.count()
            if (count > 0) {
                withContext(Dispatchers.Main) {
                    numberOfReports.value = count
                }
            }
        }
    }

}