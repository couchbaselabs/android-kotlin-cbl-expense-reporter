package com.couchbase.expensereporter.ui.report

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.couchbase.expensereporter.models.Manager
import com.couchbase.expensereporter.ui.components.AppBar
import com.couchbase.expensereporter.ui.theme.ExpenseReporterTheme

@ExperimentalMaterialApi
@Composable
fun ManagerCard(
    manager: Manager,
    onManagerSelected: (Manager) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.small,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .padding(top = 6.dp, bottom = 6.dp)
            .fillMaxWidth(),
        elevation = 8.dp,
        onClick = {
            onManagerSelected(manager)
        })
    {
        Column(
            modifier = Modifier
                .height(120.dp)
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
                        .padding(top = 4.dp),
                    text = "${manager.givenName} ${manager.surname}",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    maxLines = 3,
                    modifier = Modifier.padding(top = 2.dp),
                    overflow = TextOverflow.Ellipsis,
                    text = manager.jobTitle,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    maxLines = 3,
                    modifier = Modifier.padding(top = 2.dp),
                    overflow = TextOverflow.Ellipsis,
                    text = manager.department,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun ManagerCardPreview() {
    val manager = Manager(
        managerId = "man001",
        givenName = "John",
        surname = "Doe",
        jobTitle = "Sr. Developer",
        gender = "Male",
        department = "Engineering",
        email = "john.doe@example.com"
    )
    val onManagerSelected: (Manager) -> Unit = { _: Manager -> }
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    ExpenseReporterTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
        topBar = {
            AppBar(title = "Manager Selection",
                navigationIcon = Icons.Filled.Menu,
                navigationOnClick = {})
        })
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            )
            {
                ManagerCard(
                    manager = manager,
                    onManagerSelected = onManagerSelected
                )
            }
        }
    }
}