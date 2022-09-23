package com.couchbase.expensereporter.ui.developer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.expensereporter.data.report.ReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeveloperViewModel(
    private val reportRepository: ReportRepository
):ViewModel() {
    var toastMessage = mutableStateOf("")

    val onLoadSampleData: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO){ // <1>
            reportRepository.loadSampleData()  // <2>
            toastMessage.value = "Load Sample Data Completed"
        }
    }

    val clearToastMessage: () -> Unit = {
        toastMessage.value = ""
    }
}