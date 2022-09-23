package com.couchbase.expensereporter

import android.app.Application

import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

import java.lang.ref.WeakReference

import com.couchbase.expensereporter.data.KeyValueRepository
import com.couchbase.expensereporter.data.report.ReportRepository
import com.couchbase.expensereporter.data.report.ReportRepositoryDb
import com.couchbase.expensereporter.data.userprofile.UserProfileRepository
import com.couchbase.expensereporter.services.AuthenticationService
import com.couchbase.expensereporter.services.MockAuthenticationService
import com.couchbase.expensereporter.ui.MainViewModel
import com.couchbase.expensereporter.ui.developer.DevDatabaseInfoViewModel
import com.couchbase.expensereporter.ui.developer.DeveloperInfoWidget
import com.couchbase.expensereporter.ui.developer.DeveloperViewModel
import com.couchbase.expensereporter.ui.login.LoginViewModel
import com.couchbase.expensereporter.ui.profile.UserProfileViewModel
import com.couchbase.expensereporter.ui.report.ReportEditorViewModel
import com.couchbase.expensereporter.ui.report.ReportListViewModel

class ExpenseReporterApplication
    : Application()
{
        override fun onCreate() {
            super.onCreate()
            // enable Koin dependency injection framework
            // https://insert-koin.io/docs/reference/koin-android/start
            GlobalContext.startKoin {
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
                single { MockAuthenticationService() as AuthenticationService }
                single { UserProfileRepository(this@ExpenseReporterApplication) as KeyValueRepository }
                single { ReportRepositoryDb(this@ExpenseReporterApplication, get()) as ReportRepository}

                viewModel{ LoginViewModel(get(), WeakReference(this@ExpenseReporterApplication))}
                viewModel { MainViewModel(get(), WeakReference(this@ExpenseReporterApplication))}
                viewModel { ReportListViewModel(get())}
                viewModel { ReportEditorViewModel(get()) }
                viewModel { UserProfileViewModel(get(), get(), WeakReference(this@ExpenseReporterApplication)) }

                viewModel { DeveloperViewModel(get()) }
                viewModel { DevDatabaseInfoViewModel(get(), get(), get()) }
            }
        }
}