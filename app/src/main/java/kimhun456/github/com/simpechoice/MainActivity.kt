package kimhun456.github.com.simpechoice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kimhun456.github.com.simpechoice.data.DataSource
import kimhun456.github.com.simpechoice.data.local.DataSourceImpl
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {


    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_IMAGE_CAPTURE = 1117
    }

    private var filePath: Uri? = null
    private var file: File? = null
    private lateinit var dataSource: DataSource


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        dataSource = DataSourceImpl()
        fab.setOnClickListener { view ->
            dispatchTakePictureIntent()
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult requestCode : [$requestCode] resultCode : [$resultCode]")
            Glide.with(image_view)
                .load(filePath)
                .thumbnail(.1f)
                .into(image_view)
            file?.let {
                val savedFile = saveImageFileToExternalStorage(it)
                savedFile?.let { file ->
                    sendBroadCast(file)
                    dataSource.refresh().subscribe({
                        val uriFromContentResolver =
                            dataSource.getUriFromFile(context = applicationContext, file = file)
                        Log.d(TAG, uriFromContentResolver.toString())
                        select_image.apply {
                            visibility = View.VISIBLE
                        }
                    }, { throwable ->
                        Log.e(TAG, throwable.toString())
                    })
                }
            }
        }
    }


    private fun sendBroadCast(file: File) {
        Log.d(TAG, "sendBroadCast()")
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val uri = Uri.fromFile(file)
            Log.d(TAG, " uri : $uri")
            mediaScanIntent.data = uri
            sendBroadcast(mediaScanIntent)
        }
    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile = try {
                    createLocalImage(applicationContext)
                } catch (exception: IOException) {
                    Log.e(TAG, exception.toString())
                    null
                }
                photoFile?.also {
                    file = photoFile
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "kimhun456.github.com.simpechoice",
                        it
                    )
                    Log.d(TAG, "photoURI : $photoURI ")
                    filePath = photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }
}
