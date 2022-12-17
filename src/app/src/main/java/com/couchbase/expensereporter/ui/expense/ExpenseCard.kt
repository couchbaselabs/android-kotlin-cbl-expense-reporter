@file:OptIn(ExperimentalMaterialApi::class)
package com.couchbase.expensereporter.ui.expense

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
import com.couchbase.expensereporter.models.StandardExpense
import com.couchbase.expensereporter.ui.theme.ExpenseReporterTheme

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

import java.util.*

@Composable
fun ExpenseCard(
    expense: StandardExpense,
    onEditChange: (String, String) -> Unit,
    onDeleteChange: (String) -> Boolean,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .padding(top = 6.dp,
            bottom = 6.dp),
        elevation = 8.dp,
        onClick = {
            onEditChange(expense.reportId, expense.expenseId)
        }
    ){
        Column(
            modifier = Modifier
                .height(160.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .wrapContentWidth(Alignment.Start)
                        .padding(top = 10.dp),
                    text = expense.expenseType,
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
                            onEditChange(expense.reportId, expense.expenseId)
                        }) {
                            Text("Edit")
                        }
                        DropdownMenuItem(onClick = {
                            val results = onDeleteChange(expense.expenseId)
                            expanded = false
                            if (!results) {
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        "The expense was deleted from database",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }) {
                            Text("Delete")
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "",
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colors.onSurface
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text = expense.dateVisible.toString(),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CurrencyExchange,
                    contentDescription = "",
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colors.onSurface
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text = expense.expenseToString(),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface
                )
            }
            Row( modifier = Modifier.fillMaxWidth()) {
                Text(
                    maxLines = 3,
                    modifier = Modifier.padding(top = 10.dp),
                    overflow = TextOverflow.Ellipsis,
                    text = expense.description,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseCardPreview() {
    val expense = StandardExpense(
        reportId = "",
        expenseId = "",
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        amount = 1000000.00,
        documentType = "expense",
        expenseType = "Hotel",
        date = 1668026647
    )
    val onEditChange: (String, String) -> Unit = { _: String, _: String -> }
    val onDeleteChange: (String) -> Boolean  = { _: String -> false }
    val scaffoldState:ScaffoldState = rememberScaffoldState()
    val coRouteScope = rememberCoroutineScope()

    ExpenseReporterTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                com.couchbase.expensereporter.ui.components.AppBar(title = "Expenses",
                    navigationIcon = Icons.Filled.Menu,
                    navigationOnClick = { })
            })
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            )
            {
                ExpenseCard(
                    expense = expense,
                    onEditChange = onEditChange,
                    onDeleteChange = onDeleteChange,
                    scope = coRouteScope,
                    scaffoldState = scaffoldState
                )
            }
        }
    }
}