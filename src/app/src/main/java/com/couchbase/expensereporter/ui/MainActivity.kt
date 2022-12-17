package com.couchbase.expensereporter.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.couchbase.expensereporter.MainDestinations
import com.couchbase.expensereporter.NavigationGraph
import com.couchbase.expensereporter.data.replicator.ReplicatorProvider
import com.couchbase.expensereporter.services.AuthenticationService
import com.couchbase.expensereporter.ui.components.Drawer
import com.couchbase.expensereporter.ui.profile.UserProfileViewModel
import com.couchbase.expensereporter.ui.theme.ExpenseReporterTheme
import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val profileViewModel: UserProfileViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProvideWindowInsets {
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                val authService: AuthenticationService by inject()
                val replicatorProvider: ReplicatorProvider by inject()
                val menuResource = "btnMenu"

                fun logout() {
                    //todo handle turning off replication
                    authService.logout()
                    replicatorProvider.replicator?.let { replicator ->
                        replicator.stop()
                        replicator.close()
                    }

                }

                //we need a drawer overflow menu on multiple screens
                //so we need top level scaffold.  An event to open the drawer is passed
                //to each screen that needs it.
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val openDrawer = {
                    scope.launch {
                        profileViewModel.updateUserProfileInfo()
                        drawerState.open()
                    }
                }

                ExpenseReporterTheme {
                    Scaffold(scaffoldState = scaffoldState,
                        snackbarHost = {
                            scaffoldState.snackbarHostState
                        }) {
                        ModalDrawer(
                            modifier = Modifier
                                .semantics { contentDescription = menuResource }
                                .padding(it),
                            drawerState = drawerState,
                            gesturesEnabled = drawerState.isOpen,
                            drawerContent = {
                                val givenName = profileViewModel.givenName.value
                                val surname = profileViewModel.surname.value
                                val emailAddress = profileViewModel.emailAddress.value
                                val profilePic = profileViewModel.profilePic.value
                                val department = profileViewModel.department.value

                                Drawer(
                                    modifier = Modifier.semantics {
                                        contentDescription = "{$menuResource}1"
                                    },
                                    firstName = givenName,
                                    lastName = surname,
                                    email = emailAddress,
                                    department = department,
                                    profilePicture = profilePic,
                                    onClicked = { route ->
                                        scope.launch {
                                            drawerState.close()
                                        }
                                        when (route) {
                                            MainDestinations.LOGOUT_ROUTE -> {
                                                logout()
                                                navController.navigate(MainDestinations.LOGIN_ROUTE) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        inclusive = true
                                                    }
                                                }
                                            }
                                            else -> {
                                                navController.navigate(route) {
                                                    popUpTo(navController.graph.startDestinationId)
                                                    launchSingleTop = true
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        ) {
                            MainView(
                                mainViewModel.startDatabase,
                                mainViewModel.closeDatabase
                            )
                            NavigationGraph(
                                openDrawer = { openDrawer() },
                                navController = navController,
                                scaffoldState = scaffoldState,
                                scope = scope
                            )
                        }
                    }
                }
            }
        }
    }

    // **
    // handle lifecycle events when app goes to background and comes back
    // this handles closing and opening the database properly
    // https://developer.android.com/jetpack/compose/side-effects#disposableeffect
    // **
    @Composable
    fun MainView(
        startDatabase: () -> Unit,
        closeDatabase: () -> Unit
    ) {
        val lifecycleOwner = LocalLifecycleOwner.current

        // Safely update the current lambdas when a new one is provided
        val currentOnStart by rememberUpdatedState(startDatabase)
        val currentOnStop by rememberUpdatedState(closeDatabase)

        //if lifecycleOwner changes, dispose and reset the effect
        DisposableEffect(lifecycleOwner) {
            // Create an observer that triggers our remembered callbacks
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        Log.w(
                            "event",
                            "DEBUG:  DANGER, WILL ROBINSON!!  opening the database due to event: ${event.name}"
                        )
                        currentOnStart()
                    }
                    Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                        Log.w(
                            "event",
                            "DEBUG:  DANGER, WILL ROBINSON!!  closing the database due to event: ${event.name}"
                        )
                        currentOnStop()
                    }
                    else -> {
                        Log.w(
                            "event",
                            "DEBUG:  DANGER, WILL ROBINSON!!  Event happened that we don't handle ${event.name}"
                        )
                    }
                }
            }
            // Add the observer to the lifecycle
            lifecycleOwner.lifecycle.addObserver(observer)

            // When the effect leaves the Composition, remove the observer
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        ExpenseReporterTheme {
            MainView(
                { },
                { }
            )
        }
    }
}