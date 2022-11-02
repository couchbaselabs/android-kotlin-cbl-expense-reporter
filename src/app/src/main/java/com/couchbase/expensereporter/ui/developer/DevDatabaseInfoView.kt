package com.couchbase.expensereporter.ui.developer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.couchbase.expensereporter.ui.components.AppBar
import com.couchbase.expensereporter.ui.theme.ExpenseReporterTheme

@Composable
fun DevDatabaseInfoView(
    navigateUp: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: DevDatabaseInfoViewModel) {
    ExpenseReporterTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                AppBar(title = "Developer - Database Information",
                    navigationIcon = Icons.Filled.ArrowBack,
                    navigationOnClick = { navigateUp() })
            })
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            )
            {
                DeveloperInfoWidget(
                    viewModel.reportDatabaseName.value,
                    viewModel.reportDatabaseLocation.value,
                    viewModel.startingDatabaseName.value,
                    viewModel.startingDatabaseLocation.value,
                    viewModel.currentUsername.value,
                    viewModel.currentDepartment.value,
                    viewModel.numberOfUserProfiles.value,
                    viewModel.numberOfReports.value,
                    viewModel.numberOfManagers.value,
                )
            }
        }
    }
}

@Composable
fun DeveloperInfoWidget(
    reportDatabaseName: String,
    reportDatabaseLocation: String?,
    startingDatabaseName: String?,
    startingDatabaseLocation: String?,
    currentUser: String,
    currentDepartment: String,
    numberOfUserProfiles: Int,
    numberOfReports: Int,
    numberOfManagers: Int,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 10.dp)
    ) {
        item {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Username: $currentUser")
            }
        }
        item {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Department: $currentDepartment")
            }
        }
        item {
            Divider(
                color = Color.LightGray,
                thickness = 2.dp,
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
            )
        }
        reportDatabaseLocation?.let {
            item {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Report Database Path", fontWeight = FontWeight.Bold)
                }
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(it)
                }
                Divider(
                    color = Color.LightGray,
                    thickness = 2.dp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                )
            }
        }
        item {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Report Database Name", fontWeight = FontWeight.Bold)
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(reportDatabaseName)
            }
            Divider(
                color = Color.LightGray,
                thickness = 2.dp,
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
            )
        }

        startingDatabaseLocation?.let {
            item {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Starting Database Path", fontWeight = FontWeight.Bold)
                }
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(it)
                }
                Divider(
                    color = Color.LightGray,
                    thickness = 2.dp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                )
            }
        }
        startingDatabaseName?.let {
            item {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Starting Database Name", fontWeight = FontWeight.Bold)
                }
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(it)
                }
                Divider(
                    color = Color.LightGray,
                    thickness = 2.dp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("User Profile Document Count", fontWeight = FontWeight.Bold)
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("$numberOfUserProfiles")
            }
            Divider(
                color = Color.LightGray,
                thickness = 2.dp,
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
            )
        }
        item {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Report Count", fontWeight = FontWeight.Bold)
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("$numberOfReports")
            }
            Divider(
                color = Color.LightGray,
                thickness = 2.dp,
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
            )
        }
        item {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Managers Count", fontWeight = FontWeight.Bold)
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("$numberOfManagers")
            }
            Divider(
                color = Color.LightGray,
                thickness = 2.dp,
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeveloperInfoWidgetPreview() {
    val reportDatabaseName = "inventoryDummy"
    val reportDatabaseLocation = "/blah/inventory"
    val startingDatabaseName = "startingDummy"
    val startingDatabaseLocation = "/blah/starting"
    val currentUser = "demo@example.com"
    val currentDepartment = "Engineering"
    val numberOfUserProfiles = 1000000000
    val numberOfReports = 1000000000
    val numberOfManagers = 100000000

    DeveloperInfoWidget(
        reportDatabaseName = reportDatabaseName,
        reportDatabaseLocation = reportDatabaseLocation,
        startingDatabaseName = startingDatabaseName,
        startingDatabaseLocation = startingDatabaseLocation,
        currentUser =  currentUser,
        currentDepartment = currentDepartment,
        numberOfUserProfiles = numberOfUserProfiles,
        numberOfReports = numberOfReports,
        numberOfManagers = numberOfManagers
    )
}
