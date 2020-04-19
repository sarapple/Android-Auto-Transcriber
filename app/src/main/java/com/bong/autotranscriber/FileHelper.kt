package com.bong.autotranscriber

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.midisheetmusic.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class FileHelper {
    companion object {
        /**
         * Create a directory path, if it does not exist
         */
        private fun getTempCacheDirectory(
            context: Context,
            subPath: String
        ): File? {
            val bitmapFolder = File(
                context.cacheDir.toString() +
                        File.separator + subPath
            )
            if (!bitmapFolder.exists()) { // If folder does not exist
                if (!bitmapFolder.mkdirs()) { // and if cannot successfully make the folder
                    return null
                }
            }
            return bitmapFolder
        }

        /**
         * Brother SDK takes your bitmap data and stores it into a temporary folder. Returns that folder path.
         * We need to declare to Brother SDK which folder we want them to use for the folder location
         * (something we have permissions for).
         */
        fun getWorkPathDirectory(context: Context): String {
            val bitmapFolder = getTempCacheDirectory(context, "bitmap_files")
                ?: throw Error("Unable to generate cache directory used by Brother when sending bitmap data")
            return bitmapFolder.path
        }

        /**
         * Get the absolute path for the .bin file (custom printing paper configs)
         * 1. The resource folder does not provide an absolute path for files here.
         * 2. Brother SDK requires a full path to the .bin files for custom printing configs.
         * Therefore create a copy of the .bin file in a temp directory,
         * and from that copy we will have a file that we can derive an absolute path to provide to Brother SDK.
         */
        fun getCustomPaperPath(context: Context): String {
            return try {
                val inputStream = context.resources.openRawResource(R.raw.rj2150_58mm)
                val tempFile = getEmptyFileInFolder(context, "bin_files", "printer_file", ".bin")
                Files.copy(
                    inputStream,
                    tempFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
                tempFile.absolutePath
            } catch (e: IOException) {
                throw RuntimeException(
                    "Can't create copy of custom resource to be used by Brother SDK.",
                    e
                )
            }
        }

        fun getEmptyFileInFolder(context: Context, subPath: String, filePrefix: String = "", fileType: String = ".tmp"): File {
            return try {
                val folder = getTempCacheDirectory(context, subPath)
                    ?: throw Error("Unable to generate cache directory used by Brother when sending .bin file path")
                val tempFile =
                    File.createTempFile(filePrefix, fileType, folder)
                tempFile
            } catch (e: IOException) {
                throw RuntimeException(
                    "Can't create copy of custom resource to be used by Brother SDK.",
                    e
                )
            }
        }

        fun assetFileCopy(
            context: Context,
            assetName: String?
        ): File {
            val file = getAssetFile(context, assetName)
            if (file.exists() && file.length() > 0) {
                return file
            }
            context.assets.open(assetName!!).use { `is` ->
                FileOutputStream(file).use { os ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (`is`.read(buffer).also { read = it } != -1) {
                        os.write(buffer, 0, read)
                    }
                    os.flush()
                }
                return file
            }
        }

        /*
         * Gets the asset file?
         */
        fun getAssetFile(
            context: Context,
            assetName: String?
        ): File {
            return File(context.filesDir, assetName!!)
        }

        fun getBitmapFromUri(uri: Uri, context: Context): Bitmap? {
            context.contentResolver.openFileDescriptor(uri, "r")?.apply {
                val options = BitmapFactory.Options().apply {
                    // Printout works without these fields
                    inJustDecodeBounds = false
                    inPreferredConfig = Bitmap.Config.RGB_565
                    inSampleSize = 1
                }

                return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
            }
            return null
        }

    }
}