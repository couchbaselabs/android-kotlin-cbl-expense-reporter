package com.couchbase.expensereporter.ui.expense
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
            //floatingActionButton = { AddButton(navigateToExpenseEditor) }
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
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}