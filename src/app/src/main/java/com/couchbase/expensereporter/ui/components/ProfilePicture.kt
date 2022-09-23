package com.couchbase.expensereporter.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.couchbase.expensereporter.R

@Composable
fun ProfilePicture(
    modifier: Modifier,
    profilePic: Bitmap?
) {
    if (profilePic != null) {
        Image(
            bitmap = profilePic.asImageBitmap(),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.profile_placeholder),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePicturePreview() {
    ProfilePicture(
        modifier = Modifier.padding(),
        profilePic =  null)
}