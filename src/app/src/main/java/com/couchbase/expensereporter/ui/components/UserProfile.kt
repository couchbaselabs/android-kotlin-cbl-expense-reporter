package com.couchbase.expensereporter.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserProfile(
    modifier: Modifier,
    firstName: String?,
    lastName: String?,
    email: String?,
    department: String?,
    profilePicture: Bitmap?,
) {
    Row(
        modifier = modifier.padding(start = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        ProfilePicture(modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .border(0.5.dp, Color.Red, CircleShape)
            , profilePicture)
        LazyColumn(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        ){
            firstName?.let {
                item {
                    Text(
                        modifier = Modifier
                            .padding(top = 8.dp, start = 4.dp, end = 24.dp, bottom = 2.dp),
                        text = "$it $lastName",
                        fontSize = 14.sp
                    )
                }
            }
            item {
                Text(
                    modifier = Modifier
                        .padding(top = 2.dp, start = 4.dp, end = 24.dp, bottom = 1.dp),
                    text = "$email",
                    fontSize = 10.sp
                )
            }
            item {
                Text(
                    modifier = Modifier
                        .padding(top = 1.dp, start = 4.dp, end = 24.dp, bottom = 4.dp),
                    text = "Department: $department",
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfilePreview() {
    val firstName = "Jane"
    val lastName = "Doe"
    val email = "demo@example.com"
    val department = "Engineering"
    val profilePicture = null

    UserProfile(
        modifier = Modifier.padding(),
        firstName = firstName,
        lastName = lastName,
        email = email,
        department = department,
        profilePicture = profilePicture
    )
}