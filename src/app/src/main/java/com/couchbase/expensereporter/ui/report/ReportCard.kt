@file:OptIn(ExperimentalMaterialApi::class)
package com.couchbase.expensereporter.ui.report

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import kotlinx.coroutines.CoroutineScope

import com.couchbase.expensereporter.models.Report
import com.couchbase.expensereporter.ui.components.AppBar
import com.couchbase.expensereporter.ui.theme.ExpenseReporterTheme
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun ReportCard(report: Report,
               onSelected: (String) -> Unit,
               onEditChange: (String) -> Unit,
               onDeleteChange: (String) -> Boolean,
               onStatusChange: (Report) -> Unit,
               snackBarCoroutineScope: CoroutineScope,
               scaffoldState: ScaffoldState
){
    var expanded by remember { mutableStateOf(false) }
    Card(
        shape = MaterialTheme.shapes.small,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .padding(top = 6.dp, bottom = 6.dp)
            .fillMaxWidth(),
        elevation = 8.dp,
        onClick = {
            onSelected(report.reportId)
        }
    ) {
        Column(
            modifier = Modifier
                .height(180.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically){
                Text(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .wrapContentWidth(Alignment.Start)
                        .padding(top = 10.dp),
                    text = report.name,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically)
                        .wrapContentSize(Alignment.TopEnd)
                )
                {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false })
                    {
                        DropdownMenuItem(onClick = {
                            onEditChange(report.reportId)
                        }) {
                            Text("Edit")
                        }
                        DropdownMenuItem(onClick = {
                            val results = onDeleteChange(report.reportId)
                            expanded = false
                            if (!results) {
                                snackBarCoroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("The report was deleted from database", duration = SnackbarDuration.Short)
                                }
                            }
                        }) {
                            Text("Delete")
                        }
                        if (report.status == "Draft") {
                            DropdownMenuItem(onClick = {
                                //do change status
                                val r = report.copy(status = "In Review")
                                onStatusChange(r)

                                expanded = false
                            }) {
                                Text("Send for Review")
                            }
                        } else if (report.status == "In Review"){
                            DropdownMenuItem(onClick = {
                                //do change status
                                val r = report.copy(status = "Draft")
                                onStatusChange(r)

                                expanded = false
                            }) {
                                Text("Revert to Draft")
                            }
                        }
                    }
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                Icon(
                    Icons.Default.Report,
                    contentDescription = "status",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colors.onSurface)
                Text(modifier = Modifier.padding(start = 6.dp),
                    text = report.status,
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onSurface
                )
            }
            Row(modifier = Modifier
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "",
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colors.onSurface)
                Text(modifier = Modifier.padding(start = 6.dp),
                    text = "Report Date: ${report.getReportDateString()}",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface
                )
            }
            Row(modifier = Modifier
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "",
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colors.onSurface)
                Text(modifier = Modifier.padding(start = 6.dp),
                    text = "Update Date: ${report.getUpdatedDateString()}",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface
                )
            }
            Row(modifier = Modifier
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                Icon(
                    Icons.Default.CurrencyExchange,
                    contentDescription = "",
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colors.onSurface)
                Text(modifier = Modifier.padding(start = 6.dp),
                    text = report.amount.toString(),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface
                )
            }
            Row( modifier = Modifier.fillMaxWidth()) {
                Text(
                    maxLines = 3,
                    modifier = Modifier.padding(top = 10.dp),
                    overflow = TextOverflow.Ellipsis,
                    text = report.description,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ReportCardPreview() {
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
    val onSelected: (String) -> Unit = { _ : String -> }
    val onEditChange: (String) -> Unit = { _ : String -> }
    val onDeleteChange: (String) -> Boolean  = { _: String -> false }
    val onStatusChange: (Report) -> Unit = { }
    val scaffoldState:ScaffoldState = rememberScaffoldState()
    val coRouteScope = rememberCoroutineScope()

    ExpenseReporterTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                AppBar(title = "User Profile",
                    navigationIcon = Icons.Filled.Menu,
                    navigationOnClick = { })
            })
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            )
            {
                ReportCard(
                    report = report,
                    onSelected = onSelected,
                    onEditChange = onEditChange,
                    onDeleteChange = onDeleteChange,
                    onStatusChange = onStatusChange,
                    snackBarCoroutineScope = coRouteScope,
                    scaffoldState = scaffoldState
                )
            }
        }
    }
}