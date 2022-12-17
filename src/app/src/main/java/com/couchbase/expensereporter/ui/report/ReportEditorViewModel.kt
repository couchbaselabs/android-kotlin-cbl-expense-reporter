package com.couchbase.expensereporter.ui.report

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.expensereporter.data.report.ReportRepository
import com.couchbase.expensereporter.models.Report
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ReportEditorViewModel(private val repository: ReportRepository)
    : ViewModel() {
    var reportState = mutableStateOf<Report?>(null)

    val reportDateState = mutableStateOf("Select Report Date")
    val approvalManagerSelectedState = mutableStateOf("No Manager Selected")
    val errorMessageState = mutableStateOf("")
    var navigateUpCallback: () -> Unit  = { }
    var navigateToManagerSelection: (String) -> Unit = { }

    val documentId: (String) -> Unit = {
        viewModelScope.launch {
            val report = repository.get(it)
            withContext(Dispatchers.Main) {
                reportState.value = report

                val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                reportDateState.value = formatter.format(report.reportDate)

                if (report.approvalManager != null){
                    report.approvalManager?.let{
                        approvalManagerSelectedState.value = "${it.givenName} ${it.surname}"
                    }
                } else {
                    approvalManagerSelectedState.value = "No Manager Selected"
                }
            }
        }
    }

    private fun dateFormatter(milliseconds: Long): String {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        return formatter.format(calendar.time)
    }

    val onDateChanged: (Long?) -> Unit = { date ->
        date?.let { theDate ->
            reportDateState.value = dateFormatter(theDate)
            val r = reportState.value?.copy(reportDate = date)
            reportState.value = r
        }
    }

    val onNameChanged: (String) -> Unit = { newValue ->
        val r = reportState.value?.copy(name = newValue)
        reportState.value = r
    }

    val onDescriptionChanged: (String) -> Unit = { newValue ->
        val r = reportState.value?.copy(description = newValue)
        reportState.value = r
    }

    val onSaveReport: (navigateUp: Boolean) -> Unit = { navigateUp ->
        viewModelScope.launch(Dispatchers.IO) {
            reportState.value?.let { report ->
                if (report.name.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        errorMessageState.value = "Error:  Must enter a name before continuing"
                    }
                } else {
                    errorMessageState.value = ""
                    //save value than figure out which place to navigate to, either main report list
                    //or to the list selection screen
                    repository.save(report)

                    withContext(Dispatchers.Main) {
                        if (navigateUp) {
                            navigateUpCallback()
                        } else {
                            navigateToManagerSelection(report.reportId)
                        }
                    }
                }
            }
        }
    }
}