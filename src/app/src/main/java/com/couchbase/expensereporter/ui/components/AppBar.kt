package com.couchbase.expensereporter.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.couchbase.expensereporter.ui.theme.ExpenseReporterTheme

//used to render the main application bar in the app and show the overflow icon for drawer

@Composable
fun AppBar(title: String = "",
           navigationIcon: ImageVector,
           navigationOnClick: () -> Unit,
           menuAction: (@Composable()() -> Unit)? = null) {
    ExpenseReporterTheme {
        TopAppBar(
            backgroundColor = MaterialTheme.colors.primaryVariant,
            contentColor = Color.White,
            title = {
                Row {
                    Text(text = title)
                }
            },
            navigationIcon = {
                IconButton(
                    modifier = Modifier.semantics { contentDescription = "btnAppBarMenu" },
                    onClick = { navigationOnClick() }){
                    Icon(navigationIcon, contentDescription="")
                }
            },
            actions = {
                menuAction?.let { icon ->
                    icon()
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InventoryAppBarPreview() {
    AppBar(
        title = "Preview",
        navigationIcon = Icons.Filled.Menu,
        navigationOnClick = { },
        menuAction  = null
    )
}

sealed class MenuAction(
    val label: String,
    val icon: ImageVector
){
    object Settings: MenuAction("Settings", Icons.Filled.Settings)
    object Save: MenuAction("Save", Icons.Filled.Save)
}