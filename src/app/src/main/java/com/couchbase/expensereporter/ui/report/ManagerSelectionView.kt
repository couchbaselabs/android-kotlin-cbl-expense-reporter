package com.couchbase.expensereporter.ui.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.couchbase.expensereporter.models.Manager
import com.couchbase.expensereporter.ui.components.AppBar
import com.couchbase.expensereporter.ui.theme.ExpenseReporterTheme
import com.couchbase.expensereporter.ui.theme.Red500

@Composable
fun ManagerSelectionView(
    viewModel: ManagerSelectionViewModel,
    navigateUp: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()){
    ExpenseReporterTheme {
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                AppBar(
                    title = "Select Approval Manager",
                    navigationIcon = Icons.Filled.ArrowBack,
                    navigationOnClick = { navigateUp() })
            }
        )
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ){

                val onManagerSelected: (Manager) -> Unit  = { manager ->
                    viewModel.onManagerSelected(manager)
                    navigateUp()
                }

                ManagerSelector(
                    searchDepartment = viewModel.searchDepartment.value,
                    searchTitle = viewModel.searchTitle.value,
                    onSearchDepartmentChanged = viewModel.onSearchDepartmentChanged,
                    onSearchTitleChanged = viewModel.onSearchTitleChanged,
                    onSearch = viewModel.onSearch,
                    statusMessage = viewModel.statusMessage.value,
                    managers = viewModel.managersState,
                    onManagerSelected = onManagerSelected
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ManagerSelector(
    searchDepartment: String,
    searchTitle: String,
    onSearchDepartmentChanged: (String) -> Unit,
    onSearchTitleChanged: (String) -> Unit,
    onSearch: () -> Unit,
    statusMessage: String,
    managers: List<Manager>,
    onManagerSelected: (Manager) -> Unit
){
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp)) {
        item {
            OutlinedTextField(
                value = searchDepartment,
                onValueChange = onSearchDepartmentChanged,
                label = { Text("Department") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
            )
        }
        item {
            OutlinedTextField(
                value = searchTitle,
                onValueChange = onSearchTitleChanged,
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
            )
        }
        item {
            Column(
                Modifier
                    .padding(bottom = 12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(modifier = Modifier
                    .padding(top = 4.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Red500),
                    onClick = {
                        onSearch()
                    })
                {
                    Text("Search",
                        color = Color.White,
                        style = MaterialTheme.typography.h5)
                }
            }
        }
        if (managers.isNotEmpty()) {
            managers.forEach { manager ->
                item {
                    ManagerCard(
                        manager = manager,
                        onManagerSelected = onManagerSelected
                    )
                }
            }
        } else {
            item {
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = statusMessage,
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
private fun ManagerSelectorPreview() {
    val manager = Manager(
        managerId = "man001",
        givenName = "John",
        surname = "Doe",
        email = "john.doe@example.com",
        gender = "Male",
        jobTitle = "Manager",
        department = "Engineering",
    )
    val onManagerSelected: (Manager) -> Unit  = { }
    val searchDepartment = ""
    val searchTitle = ""
    val onSearchDepartmentChanged: (String) -> Unit = {}
    val onSearchTitleChanged: (String) -> Unit = {}
    val onSearch: () -> Unit = { }
    val statusMessage = ""
    val managerList = listOf<Manager>() + manager + manager + manager
    ExpenseReporterTheme {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            ManagerSelector(
                searchDepartment = searchDepartment,
                searchTitle = searchTitle,
                onSearchDepartmentChanged = onSearchDepartmentChanged,
                onSearchTitleChanged = onSearchTitleChanged,
                onSearch = onSearch,
                statusMessage = statusMessage,
                managers = managerList,
                onManagerSelected = onManagerSelected
            )
        }
    }
}