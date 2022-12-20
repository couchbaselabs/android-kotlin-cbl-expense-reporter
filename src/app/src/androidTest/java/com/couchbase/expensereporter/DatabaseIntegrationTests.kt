package com.couchbase.expensereporter

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.couchbase.expensereporter.data.DatabaseProvider
import com.couchbase.expensereporter.data.expense.ExpenseRepository
import com.couchbase.expensereporter.data.expense.ExpenseRepositoryDb
import com.couchbase.expensereporter.data.expenseTypes.ExpenseTypeRepository
import com.couchbase.expensereporter.data.expenseTypes.ExpenseTypeRepositoryDb
import com.couchbase.expensereporter.data.manager.ManagerRepository
import com.couchbase.expensereporter.data.manager.ManagerRepositoryDb
import com.couchbase.expensereporter.data.replicator.ReplicatorProvider
import com.couchbase.expensereporter.data.report.ReportRepository
import com.couchbase.expensereporter.data.report.ReportRepositoryDb
import com.couchbase.expensereporter.data.userprofile.UserProfileRepository
import com.couchbase.expensereporter.models.User
import com.couchbase.expensereporter.services.AuthenticationService
import com.couchbase.expensereporter.services.MockAuthenticationService
import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.Database
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import kotlin.math.exp

