package com.couchbase.expensereporter

import android.app.Application
import android.content.Context

import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.core.module.dsl.singleOf
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.bind

import com.couchbase.expensereporter.data.KeyValueRepository
import com.couchbase.expensereporter.data.expense.ExpenseRepository
import com.couchbase.expensereporter.data.expense.ExpenseRepositoryDb
import com.couchbase.expensereporter.data.expenseTypes.ExpenseTypeRepository
import com.couchbase.expensereporter.data.expenseTypes.ExpenseTypeRepositoryDb
import com.couchbase.expensereporter.data.manager.ManagerRepository
import com.couchbase.expensereporter.data.manager.ManagerRepositoryDb
import com.couchbase.expensereporter.data.report.ReportRepository
import com.couchbase.expensereporter.data.report.ReportRepositoryDb
import com.couchbase.expensereporter.data.userprofile.UserProfileRepository
import com.couchbase.expensereporter.services.AuthenticationService
import com.couchbase.expensereporter.services.MockAuthenticationService
import com.couchbase.expensereporter.ui.MainViewModel
import com.couchbase.expensereporter.ui.developer.DevDatabaseInfoViewModel
import com.couchbase.expensereporter.ui.developer.DeveloperInfoWidget
import com.couchbase.expensereporter.ui.developer.DeveloperViewModel
import com.couchbase.expensereporter.ui.expense.ExpenseEditorViewModel
import com.couchbase.expensereporter.ui.expense.ExpenseListViewModel
import com.couchbase.expensereporter.ui.login.LoginViewModel
import com.couchbase.expensereporter.ui.profile.UserProfileViewModel
import com.couchbase.expensereporter.ui.report.ManagerSelectionViewModel
import com.couchbase.expensereporter.ui.report.ReportEditorViewModel
import com.couchbase.expensereporter.ui.report.ReportListViewModel
import com.couchbase.expensereporter.data.DatabaseProvider
import com.couchbase.expensereporter.data.replicator.ReplicatorProvider
import org.koin.core.context.GlobalContext.startKoin

class ExpenseReporterApplication
    : Application()
{
        override fun onCreate() {
            super.onCreate()
            // enable Koin dependency injection framework
            // https://insert-koin.io/docs/reference/koin-android/start
            startKoin {
                // Koin Android logger
                //work around for error: https://github.com/InsertKoinIO/koin/issues/1188
                androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)

                //inject Android context
                androidContext(this@ExpenseReporterApplication)

                //dependency register modules
                modules(registerDependencies())
            }
        }

        private fun registerDependencies() : Module {
            return module {
                // ** DO NOT listen to the NO cast needed warnings - removing the as statement will
                // ** result in the application not functioning correctly
                singleOf(::DatabaseProvider)

                singleOf(::MockAuthenticationService) bind AuthenticationService::class
                singleOf(::UserProfileRepository) bind KeyValueRepository::class
                singleOf(::ReportRepositoryDb) bind ReportRepository::class
                singleOf(::ManagerRepositoryDb) bind ManagerRepository::class
                singleOf(::ExpenseRepositoryDb) bind ExpenseRepository::class
                singleOf(::ExpenseTypeRepositoryDb) bind ExpenseTypeRepository::class

                singleOf(::ReplicatorProvider)

                viewModelOf(::LoginViewModel)
                viewModelOf(::MainViewModel)
                viewModelOf(::ReportListViewModel)
                viewModelOf(::ReportEditorViewModel)
                viewModelOf(::ExpenseListViewModel)
                viewModelOf(::ExpenseEditorViewModel)
                viewModelOf(::UserProfileViewModel)
                viewModelOf(::ManagerSelectionViewModel)
                viewModelOf(::DeveloperViewModel)
                viewModelOf(::DevDatabaseInfoViewModel)
            }
        }
}