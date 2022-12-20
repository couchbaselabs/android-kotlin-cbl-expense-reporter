package com.couchbase.expensereporter.data

import android.content.Context
import com.couchbase.expensereporter.data.replicator.ReplicatorProvider
import com.couchbase.expensereporter.models.User
import com.couchbase.lite.*
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class DatabaseProvider(
    private val context: Context,
    private val replicatorProvider: ReplicatorProvider) {

    var reportDatabase: Database? = null
    var startingDatabase: Database? = null

    private val defaultReportDatabaseName = "report"
    private val staringDatabaseName = "startingDb"
    var currentReportDatabaseName = "report"

    init {
        //setup couchbase lite
        CouchbaseLite.init(context)

        //WARNING:  turn on FULL LOGGING
        //in production apps this shouldn't be turn on
        Database.log.console.domains = LogDomain.ALL_DOMAINS
        Database.log.console.level = LogLevel.VERBOSE
    }

    fun dispose() {
        reportDatabase?.close()
        startingDatabase?.close()
    }

    fun closeDatabases() {
        try {
            reportDatabase?.close()
            startingDatabase?.close()
        } catch (e: java.lang.Exception) {
            android.util.Log.e(e.message, e.stackTraceToString())
        }
    }

    fun deleteDatabases() {
        try {
            closeDatabases()
            Database.delete(currentReportDatabaseName, context.filesDir)
            Database.delete(staringDatabaseName, context.filesDir)
        } catch (e: Exception) {
            android.util.Log.e(e.message, e.stackTraceToString())
        }
    }


    fun initializeDatabases(currentUser: User) {
        try {
            val dbConfig = DatabaseConfigurationFactory.create(context.filesDir.toString())

            // create or open a database for user to create expense reports
            // calculate database name based on current logged in users username
            val username = currentUser.username
            currentReportDatabaseName =
                username.replace('@', '_').plus("_").plus(defaultReportDatabaseName)
            reportDatabase = Database(currentReportDatabaseName, dbConfig)

            //setup the warehouse Database
            setupStartingDatabase(dbConfig)

            //create indexes for database queries
            createDocumentTypeIndex(reportDatabase)
            createDocumentTypeIndex(startingDatabase)

            createReportIdIndex(reportDatabase)
            createDocumentTypeReportIdIndex(reportDatabase)

            createDocumentTypeTitleDepartmentIndex(startingDatabase)

            //setup replicator
            reportDatabase?.let { db ->
                replicatorProvider.setupReplicator(db)
                replicatorProvider.replicator?.let { replicator ->
                    if (replicator.status.activityLevel == ReplicatorActivityLevel.STOPPED || replicator.status.activityLevel == ReplicatorActivityLevel.OFFLINE) {
                        //replicator.start()
                    }
                }
            }


        } catch (e: Exception) {
            android.util.Log.e(e.message, e.stackTraceToString())
        }
    }

    private fun setupStartingDatabase(dbConfig: DatabaseConfiguration) {
        // create the warehouse database if it doesn't already exist
        val startingTypesFileName = "starting.zip"
        val prebuiltTypesDatabaseName = "starting"

        if (!Database.exists(staringDatabaseName, context.filesDir)) {
            unzip(startingTypesFileName, File(context.filesDir.toString()))

            /*
              Copy the starting types database to a new database file
              be usable for sync, if needed.  Never use the prebuilt database
              directly as this will cause issues with sync
              and document revisions.
             */
            val typesDbFile =
                File(
                    String.format(
                        "%s/%s",
                        context.filesDir,
                        ("${prebuiltTypesDatabaseName}.cblite2")
                    )
                )
            Database.copy(typesDbFile, staringDatabaseName, dbConfig)
        }
        startingDatabase = Database(staringDatabaseName, dbConfig)
    }

    // create index for document type if it doesn't exist
    private fun createDocumentTypeIndex(
        database: Database?
    ) {
        database?.let {
            if (!it.indexes.contains("idx_document_type")) {
                it.createIndex(
                    "idx_document_type", IndexBuilder.valueIndex(
                        ValueIndexItem.property("documentType")
                    )
                )
            }
        }
    }

    private fun createReportIdIndex(
        database: Database?
    ) {
        database?.let {
            if (!it.indexes.contains("idx_reportId")) {
                it.createIndex(
                    "idx_reportId", IndexBuilder.valueIndex(
                        ValueIndexItem.property("reportId")
                    )
                )
            }
        }
    }

    private fun createDocumentTypeReportIdIndex(
        database: Database?
    ) {
        database?.let {
            if (!it.indexes.contains("idx_document_type_reportId")) {
                it.createIndex(
                    "idx_document_type_reportId", IndexBuilder.valueIndex(
                        ValueIndexItem.property("documentType"),
                        ValueIndexItem.property("reportId")
                    )
                )
            }
        }
    }

    // create index for manager search
    private fun createDocumentTypeTitleDepartmentIndex(
        database: Database?
    ) {
        database?.let {
            if (!it.indexes.contains("idx_document_type_title_department")) {
                it.createIndex(
                    "idx_document_type_title_department", IndexBuilder.valueIndex(
                        ValueIndexItem.property("documentType"),
                        ValueIndexItem.property("title"),
                        ValueIndexItem.property("department")
                    )
                )
            }
        }
    }

    private fun unzip(
        file: String,
        destination: File
    ) {
        context.assets.open(file).use { stream ->
            val buffer = ByteArray(1024)
            val zis = ZipInputStream(stream)
            var ze: ZipEntry? = zis.nextEntry
            while (ze != null) {
                val fileName: String = ze.name
                val newFile = File(destination, fileName)
                if (ze.isDirectory) {
                    newFile.mkdirs()
                } else {
                    File(newFile.parent!!).mkdirs()
                    val fos = FileOutputStream(newFile)
                    var len: Int
                    while (zis.read(buffer).also { len = it } > 0) {
                        fos.write(buffer, 0, len)
                    }
                    fos.close()
                }
                ze = zis.nextEntry
            }
            zis.closeEntry()
            zis.close()
            stream.close()
        }
    }
}