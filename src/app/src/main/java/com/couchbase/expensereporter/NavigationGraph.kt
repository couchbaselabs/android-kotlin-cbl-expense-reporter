package com.couchbase.expensereporter

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.couchbase.expensereporter.ui.developer.DevDatabaseInfoView
import com.couchbase.expensereporter.ui.developer.DevDatabaseInfoViewModel
import com.couchbase.expensereporter.ui.developer.DeveloperView
import com.couchbase.expensereporter.ui.developer.DeveloperViewModel
import com.couchbase.expensereporter.ui.login.LoginView
import com.couchbase.expensereporter.ui.login.LoginViewModel
import com.couchbase.expensereporter.ui.profile.UserProfileView
import com.couchbase.expensereporter.ui.profile.UserProfileViewModel
import com.couchbase.expensereporter.ui.report.ReportEditorView
import com.couchbase.expensereporter.ui.report.ReportEditorViewModel
import com.couchbase.expensereporter.ui.report.ReportListView
import com.couchbase.expensereporter.ui.report.ReportListViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.compose.getViewModel
import java.util.*

object MainDestinations {
    const val LOGIN_ROUTE = "login"
    const val USERPROFILE_ROUTE = "userprofile"
    const val REPORT_LIST_ROUTE = "report_list"
    const val DEVELOPER_ROUTE = "developer"
    const val DEVELOPER_DATABASE_INFO_ROUTE = "developer_database_info"
    const val LOGOUT_ROUTE = "logout"

    const val REPORT_EDITOR_ROUTE = "report_editor"
    const val REPORT_EDITOR_ROUTE_PATH = "report_editor/{reportId}"
    const val REPORT_KEY_ID = "reportId"

    const val EXPENSE_LIST_ROUTE_PATH = "expenseList/{report}"
    const val EXPENSE_LIST_ROUTE = "expenseList"
    const val EXPENSE_LIST_KEY_ID = "report"

    const val EXPENSE_EDITOR_ROUTE_PATH = "expenseEditor/{reportId}/{expense}"
    const val EXPENSE_EDITOR_ROUTE = "expenseEditor"
    const val EXPENSE_EDITOR_KEY_ID = "expense"
}

@Composable
fun NavigationGraph (
    openDrawer: () -> Unit,
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = MainDestinations.LOGIN_ROUTE) {

    val actions = remember(navController) { MainActions(navController) }
    NavHost(navController = navController,
        startDestination = startDestination) {

        composable(MainDestinations.LOGIN_ROUTE) {
            LoginView(onSuccessLogin = {
                actions.navigateToReportListView()
            },
                viewModel = getViewModel<LoginViewModel>()
            )
        }

        composable(MainDestinations.REPORT_LIST_ROUTE) {
            ReportListView(
                openDrawer = openDrawer,
                scaffoldState = scaffoldState,
                scope = scope,
                navigateToReportEditor = actions.navigateToReportEditor,
                navigateToExpenseListByReport = actions.navigateToExpenseListByReport,
                viewModel = getViewModel<ReportListViewModel>())
        }

        composable(MainDestinations.REPORT_EDITOR_ROUTE_PATH ) { backstackEntry ->
            val documentId = backstackEntry.arguments?.getString(MainDestinations.REPORT_KEY_ID)
            val viewModel = getViewModel<ReportEditorViewModel>()
            if (documentId == null){
                viewModel.documentId(UUID.randomUUID().toString())
            }
            else {
                viewModel.documentId(documentId)
            }
            ReportEditorView(
                viewModel = viewModel,
                navigateToManagerSelection = { },
                navigateUp = actions.upPress,
                scaffoldState = scaffoldState)
        }

        composable(MainDestinations.USERPROFILE_ROUTE) {
            UserProfileView(
                openDrawer = openDrawer,
                scaffoldState = scaffoldState,
                viewModel = getViewModel<UserProfileViewModel>())
        }

        composable(MainDestinations.DEVELOPER_ROUTE){
            DeveloperView(
                scaffoldState = scaffoldState,
                viewModel = getViewModel<DeveloperViewModel>(),
                openDrawer = openDrawer,
                navigateToDatabaseInfoView = actions.navigateToDeveloperDatabaseInfo)
        }

        composable(MainDestinations.DEVELOPER_DATABASE_INFO_ROUTE){
            DevDatabaseInfoView(
                scaffoldState = scaffoldState,
                navigateUp = actions.upPress,
                viewModel = getViewModel<DevDatabaseInfoViewModel>())
        }

        composable(MainDestinations.LOGOUT_ROUTE){
        }
    }
}

/**
 * Models the navigation actions in the app.
 */
class MainActions(navController: NavHostController) {

    val navigateToReportListView: () -> Unit = {
        navController.navigate(MainDestinations.REPORT_LIST_ROUTE)
    }

    val navigateToReportEditor: (String) -> Unit = { documentId: String ->
        navController.navigate("${MainDestinations.REPORT_EDITOR_ROUTE}/$documentId")
    }

    val navigateToExpenseListByReport: (String) -> Unit = { reportJson: String ->
        navController.navigate("${MainDestinations.EXPENSE_LIST_ROUTE}/$reportJson")
    }

    val navigateToExpenseEditor:(String, String) -> Unit = { reportId: String, expense: String ->
        navController.navigate("${MainDestinations.EXPENSE_EDITOR_ROUTE}/$reportId/$expense")
    }

    val navigateToDeveloperDatabaseInfo:() -> Unit = {
        navController.navigate(MainDestinations.DEVELOPER_DATABASE_INFO_ROUTE)
    }

    val upPress: () -> Unit = {
        navController.popBackStack()
    }
}