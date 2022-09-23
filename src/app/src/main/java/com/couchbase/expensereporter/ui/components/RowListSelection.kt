package com.couchbase.expensereporter.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

@Composable
fun RowListSelection(selection: String,
                     items: List<String>,
                     onSelectionChanged: (String) -> Unit,
                     imageVector: ImageVector
) {

    var expanded by remember { mutableStateOf(false) }
    Row(modifier = Modifier
        .padding(start = 16.dp, end = 16.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
            .padding(top = 10.dp)
            .border(BorderStroke(.5.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.5f)))
            .clickable {
                expanded = true
            }) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                val (label, iconView) = createRefs()

                Text(
                    text = selection,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(label) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(iconView.start)
                            width = Dimension.fillToConstraints
                        }
                )
                Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp, 20.dp)
                        .constrainAs(iconView) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                    tint = MaterialTheme.colors.onSurface
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false })
                {
                    items.forEach {
                        DropdownMenuItem(onClick = {
                            onSelectionChanged(it)
                            expanded = false
                        }) {
                            Text(it)
                        }
                    }
                }
            }
        }
    }
}