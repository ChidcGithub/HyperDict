package com.hyperdict.app.data.local

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

private const val TAG = "DatabaseDownloader"

// ECDICT - https://github.com/skywind3000/ECDICT
private const val DATABASE_ZIP_URL = "https://github.com/skywind3000/ECDICT/releases/download/1.0.28/ecdict-sqlite-28.zip"
private const val DATABASE_NAME = "ecdict.db"
private const val DATABASE_FILE_IN_ZIP = "ecdict.db"

data class DownloadProgress(
    val status: Status,
    val bytesDownloaded: Long = 0,
    val totalBytes: Long = 0,
    val error: String? = null
) {
    enum class Status {
        NOT_STARTED, DOWNLOADING, SUCCESS, FAILED
    }

    val percentage: Int
        get() = if (totalBytes > 0) ((bytesDownloaded * 100) / totalBytes).toInt() else 0
}

object DatabaseDownloader {

    fun getDatabasePath(context: Context): File {
        return context.getDatabasePath(DATABASE_NAME)
    }

    fun isDatabaseDownloaded(context: Context): Boolean {
        return getDatabasePath(context).exists()
    }

    fun downloadDatabase(context: Context): Flow<DownloadProgress> = flow {
        val dbFile = getDatabasePath(context)

        if (dbFile.exists()) {
            emit(DownloadProgress(DownloadProgress.Status.SUCCESS))
            return@flow
        }

        dbFile.parentFile?.mkdirs()

        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null

        try {
            emit(DownloadProgress(DownloadProgress.Status.DOWNLOADING))

            val url = URL(DATABASE_ZIP_URL)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 120000
            connection.readTimeout = 120000
            connection.connect()

            val totalBytes = connection.contentLengthLong
            inputStream = connection.inputStream

            // Use ZipInputStream to extract the database file
            val zipInputStream = ZipInputStream(inputStream)
            val outputStream = FileOutputStream(dbFile)

            var entry = zipInputStream.nextEntry
            while (entry != null) {
                if (entry.name == DATABASE_FILE_IN_ZIP || entry.name.endsWith("/$DATABASE_FILE_IN_ZIP")) {
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalBytesRead = 0L

                    while (zipInputStream.read(buffer).also { bytesRead = it } > 0) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead

                        emit(
                            DownloadProgress(
                                status = DownloadProgress.Status.DOWNLOADING,
                                bytesDownloaded = totalBytesRead,
                                totalBytes = totalBytes
                            )
                        )
                    }
                    break
                }
                entry = zipInputStream.nextEntry
            }

            outputStream.flush()
            outputStream.close()
            zipInputStream.closeEntry()
            zipInputStream.close()

            emit(DownloadProgress(DownloadProgress.Status.SUCCESS))
            Log.d(TAG, "Database downloaded successfully to: ${dbFile.absolutePath}")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to download database", e)
            dbFile.delete()
            emit(
                DownloadProgress(
                    status = DownloadProgress.Status.FAILED,
                    error = e.message ?: "Unknown error occurred"
                )
            )
        } finally {
            inputStream?.close()
            connection?.disconnect()
        }
    }
}
