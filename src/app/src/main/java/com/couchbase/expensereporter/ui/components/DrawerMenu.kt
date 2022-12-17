package com.couchbase.expensereporter.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.couchbase.expensereporter.MainDestinations
import com.couchbase.expensereporter.ui.theme.ExpenseReporterTheme


//used to draw menu of items in the drawer
sealed class DrawerMenu(val title: String, val route: String, val imageVector: ImageVector){
    object Home: DrawerMenu("Home", MainDestinations.REPORT_LIST_ROUTE, Icons.Filled.Home)
    object Developer: DrawerMenu("Developer", MainDestinations.DEVELOPER_ROUTE, Icons.Filled.DeveloperMode)
    object Logout: DrawerMenu("Logout", MainDestinations.LOGOUT_ROUTE, Icons.Filled.Logout)
}

//creating a list of things to draw in the overflow menu
private val menu = listOf(
    DrawerMenu.Home,
    DrawerMenu.Developer,
    DrawerMenu.Logout)


//renders the drawer for the overflow menu
@Composable
fun Drawer(
    modifier: Modifier = Modifier,
    firstName: String?,
    lastName: String?,
    email: String?,
    department: String?,
    profilePicture: Bitmap?,
    onClicked: (route: String) -> Unit
)
{
    Column(
        modifier
            .fillMaxSize()
    ){
        Box(modifier = Modifier
            .background(color = MaterialTheme.colors.primaryVariant)
            .fillMaxWidth()
            .height(40.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically)
            {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    text = "Expense Report",
                    style = MaterialTheme.typography.h5,
                    color = Color.White)
            }
        }
        Column(
            modifier
                .padding(start = 24.dp, top = 20.dp)
        )
        {
            UserProfile(
                modifier = Modifier.fillMaxWidth(),
                firstName = firstName,
                lastName = lastName,
                email = email,
                department = department,
                profilePicture = profilePicture
            )
            Row(modifier = Modifier
                .padding(top = 1.dp, bottom = 5.dp)
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start) {
                TextButton(
                    onClick = {
                        onClicked(MainDestinations.USERPROFILE_ROUTE)
                    }){
                    Text(
                        "Update User Profile",
                        color = if(  isSystemInDarkTheme())  {
                            Color.White } else { Color.Black },
                        style = TextStyle(textDecoration = TextDecoration.Underline)
                    )
                }
            }
            Divider(
                color = Color.LightGray,
                thickness = 2.dp,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, end = 24.dp)
            )
            menu.forEach { menuItem ->
                Spacer(Modifier.height(24.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = menuItem.title }
                    .clickable { onClicked(menuItem.route) } ,
                    verticalAlignment = Alignment.CenterVertically )
                {
                    Icon(
                        menuItem.imageVector,
                        contentDescription = "",
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = menuItem.title,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .semantics { contentDescription = menuItem.title }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun DrawerPreview() {
    val onClicked: (String) -> Unit = { _ -> }
    ExpenseReporterTheme {
        Drawer(
            modifier = Modifier.padding(),
            firstName =  "Jane",
            lastName =  "Doe",
            email = "demo@example.com",
            department = "Engineering",
            profilePicture = null,
            onClicked = onClicked
        )
    }
}