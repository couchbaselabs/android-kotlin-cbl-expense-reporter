package com.couchbase.expensereporter.ui.components

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import java.util.*


@Composable
fun AddButton(onClick: (String) -> Unit) {
    FloatingActionButton(
        backgroundColor = MaterialTheme.colors.primary,
        elevation = FloatingActionButtonDefaults.elevation(),
        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
        onClick = {
            onClick(UUID.randomUUID().toString())
        })
    {
        Icon(
            Icons.Default.Add,
            contentDescription = "add",
            tint = Color.White
        )
    }
}


@Composable
fun AddSubItemButton(onNavClick: () -> Unit) {
    FloatingActionButton(
        backgroundColor = MaterialTheme.colors.primary,
        elevation = FloatingActionButtonDefaults.elevation(),
        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
        onClick = onNavClick
    )
    {
        Icon(
            Icons.Default.Add,
            contentDescription = "add",
            tint = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddButtonPreview() {
    AddButton(onClick = {  })
}
