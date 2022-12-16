package com.couchbase.expensereporter.ui.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.couchbase.expensereporter.models.Report
import com.couchbase.expensereporter.ui.components.AppBar
import com.couchbase.expensereporter.ui.components.DatePicker
import com.couchbase.expensereporter.ui.components.HorizontalDottedProgressBar
import com.couchbase.expensereporter.ui.theme.ExpenseReporterTheme
import com.couchbase.expensereporter.ui.theme.Red500

@Composable
fun ReportEditorView(
    viewModel: ReportEditorViewModel,
    navigateUp: () -> Unit,
    navigateToManagerSelection: (String) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
){
    ExpenseReporterTheme {
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                AppBar(
                    title = "Report Editor",
                    navigationIcon = Icons.Filled.ArrowBack,
                    navigationOnClick = { navigateUp() })
            }
        )
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)) {

                viewModel.navigateUpCallback = navigateUp
                viewModel.navigateToManagerSelection = navigateToManagerSelection

                ReportEditor(
                    report = viewModel.reportState.value,
                    approverManagerSelection =  viewModel.approvalManagerSelectedState.value,
                    reportDate = viewModel.reportDateState.value,
                    onNameChange = viewModel.onNameChanged,
                    onDescriptionChange = viewModel.onDescriptionChanged,
                    onDateChanged = viewModel.onDateChanged,
                    onSaveReport = viewModel.onSaveReport,
                    errorMessage = viewModel.errorMessageState.value
                )
            }
        }
    }
}

@Composable
fun ReportEditor(
    report: Report?,
    approverManagerSelection: String,
    reportDate: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChanged: (Long?) -> Unit,
    onSaveReport: (navigateUp: Boolean) -> Unit,
    errorMessage: String
){
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(16.dp)) {
        if (report == null) {
            item {
                HorizontalDottedProgressBar(modifier = Modifier.padding())
            }
        } else {
            item {
                OutlinedTextField(
                    value = report.name,
                    onValueChange = onNameChange,
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = report.description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }
            item {
                DatePicker(selectedDate = reportDate, onDateChanged = onDateChanged)
            }

            item {
                LazyRow(modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start) {
                    item {
                        Text(
                            modifier = Modifier.padding(end = 16.dp),
                            text = "Approval Manager:")}
                    item {
                        TextButton(
                            onClick = {
                                onSaveReport(false)
                            }) {
                            Text(approverManagerSelection,
                                style = TextStyle(textDecoration = TextDecoration.Underline)
                            )
                        }
                    }
                }
            }

            item {
                Column(
                    Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(modifier = Modifier
                        .padding(top = 24.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Red500),
                        onClick = {
                            onSaveReport(true)
                        })
                    {
                        Text("Save",
                            color = Color.White,
                            style = MaterialTheme.typography.h5)
                    }
                }
            }
            if (errorMessage.isNotEmpty()){
                item {
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        text = errorMessage,
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportEditorPreview() {
    val report = Report()
    val onNameChange: (String) -> Unit = {}
    val approveManagerSelectionText = "No Approval Manager Selected"
    val reportDate = "Report Date"
    val onDescriptionChange: (String) -> Unit = { }
    val onSaveReport: (navigateUp: Boolean) -> Unit  =  { }
    val onDateChanged: (Long?) -> Unit = {}
    val errorMessage = ""

    ExpenseReporterTheme {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            ReportEditor(report = report,
                approverManagerSelection = approveManagerSelectionText,
                onNameChange = onNameChange,
                onDescriptionChange = onDescriptionChange,
                onDateChanged = onDateChanged,
                onSaveReport = onSaveReport,
                reportDate = reportDate,
                errorMessage = errorMessage)
        }
    }
}