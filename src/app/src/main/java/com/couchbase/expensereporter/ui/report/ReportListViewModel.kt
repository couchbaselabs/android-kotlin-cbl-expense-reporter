package com.couchbase.expensereporter.ui.report

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.expensereporter.data.report.ReportRepository
import com.couchbase.expensereporter.models.Report
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ReportListViewModel(repository: ReportRepository)
    : ViewModel() {

    var isLoading = mutableStateOf(false)

    // create a flow to return the results dynamically as needed - more information on CoRoutine Flows can be found at
    // https://developer.android.com/kotlin/flow
    var repositoryFlow: Flow<List<Report>> = repository.getDocuments()

    val delete: (String) -> Boolean = { documentId: String ->
        var didDelete = false
        viewModelScope.launch(Dispatchers.IO){
            didDelete = repository.delete(documentId)
        }
        didDelete
    }

}