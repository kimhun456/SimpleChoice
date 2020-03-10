package kimhun456.github.com.simplechoice.data

import android.content.Context
import android.net.Uri
import io.reactivex.Completable
import java.io.File

interface DataSource {
    fun refresh(): Completable
    fun getUriFromFile(context: Context, file: File): Uri?
}