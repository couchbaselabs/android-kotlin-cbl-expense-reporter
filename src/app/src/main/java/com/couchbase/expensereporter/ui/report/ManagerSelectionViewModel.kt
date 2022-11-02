package com.couchbase.expensereporter.ui.report

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.expensereporter.data.manager.ManagerRepository
import com.couchbase.expensereporter.data.report.ReportRepository
import com.couchbase.expensereporter.models.Manager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManagerSelectionViewModel(
    private val reportRepository: ReportRepository,
    private val managerRepository: ManagerRepository) : ViewModel()
{
    private var reportIdState = mutableStateOf<String?>(null)
    private var isLoading = mutableStateOf(false)

    val searchDepartment = mutableStateOf("")
    val searchTitle = mutableStateOf("")
    val managersState = mutableStateListOf<Manager>()
    var statusMessage = mutableStateOf("No Approval Managers Searched")

    val reportId: (String) -> Unit  = {
        reportIdState.value = it
    }

    val onSearchDepartmentChanged: (String) -> Unit = { newValue ->
        searchDepartment.value = newValue
    }

    val onSearchTitleChanged: (String) -> Unit = { newValue ->
        searchTitle.value = newValue
    }

    val onSearch: () -> Unit = {
        viewModelScope.launch {  // <1>
            if (searchDepartment.value.length >= 2) {  // <2>
                isLoading.value = true
                val managers = managerRepository  // <3>
                    .getByDepartmentTitle(searchDepartment.value, searchTitle.value) // <3>
                if (managers.isNotEmpty()) { // <4>
                    withContext(Dispatchers.Main) {
                        managersState.clear()
                        managersState.addAll(managers)
                        isLoading.value = false
                    }
                } else {  // <5>
                    withContext(Dispatchers.Main) {
                        managersState.clear()
                        statusMessage.value = "No Approval Managers Found"
                        isLoading.value = false
                    }
                }
            }
        }
    }

    fun onManagerSelected(manager: Manager) {
        reportIdState.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                reportRepository.updateManager(it, manager)
            }
        }
    }


}