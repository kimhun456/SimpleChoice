package kimhun456.github.com.simplechoice.data.local

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kimhun456.github.com.simplechoice.data.DataSource
import java.io.File
import java.util.concurrent.TimeUnit

class DataSourceImpl : DataSource {
    override fun refresh(): Completable {
        return Completable.complete()
            .delay(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


    override fun getUriFromFile(context: Context, file: File): Uri? {
        val fileUri = Uri.parse(file.path)
        val filePath = fileUri.path
        context.contentResolver
            .query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                "_data = '$filePath'",
                null,
                null
            )?.use {
                it.moveToNext()
                val id = it.getLong(it.getColumnIndex(MediaStore.Images.ImageColumns._ID))
                return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            }
        return null
    }
}