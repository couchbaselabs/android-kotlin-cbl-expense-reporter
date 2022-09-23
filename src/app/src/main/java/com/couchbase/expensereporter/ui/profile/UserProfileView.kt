package com.couchbase.expensereporter.ui.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.couchbase.expensereporter.ui.components.AppBar
import com.couchbase.expensereporter.ui.components.ProfilePicture
import com.couchbase.expensereporter.ui.profile.UserProfileViewModel
import com.couchbase.expensereporter.ui.theme.ExpenseReporterTheme
import com.couchbase.expensereporter.ui.theme.Red500

@Composable
fun UserProfileView(
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: UserProfileViewModel
) {

    ExpenseReporterTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                AppBar(title = "User Profile",
                    navigationIcon = Icons.Filled.Menu,
                    navigationOnClick = { openDrawer() })
            })
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            )
            {
                UserProfileFormWidget(
                    viewModel.givenName.value,
                    viewModel.onGivenNameChanged,
                    viewModel.surname.value,
                    viewModel.onSurnameChanged,
                    viewModel.jobTitle.value,
                    viewModel.onJobTitleChanged,
                    viewModel.profilePic.value,
                    viewModel.onProfilePicChanged,
                    viewModel.emailAddress.value,
                    viewModel.department.value,
                    viewModel.toastMessage.value,
                    viewModel.onSave,
                    viewModel.clearToastMessage
                )
            }
        }
    }
}

@Composable
fun UserProfileFormWidget(
    firstName: String,
    onFirstNameChanged: (String) -> Unit,
    lastName: String,
    onLastNameChanged: (String) -> Unit,
    jobTitle: String,
    onJobTitleChanged: (String) -> Unit,
    profilePic: Bitmap?,
    onProfilePicChanged: (Bitmap) -> Unit,
    email: String,
    department: String,
    toastMessage: String?,
    onSave: () -> Unit,
    clearToastMessage: () -> Unit
) {
    //****
    //used for tests - set in the semantics - contentDescription
    //https://developer.android.com/jetpack/compose/testing
    //****
    val emailResource = "email"
    val firstNameResource = "givenName"
    val lastNameResource = "surname"
    val jobTitleResource = "jobTitle"
    val saveResource = "save"

    //hide the keyboard when we are done editing
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 1.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        ProfilePictureSelector(profilePic, onProfilePicChanged)

        Text(
            modifier = Modifier
                .padding(top = 8.dp, start = 24.dp, end = 24.dp, bottom = 4.dp)
                .semantics { contentDescription = emailResource },
            text = email
        )

        Text(
            modifier = Modifier
                .padding(top = 4.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
            text = "Department: $department"
        )

        OutlinedTextField(
            modifier = Modifier.semantics { contentDescription = firstNameResource },
            value = firstName,
            onValueChange = { onFirstNameChanged(it) },
            label = { Text("First Name") },
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Text
            )
        )

        OutlinedTextField(
            modifier = Modifier.semantics { contentDescription = lastNameResource },
            value = lastName,
            onValueChange = { onLastNameChanged(it) },
            label = { Text("Last Name") },
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Text
            )
        )

        OutlinedTextField(
            modifier = Modifier.semantics { contentDescription = jobTitleResource },
            value = jobTitle,
            onValueChange = { onJobTitleChanged(it) },
            label = { Text("Job Title") },
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Text
            )
        )

        Button(
            modifier = Modifier
                .padding(top = 32.dp)
                .semantics { contentDescription = saveResource },
            colors = ButtonDefaults.buttonColors(backgroundColor = Red500),
            onClick = {
                onSave()
            })
        {
            Text(
                "Save",
                style = MaterialTheme.typography.h5,
                color = Color.White
            )
        }

        toastMessage?.let {
            if (it.isNotEmpty()) {
                Toast.makeText(LocalContext.current, it, Toast.LENGTH_SHORT).show()
                clearToastMessage()
            }
        }
    }
}

@Composable
fun ProfilePictureSelector(
    profilePic: Bitmap?,
    onProfilePicChanged: (Bitmap) -> Unit
) {
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    //used to get the image from a media picker control
    //https://developer.android.com/reference/androidx/activity/result/contract/ActivityResultContracts.GetContent
    //would love to use the new photo picker API but it only supports API > 30
    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    ProfilePicture(modifier = Modifier
        .size(128.dp)
        .clip(CircleShape)
        .border(1.dp, Color.Red, CircleShape)
        .clickable {
            launcher.launch("image/*")
        }, profilePic)

    //images work different in older versions of the android SDK
    //requires different code to get the image
    imageUri?.let {
        if (Build.VERSION.SDK_INT < 28) {
            @Suppress("DEPRECATION")
            onProfilePicChanged(
                MediaStore.Images
                    .Media.getBitmap(LocalContext.current.contentResolver, it)
            )
        } else {
            val source = ImageDecoder
                .createSource(LocalContext.current.contentResolver, it)
            onProfilePicChanged(ImageDecoder.decodeBitmap(source))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileWidgetPreview() {
    val onGivenNameChange: (String) -> Unit = {}
    val onSurnameChange: (String) -> Unit = {}
    val onJobTitleChange: (String) -> Unit = {}
    val onProfilePicChanged: (Bitmap) -> Unit = {}

    UserProfileFormWidget(
        "Bob",
        onGivenNameChange,
        "Smith",
        onSurnameChange,
        "Sr. Android Developer",
        onJobTitleChanged = onJobTitleChange,
        null,
        onProfilePicChanged,
        "demo@example.com",
        "Engineering",
        null,
        {},
        {})
}