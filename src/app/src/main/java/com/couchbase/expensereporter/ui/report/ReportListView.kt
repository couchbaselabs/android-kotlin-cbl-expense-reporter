package com.couchbase.expensereporter.ui.report

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.couchbase.expensereporter.models.Report
import com.couchbase.expensereporter.ui.components.AddButton
import com.couchbase.expensereporter.ui.components.AppBar
import com.couchbase.expensereporter.ui.components.HorizontalDottedProgressBar
import com.couchbase.expensereporter.ui.components.NoItemsFound
import com.couchbase.expensereporter.ui.theme.ExpenseReporterTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@Composable
fun ReportListView(
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    navigateToReportEditor: (String) -> Unit,
    navigateToExpenseListByReport: (String) -> Unit,
    viewModel: ReportListViewModel) {
    ExpenseReporterTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                AppBar(title = "Reports",
                    navigationIcon = Icons.Filled.Menu,
                    navigationOnClick = { openDrawer() })
            }, floatingActionButton = { AddButton(navigateToReportEditor) })
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            )
            {
                // collecting the flow and turning it into state
                // https://developer.android.com/jetpack/compose/libraries#streams
                val documents by viewModel.repositoryFlow.collectAsState(initial = listOf())

                ReportList(
                    items = documents,
                    isLoading = viewModel.isLoading.value,
                    onSelected = navigateToExpenseListByReport,
                    onEditChange = navigateToReportEditor,
                    onDeleteChange = viewModel.delete,
                    scaffoldState =  scaffoldState,
                    scope = scope
                )
            }
        }
    }
}

@Composable
fun ReportList(
    items: List<Report>,
    isLoading: Boolean,
    onSelected: (String) -> Unit,
    onEditChange: (String) -> Unit,
    onDeleteChange: (String) -> Boolean,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {

        // changes between state will load super fast on emulator - in a real app
        // probably better to animate between them with a library like shimmer
        if (isLoading && items.isEmpty()) {
            item {
                HorizontalDottedProgressBar(modifier = Modifier.padding())
            }
        } else if (items.isEmpty()) {
            item {
                NoItemsFound(modifier = Modifier.padding())
            }
        } else {
            items.forEach { report ->
                item {
                    ReportCard(
                       report = report,
                        onSelected = onSelected,
                        onEditChange = onEditChange,
                        onDeleteChange = onDeleteChange,
                        snackBarCoroutineScope = scope,
                        scaffoldState = scaffoldState
                    )
                    Spacer(modifier = Modifier.padding(top = 30.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportListPreview() {
    val report = Report(
        reportId = "",
        name = "Test Report",
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        isComplete = false,
        reportDate = 0,
        amount = 0.00,
        status = "Draft",
        department = "Engineering",
        createdBy = "demo@example.com")
    val items = listOf<Report>() + report + report + report
    val onSelected: (String) -> Unit = { _ : String -> }
    val onEditChange: (String) -> Unit = { _ : String -> }
    val onDeleteChange: (String) -> Boolean  = { _: String -> false }
    val scaffoldState:ScaffoldState = rememberScaffoldState()
    val coRouteScope = rememberCoroutineScope()

    ReportList(
        items = items ,
        isLoading = false,
        onSelected = onSelected,
        onEditChange = onEditChange,
        onDeleteChange = onDeleteChange,
        scaffoldState = scaffoldState,
        scope = coRouteScope)

}