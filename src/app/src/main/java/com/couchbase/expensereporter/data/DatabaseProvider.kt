package com.couchbase.expensereporter.data

import android.content.Context
import com.couchbase.expensereporter.models.User
import com.couchbase.expensereporter.util.Singleton
import com.couchbase.lite.*
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class DatabaseProvider private constructor(private val context: Context) {

    var reportDatabase: Database? = null
    var typesDatabase: Database? = null

    private val defaultReportDatabaseName = "report"
    private val typesDatabaseName = "types"
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
        typesDatabase?.close()
    }

    fun closeDatabases() {
        try {
            reportDatabase?.close()
            typesDatabase?.close()
        } catch (e: java.lang.Exception) {
            android.util.Log.e(e.message, e.stackTraceToString())
        }
    }

    fun deleteDatabases() {
        try {
            closeDatabases()
            Database.delete(currentReportDatabaseName, context.filesDir)
            Database.delete(typesDatabaseName, context.filesDir)
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
            currentReportDatabaseName = username.replace('@', '_').plus("_").plus(defaultReportDatabaseName)
            reportDatabase = Database(currentReportDatabaseName, dbConfig)

            //setup the warehouse Database
            //setupTypesDatabase(dbConfig)

            //create indexes for database queries
            createTypeIndex(reportDatabase)
            createTypeIndex(typesDatabase)

            //todo create indexes

        } catch (e: Exception) {
            android.util.Log.e(e.message, e.stackTraceToString())
        }
    }

    private fun setupTypesDatabase(dbConfig: DatabaseConfiguration) {
        // create the warehouse database if it doesn't already exist
        val startingTypesFileName = "prebuiltTypes.zip"
        val prebuiltTypesDatabaseName = "prebuiltTypes"

        if (!Database.exists(typesDatabaseName, context.filesDir)) {
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
            Database.copy(typesDbFile, typesDatabaseName, dbConfig)
        }
        typesDatabase = Database(typesDatabaseName, dbConfig)
    }

    // create index for document type if it doesn't exist
    private fun createTypeIndex(
        database: Database?) {
        database?.let {
            if (!it.indexes.contains("idx_document_type")) {
                it.createIndex(
                    "idx_document_type", IndexBuilder.valueIndex(
                        ValueIndexItem.expression(
                            Expression.property("documentType")
                        )
                    )
                )
            }
        }
    }

    private fun unzip(
        file: String,
        destination: File) {
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

    companion object : Singleton<DatabaseProvider, Context>(::DatabaseProvider)
}