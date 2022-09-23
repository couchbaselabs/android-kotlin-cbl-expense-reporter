package com.couchbase.expensereporter.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NoItemsFound (modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.align(Alignment.Center)){
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                text = "No documents found in the database",
                style = MaterialTheme.typography.h5
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoItemsFoundPreview() {
    NoItemsFound(
        modifier = Modifier.padding())
}