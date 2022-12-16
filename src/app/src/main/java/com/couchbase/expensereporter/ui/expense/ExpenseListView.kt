package com.couchbase.expensereporter.ui.expense
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.couchbase.expensereporter.models.StandardExpense
import com.couchbase.expensereporter.ui.components.*
import com.couchbase.expensereporter.ui.theme.ExpenseReporterTheme
import kotlinx.coroutines.CoroutineScope
import java.util.*

@Composable
fun ExpenseListView(
    viewModel: ExpenseListViewModel,
    navigateUp: () -> Unit,
    navigateToExpenseEditor: (String, String) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    scope: CoroutineScope = rememberCoroutineScope())
{
    ExpenseReporterTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                AppBar(title = "Expenses",
                    navigationIcon = Icons.Filled.ArrowBack,
                    navigationOnClick = { navigateUp() })
            },
            floatingActionButton = {
                AddSubItemButton(
                    onNavClick = {
                    navigateToExpenseEditor(viewModel.reportId,
                        UUID.randomUUID().toString())
                })
            }
        )
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            )
            {
                // collecting the flow and turning it into state
                // https://developer.android.com/jetpack/compose/libraries#streams
                if (viewModel.expenseFlow == null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        HorizontalDottedProgressBar(modifier = Modifier.padding())
                    }
                }
                viewModel.expenseFlow?.let {
                    val documents by it.collectAsState(initial = listOf())

                    ExpenseList(
                        items = documents,
                        isLoading = viewModel.isLoading.value,
                        onEditChange = navigateToExpenseEditor,
                        onDeleteChange = viewModel.delete,
                        scaffoldState = scaffoldState,
                        scope = scope
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseList(
    items: List<StandardExpense>,
    isLoading: Boolean,
    onEditChange: (String, String) -> Unit,
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
            items.forEach { expense ->
                item {
                    ExpenseCard(
                        expense = expense,
                        onEditChange = onEditChange,
                        onDeleteChange = onDeleteChange,
                        scope = scope,
                        scaffoldState = scaffoldState
                    )
                    Spacer(modifier = Modifier.padding(top = 30.dp))
                }
            }
        }
    }
}