@RunWith(AndroidJUnit4::class)
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class DatabaseIntegrationTests {
    //required for application
    private lateinit var context:Context

    //major providers and services
    private lateinit var databaseProvider: DatabaseProvider
    private lateinit var authenticationService: AuthenticationService
    private lateinit var replicatorProvider: ReplicatorProvider

    //repositories
    private lateinit var expenseTypeRepository: ExpenseTypeRepository
    private lateinit var expenseRepository: ExpenseRepository
    private lateinit var managerRepository: ManagerRepository
    private lateinit var reportRepository: ReportRepository
    private lateinit var userProfileRepository: UserProfileRepository

    //test users
    private lateinit var demoUser: User
    private lateinit var demoUser10: User

    @Before
    fun setup() {
        try {
            //arrange database
            context = ApplicationProvider.getApplicationContext()
            authenticationService = MockAuthenticationService()
            replicatorProvider = ReplicatorProvider(authenticationService)
            databaseProvider = DatabaseProvider(context, replicatorProvider)

            //arrange demo users
            demoUser = User(
                username = "demo@example.com",
                password = "P@ssw0rd12",
                department = "Engineering"
            )
            demoUser10 = User(
                username = "demo10@example.com",
                password = "P@ssw0rd12",
                department = "Sales"
            )

            //setup databases for use
            //if a test fails the database will be dirty, this
            //fixes that problem
            databaseProvider.initializeDatabases(demoUser)
            databaseProvider.deleteDatabases()
            databaseProvider.initializeDatabases(demoUser)

            //arrange repositories
            userProfileRepository = UserProfileRepository(databaseProvider = databaseProvider)
            expenseTypeRepository = ExpenseTypeRepositoryDb(databaseProvider = databaseProvider)
            expenseRepository = ExpenseRepositoryDb(
                databaseProvider = databaseProvider,
                authenticationService = authenticationService
            )
            managerRepository = ManagerRepositoryDb(databaseProvider = databaseProvider)
            reportRepository = ReportRepositoryDb(
                databaseProvider = databaseProvider,
                authenticationService = authenticationService,
                expenseTypeRepository = expenseTypeRepository,
                managerRepository = managerRepository
            )

            runTest {
                reportRepository.loadSampleData()
            }
        } catch (e: Exception){
            Log.e(e.message, e.stackTraceToString())
        }
    }

    @After
    fun cleanUp() {
        try{
            databaseProvider.closeDatabases()
            databaseProvider.deleteDatabases()
        }catch(e: CouchbaseLiteException){
            Log.e(e.message, e.stackTraceToString())
        }
    }

    @Test
    fun databaseSetupTest(){
        //arrange for demoUser10
        databaseProvider.closeDatabases()
        databaseProvider.initializeDatabases(demoUser10)

        //assert for demoUser10
        assertNotNull(databaseProvider.reportDatabase)
        assertNotNull(databaseProvider.startingDatabase)
        assertEquals("demo10_example.com_report", databaseProvider.reportDatabase?.name)
        assertEquals("startingDb", databaseProvider.startingDatabase?.name)

        //arrange for demoUser
        databaseProvider.closeDatabases()
        databaseProvider.deleteDatabases()
        databaseProvider.initializeDatabases(demoUser)

        //assert for demoUser
        assertNotNull(databaseProvider.reportDatabase)
        assertNotNull(databaseProvider.startingDatabase)
        assertEquals("demo_example.com_report", databaseProvider.reportDatabase?.name)
        assertEquals("startingDb", databaseProvider.startingDatabase?.name)

    }

    @Test
    fun testUserProfileCount() {
        runTest{
            //arrange
            val userProfile = getDemoUserProfileDictionary()

            //act
            val preCount = userProfileRepository.count()
            val didSave = userProfileRepository.save(userProfile)
            val postCount = userProfileRepository.count()

            //assert
            assertTrue(didSave)
            assertEquals(0, preCount)
            assertEquals(1, postCount)
        }
    }

    @Test
    fun testUserProfileSave() {
        runTest{
            //arrange
            val userProfile = getDemoUserProfileDictionary()

            //act
            val didSave = userProfileRepository.save(userProfile)
            val demoUserUserProfile = userProfileRepository.get("demo@example.com")

            //assert
            assertTrue(didSave)
            assertEquals("Jane", demoUserUserProfile["givenName"])
            assertEquals("Doe", demoUserUserProfile["surname"])
            assertEquals("Engineering", demoUserUserProfile["department"])
            assertEquals("Sr. Developer", demoUserUserProfile["jobTitle"])
            assertEquals("user", demoUserUserProfile["documentType"])

            //cleanup
            userProfileRepository.delete("user::demo@example.com")
        }
    }

    @Test
    fun testReportCount() {
        runTest {
            //arrange already done as part of @Before

            //act
            val reportCount = reportRepository.count()

            //assert
            assertEquals(6, reportCount)
        }
    }

    @Test
    fun testGetReports() {
        runTest {
            //arrange
            val reports = reportRepository.getDocuments().first()
            val firstReport = reports.first()

            //act
            val report = reportRepository.get(firstReport.reportId)

            //assert
            assertNotNull(report)
            assertEquals(firstReport.name, report.name)
        }
    }

    @Test
    fun testUpdateManager() {
        runTest {
            //arrange
            val reports = reportRepository.getDocuments().first()
            val secondReport = reports[1]
            val managers = managerRepository.get()
            val manager = managers[(managers.count() - 2)]

            //act
            reportRepository.updateManager(secondReport.reportId, manager)
            val updatedReport = reportRepository.get(secondReport.reportId)

            //assert
            assertNotNull(updatedReport)
            assertNotNull(updatedReport.approvalManager)
            assertEquals(manager, updatedReport.approvalManager)
            assertNotEquals(secondReport.approvalManager, manager)
        }
    }

    @Test
    fun testReportSave() {
        runTest {
            //arrange
            val reports = reportRepository.getDocuments().first()
            val thirdReport = reports[3]
            val updatedThirdReport = thirdReport.copy(name = "Updated Expense Report 3", description = "Updated Description")

            //act
            val didSave = reportRepository.save(updatedThirdReport)
            val updatedReport = reportRepository.get(updatedThirdReport.reportId)

            //assert
            assertTrue(didSave)
            assertNotNull(updatedReport)
            assertEquals("Updated Expense Report 3", updatedReport.name)
            assertEquals("Updated Description", updatedReport.description)
        }
    }

    @Test
    fun testReportDelete() {
        runTest {
            //arrange
            val reports = reportRepository.getDocuments().first()
            val deleteReport = reports[reports.count() - 1]

            //act
            val result = reportRepository.delete(deleteReport.reportId)
            val updatedReport = reportRepository.get(deleteReport.reportId)

            //assert
            assertTrue(result)
            assertNotNull(updatedReport)
            assertNotEquals(deleteReport.name, updatedReport.name)
            assertNotEquals(deleteReport.reportDate, updatedReport.reportDate)
        }
    }

    @Test
    fun testPreBuildDatabaseCounters() {
        runTest {
            //arrange done in @Before

            //act
            val managersCount = managerRepository.count()
            val expenseTypeCount = expenseTypeRepository.count()

            //assert
            assertEquals(18, managersCount)
            assertEquals(6, expenseTypeCount)
        }
    }

    @Test
    fun testManagerGetByDepartmentTitle() {
        runTest {
            //arrange is done by @Before
            val title = "dev"
            val department = "eng"

            //act
            val searchManagers = managerRepository.getByDepartmentTitle(department = department, title = title)
            val manager = searchManagers[0]

            //assert
            assertNotNull(searchManagers)
            assertEquals(4, searchManagers.count())
            assertEquals("Cottem", manager.surname)
            assertEquals("Lead Frontend Developer", manager.jobTitle)
        }
    }

    @Test
    fun testGetExpensesByReportId()
    {
        runTest {
            //arrange
            val reports = reportRepository.getDocuments().first()
            val report = reports.first()

            //act
            val expenses = expenseRepository.getExpenses(report.reportId).first()

            //assert
            assertNotNull(expenses)
            assertEquals(6, expenses.count())
        }
    }

    @Test
    fun testGetExpense()
    {
        runTest {
            //arrange
            val reports = reportRepository.getDocuments().first()
            val report = reports.first()
            val expenses = expenseRepository.getExpenses(report.reportId).first()
            val testExpense = expenses.first()

            //act
            val expense = expenseRepository.get(testExpense.reportId, testExpense.expenseId)

            //assert
            assertNotNull(expense)
            assertEquals(testExpense.expenseType, expense.expenseType)
            assertEquals(testExpense.expenseTypeCategory, expense.expenseTypeCategory)
            assertEquals(testExpense.amount, expense.amount, 0.0001)
            assertEquals(testExpense.description, expense.description)
            assertEquals(testExpense.documentType, expense.documentType)
            assertEquals(testExpense.date, expense.date)
        }
    }

    @Test
    fun testSaveExpense() {
        runTest {
            //arrange
            val reports = reportRepository.getDocuments().first()
            val report = reports.first()
            val expenses = expenseRepository.getExpenses(report.reportId).first()
            val testExpense = expenses[expenses.count() - 2]
            val expenseTypes = expenseTypeRepository.get()
            val expenseType = expenseTypes.first().expenseTypes.last()
            val expenseSubType = expenseType.subTypes.last()
            val date = System.currentTimeMillis()

            //act
            val updatedDoc = testExpense.copy(description = "test description", amount = 1.00, expenseTypeCategory = expenseType.name, expenseType = expenseSubType, date = date)
            val didSave = expenseRepository.save(updatedDoc)
            val expense = expenseRepository.get(testExpense.reportId, testExpense.expenseId)

            //assert
            assertEquals(1.00 , expense.amount, 0.0001)
            assertEquals(expenseType.name , expense.expenseTypeCategory)
            assertEquals(expenseSubType , expense.expenseType)
            assertEquals(date, expense.date)
            assertEquals("test description", expense.description)
        }
    }

    private fun getDemoUserProfileDictionary(): MutableMap<String, String> {
        val dict = mutableMapOf<String, String>()
        dict["givenName"] = "Jane"
        dict["surname"] = "Doe"
        dict["jobTitle"] = "Sr. Developer"
        dict["department"] = "Engineering"
        dict["email"] = "demo@example.com"
        dict["documentType"] = "user"
        return dict
    }

}