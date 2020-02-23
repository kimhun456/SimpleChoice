package kimhun456.github.com.simpechoice

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@Throws(IOException::class)
fun createLocalImage(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}

fun saveImageFileToExternalStorage(savedFile: File): File? {

    val linkSharingSavingFolder = "/DCIM/LinkSharing"
    val state = Environment.getExternalStorageState()
    return if (Environment.MEDIA_MOUNTED == state) {
        val root = Environment.getExternalStorageDirectory()
        val dir = File(root.absolutePath + linkSharingSavingFolder)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val copyFile = File(dir, savedFile.name)
        copyFile.outputStream().use {
            savedFile.inputStream().copyTo(it)
        }
        copyFile
    } else {
        null
    }
}