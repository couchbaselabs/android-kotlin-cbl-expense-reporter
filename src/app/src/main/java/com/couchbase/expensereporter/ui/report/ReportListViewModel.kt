package com.couchbase.expensereporter.ui.report

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.expensereporter.data.report.ReportRepository
import com.couchbase.expensereporter.models.Report
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ReportListViewModel(private val repository: ReportRepository)
    : ViewModel() {

    var isLoading = mutableStateOf(false)

    // create a flow to return the results dynamically as needed - more information on CoRoutine Flows can be found at
    // https://developer.android.com/kotlin/flow
    var reportFlow: Flow<List<Report>>? = null
    init {
        viewModelScope.launch(Dispatchers.IO) {
            reportFlow = repository.getDocuments()
        }
    }

    val statusChange: (Report) -> Unit = { report ->
        viewModelScope.launch(Dispatchers.IO){
            repository.save(report)
        }
    }

    val delete: (String) -> Boolean = { documentId: String ->
        var didDelete = false
        viewModelScope.launch(Dispatchers.IO){
            didDelete = repository.delete(documentId)
        }
        didDelete
    }

}