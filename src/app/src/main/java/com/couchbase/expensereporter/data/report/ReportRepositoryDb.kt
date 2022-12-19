package com.couchbase.expensereporter.data.report

import android.util.Log
import com.couchbase.expensereporter.data.DatabaseProvider
import com.couchbase.expensereporter.data.expenseTypes.ExpenseTypeRepository
import com.couchbase.expensereporter.data.manager.ManagerRepository
import com.couchbase.expensereporter.models.Manager
import com.couchbase.expensereporter.models.Report
import com.couchbase.expensereporter.models.StandardExpense
import com.couchbase.expensereporter.services.AuthenticationService
import com.couchbase.lite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random.Default.nextDouble

class ReportRepositoryDb(
    private val databaseProvider: DatabaseProvider,
    private val authenticationService: AuthenticationService,
    private val expenseTypeRepository: ExpenseTypeRepository,
    private val managerRepository: ManagerRepository
) : ReportRepository {

    override val databaseName: String
        get() = databaseProvider.currentReportDatabaseName

    override suspend fun get(documentId: String): Report {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseProvider.reportDatabase
                db?.let { database ->
                    val doc = database.getDocument(documentId)
                    doc?.let { document ->
                        val json = document.toJSON()
                        json?.let { reportJson ->
                            return@withContext (Json.decodeFromString<Report>(reportJson))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            val user = authenticationService.getCurrentUser()
            return@withContext (
                    Report(
                        reportId = documentId,
                        reportDate = System.currentTimeMillis(),
                        department = user.department,
                        createdBy = user.username,
                        documentType = "report",
                        status = "Draft",
                        isComplete = false
                    )
                    )
        }
    }

    override suspend fun getDocuments(): Flow<List<Report>> {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseProvider.reportDatabase
                db?.let { database ->
                    //SQL++ VERSION OF THE JOIN
                    val query = database.createQuery("SELECT item.reportId, item.name, item.description, item.isComplete, item.documentType, item.reportDate, item.updatedDate, item.status, item.department, item.createdBy, item.approvalManager, SUM(e.amount) as amount FROM _ AS item LEFT OUTER JOIN _ AS e ON item.reportId = e.reportId AND e.documentType = \"expense\" WHERE item.documentType=\"report\" GROUP BY item.reportId, e.reportId, item.name, item.description, item.isComplete, item.documentType, item.reportDate, item.updatedDate, item.status, item.department, item.createdBy, item.approvalManager ORDER BY item.reportDate DESC")

                    // create a flow to return the results dynamically as needed - more information on
                    // CoRoutine Flows can be found at
                    // https://developer.android.com/kotlin/flow
                    val flow = query
                        .queryChangeFlow()
                        .map { qc -> mapQueryChangeToReport(qc) }
                        .flowOn(Dispatchers.IO)

                    Log.d("**JOIN QUERY**",query.explain())
                    query.execute()
                    return@withContext flow
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext flow { }
        }
    }

    private fun mapQueryChangeToReport(queryChange: QueryChange): List<Report> {
        val documents = mutableListOf<Report>()
        try {
            queryChange.results?.let { results ->
                results.forEach { result ->
                    val json = result.toJSON()
                    val document =
                        Json.decodeFromString<Report>(json)
                    if (!document.reportId.isNullOrBlank()) {
                        documents.add(document)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(e.message, e.stackTraceToString())
        }
        return documents
    }

    override suspend fun save(document: Report): Boolean {
        return withContext(Dispatchers.IO) {
            var result = false
            try {
                val db = databaseProvider.reportDatabase
                db?.let { database ->
                    val updateDoc = document.copy(updatedDate = System.currentTimeMillis())
                    val encoder = Json { encodeDefaults = true }
                    val json = encoder.encodeToString(updateDoc)
                    val doc = MutableDocument(document.reportId, json)
                    database.save(doc)
                    result = true
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext result
        }
    }

    override suspend fun delete(documentId: String): Boolean {
        return withContext(Dispatchers.IO) {
            var result = false
            try {
                val db = databaseProvider.reportDatabase
                db?.let { database ->
                    val document = database.getDocument(documentId)
                    document?.let { doc ->
                        db.delete(doc)
                        result = true
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext result
        }
    }

    override suspend fun count(): Int {
        return withContext(Dispatchers.IO) {
            var count = 0
            try {
                val db = databaseProvider.reportDatabase
                db?.let { database ->
                    val query =
                        database.createQuery("SELECT COUNT(*) AS count FROM _ as item WHERE documentType=\"report\"")
                    val results = query.execute().allResults()
                    count = results[0].getInt("count")
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext count
        }
    }

    override suspend fun updateManager(documentId: String, manager: Manager) {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseProvider.reportDatabase
                val document = get(documentId)
                val updateDoc = document.copy(approvalManager = manager)
                db?.let { database ->
                    val json = Json.encodeToString(updateDoc)
                    val doc = MutableDocument(updateDoc.reportId, json)
                    database.save(doc)
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
        }
    }

    override suspend fun loadSampleData() {
        return withContext(Dispatchers.IO) {
            try {
                //setup values to use for random generation of data
                val currentUser = authenticationService.getCurrentUser()
                val createdBy = currentUser.username
                val department = currentUser.department
                val managers = managerRepository.getByDepartmentTitle(department, title="")
                val expenseTypes = expenseTypeRepository.get()
                val expenseTypesCount = expenseTypes.count()
                val managersCount = managers.count()
                var priorMonthsList = getPriorMonths()
                val zoneId = ZoneId.systemDefault()
                val reportDocumentType = "report"
                val expenseDocumentType = "expense"

                //validate we have managers and expense types to use to randomly generate data
                if (managersCount > 0 &&
                    expenseTypesCount > 0 &&
                    priorMonthsList.isNotEmpty()
                ){
                    val db = databaseProvider.reportDatabase
                    db?.let { database ->
                        for(count in 0 until priorMonthsList.count()){
                            database.inBatch(UnitOfWork {
                                val reportId = UUID.randomUUID().toString()
                                val createdBy = currentUser.username
                                val manager = managers.random()

                                val status = if (count > 0 ) "Completed - Paid" else "In Review"
                                val dateLong = priorMonthsList[count]

                                val reportName = "Expense Report ${count+1}"
                                val description = "$reportName Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                                val reportDocument = Report(
                                    reportId = reportId,
                                    name = reportName,
                                    description = description,
                                    isComplete = true,
                                    reportDate = dateLong,
                                    status = status,
                                    documentType = reportDocumentType,
                                    department = department,
                                    createdBy = createdBy,
                                    updatedDate = dateLong,
                                    approvalManager = manager)
                                val json = Json.encodeToString(reportDocument)
                                val doc = MutableDocument(reportDocument.reportId, json)
                                database.save(doc)

                                //setup expenses for this report
                                for (expenseCount in 0 .. 5){
                                    val expenseId = UUID.randomUUID().toString()
                                    val amount = nextDouble(10000.0).roundToInt()/100.0
                                    val expenseTypeCategory = expenseTypes[0].expenseTypes.random()
                                    val expenseType = expenseTypeCategory.subTypes.random()

                                    val expenseDescription = "${expenseType} description - At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio."
                                    var standardExpenseDocument = StandardExpense(
                                        expenseId = expenseId,
                                        reportId = reportId,
                                        description = expenseDescription,
                                        date = dateLong - 86400,
                                        expenseType = expenseType,
                                        expenseTypeCategory = expenseTypeCategory.name,
                                        documentType = expenseDocumentType,
                                        createdBy = createdBy,
                                        amount = amount)
                                    val expenseJson = Json.encodeToString(standardExpenseDocument)
                                    val expenseDoc = MutableDocument(standardExpenseDocument.expenseId, expenseJson)
                                    database.save(expenseDoc)
                                }
                            })
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
        }
    }
    private fun getPriorMonths():MutableList<Long> {
        var priorMonthsList = mutableListOf<Long>()
        val currentDate =  System.currentTimeMillis()
        val zoneId = ZoneId.systemDefault()

        for (count in 0 .. 5) {
            val month = count + 1
            val date = currentDate - (2.628e+9 * month)
            priorMonthsList.add(date.toLong())
        }
        return priorMonthsList
    }

}