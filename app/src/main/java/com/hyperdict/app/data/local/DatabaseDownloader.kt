package com.hyperdict.app.data.local

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

private const val TAG = "DatabaseDownloader"

// ECDICT - https://github.com/skywind3000/ECDICT
private const val DATABASE_ZIP_URL = "https://github.com/skywind3000/ECDICT/releases/download/1.0.28/ecdict-sqlite-28.zip"
private const val DATABASE_NAME = "ecdict.db"
private const val DATABASE_FILE_IN_ZIP = "ecdict.db"
private const val TEMP_DOWNLOAD_FILE = "ecdict.db.download"

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

    fun getTempDownloadPath(context: Context): File {
        return File(context.filesDir, TEMP_DOWNLOAD_FILE)
    }

    fun isDatabaseDownloaded(context: Context): Boolean {
        return getDatabasePath(context).exists()
    }

    fun downloadDatabase(context: Context): Flow<DownloadProgress> = flow {
        withContext(Dispatchers.IO) {
            val dbFile = getDatabasePath(context)
            val tempFile = getTempDownloadPath(context)

            if (dbFile.exists()) {
                emit(DownloadProgress(DownloadProgress.Status.SUCCESS))
                return@withContext
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

                // Support resume: check if we have a partial download
                var downloadedBytes = 0L
                if (tempFile.exists()) {
                    downloadedBytes = tempFile.length()
                    connection.setRequestProperty("Range", "bytes=$downloadedBytes-")
                    Log.d(TAG, "Resuming download from byte $downloadedBytes")
                }

                connection.connect()

                val responseCode = connection.responseCode
                // Accept both 200 (full download) and 206 (partial content/resume)
                if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_PARTIAL) {
                    throw java.io.IOException("Server returned HTTP $responseCode")
                }

                val totalBytes = connection.contentLengthLong
                val actualTotalBytes = if (downloadedBytes > 0) {
                    // When resuming, totalBytes is the remaining bytes, so add downloadedBytes
                    if (totalBytes > 0) downloadedBytes + totalBytes else 0
                } else {
                    totalBytes
                }

                if (actualTotalBytes <= 0) {
                    Log.w(TAG, "Server did not return content length")
                }

                inputStream = connection.inputStream

                // Use FileOutputStream with append support for resume
                val outputStream = FileOutputStream(tempFile, downloadedBytes > 0)

                // Use ZipInputStream only for fresh downloads; for resume, we need to handle differently
                // Since ZIP doesn't support resume well, we'll download the full ZIP to temp first
                // then extract on completion
                val buffer = ByteArray(65536) // Larger buffer for better performance
                var bytesRead: Int
                var totalBytesRead = downloadedBytes

                while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead

                    emit(
                        DownloadProgress(
                            status = DownloadProgress.Status.DOWNLOADING,
                            bytesDownloaded = totalBytesRead,
                            totalBytes = actualTotalBytes
                        )
                    )
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

                // Extract the database from the downloaded ZIP
                emit(
                    DownloadProgress(
                        status = DownloadProgress.Status.DOWNLOADING,
                        bytesDownloaded = totalBytesRead,
                        totalBytes = actualTotalBytes
                    )
                )

                // Extract ZIP
                val zipInputStream = ZipInputStream(FileInputStream(tempFile))
                val dbOutputStream = FileOutputStream(dbFile)

                var entryFound = false
                var entry = zipInputStream.nextEntry
                while (entry != null) {
                    val entryName = entry.name.substringAfterLast('/').substringAfterLast('\\')
                    if (entryName == DATABASE_FILE_IN_ZIP) {
                        val extractBuffer = ByteArray(65536)
                        var extractBytesRead: Int

                        while (zipInputStream.read(extractBuffer).also { extractBytesRead = it } > 0) {
                            dbOutputStream.write(extractBuffer, 0, extractBytesRead)
                        }
                        entryFound = true
                        break
                    }
                    entry = zipInputStream.nextEntry
                }

                if (!entryFound) {
                    dbOutputStream.close()
                    dbFile.delete()
                    throw java.io.IOException("Database file '$DATABASE_FILE_IN_ZIP' not found in ZIP archive")
                }

                dbOutputStream.flush()
                dbOutputStream.close()
                zipInputStream.closeEntry()
                zipInputStream.close()

                // Clean up temp file
                tempFile.delete()

                // Verify the downloaded file
                if (!dbFile.exists() || dbFile.length() == 0L) {
                    throw java.io.IOException("Downloaded database file is empty or corrupted")
                }

                emit(DownloadProgress(DownloadProgress.Status.SUCCESS))
                Log.d(TAG, "Database downloaded successfully to: ${dbFile.absolutePath} (${dbFile.length()} bytes)")

            } catch (e: Throwable) {
                Log.e(TAG, "Failed to download database", e)
                // Don't delete temp file - allow resume on retry
                if (dbFile.exists()) {
                    dbFile.delete()
                }
                val errorMessage = buildString {
                    val message = e.message
                    if (!message.isNullOrBlank()) {
                        append(message)
                    } else {
                        append("Error: ${e::class.java.simpleName}")
                    }
                    if (e.cause != null) {
                        append("\nCaused by: ${e.cause?.message}")
                    }
                    // Add helpful context based on exception type
                    when (e) {
                        is java.net.SocketTimeoutException -> append("\n\nConnection timed out. Please check your network.")
                        is java.net.UnknownHostException -> append("\n\nCannot reach server. Check internet connection.")
                        is java.net.MalformedURLException -> append("\n\nInvalid download URL.")
                        is java.io.FileNotFoundException -> append("\n\nFile not found: $message")
                        is java.io.IOException -> append("\n\nNetwork or storage error.")
                        is SecurityException -> append("\n\nPermission denied: $message")
                        else -> {}
                    }
                    // Append full stack trace for debugging
                    append("\n\n--- Full Stack Trace ---\n")
                    val sw = StringWriter()
                    e.printStackTrace(PrintWriter(sw))
                    append(sw.toString())
                }
                emit(
                    DownloadProgress(
                        status = DownloadProgress.Status.FAILED,
                        error = errorMessage
                    )
                )
            } finally {
                try { inputStream?.close() } catch (_: Exception) {}
                try { connection?.disconnect() } catch (_: Exception) {}
            }
        }
    }.flowOn(Dispatchers.IO)
}
