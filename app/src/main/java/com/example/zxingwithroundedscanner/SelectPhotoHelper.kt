package com.example.zxingwithroundedscanner

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.zxingwithroundedscanner.ResultFragment.Companion.APP_FILE_PROVIDER
import java.io.File
import kotlin.math.abs
import kotlin.random.Random

class SelectPhotoHelper(private val fragment: Fragment) :
    DefaultLifecycleObserver {
    init {
        fragment.lifecycle.addObserver(this)
    }

    private val cameraPermissionObserver = SinglePermissionObserver(
        fragment,
        Manifest.permission.CAMERA,
        onGranted = ::takePicture,
        onPermanentlyDenied = {  }
    )

    private var fileAndUri: Pair<File, Uri>? = null
    var action: Int = ACTION_OTHER

//    private val readWriteExternalStoragePermissionObserver: SinglePermissionObserver =
//        SinglePermissionObserver(fragment, Manifest.permission.READ_EXTERNAL_STORAGE) {
//
//        }

    fun selectPhotoByAction(action: Int) {
        this.action = action
        when (action) {
            ACTION_TAKE_PHOTO -> {
                if (!fragment.requireContext().hasCameraPermission()) {
                    cameraPermissionObserver.requestPermission()
                } else {
                    takePicture()
                }
            }
            ACTION_SELECT_PHOTO -> {
                pickPhotoFromGalleryLauncher.launch(IMAGE_TYPE)
            }
            ACTION_SELECT_MULTI_PHOTO -> {
                pickMultiPhotoFromGalleyLauncher.launch(IMAGE_TYPE)
            }
        }
//        readWriteExternalStoragePermissionObserver.requestPermission()
    }

    fun deleteFile() {
        fileAndUri?.first?.deleteIfExist()
    }

    private val takePicturePreviewLauncher =
        fragment.registerTakePicturePreviewIntent { isSuccess ->
            if (isSuccess && fileAndUri != null) {
                listener?.onSelectPhoto(fileAndUri!!.second, ACTION_TAKE_PHOTO)
            }
        }

    private val pickPhotoFromGalleryLauncher = fragment.registerPickPhotoFromGalleryIntent { uri ->
        listener?.onSelectPhoto(uri, ACTION_SELECT_PHOTO)
    }

    private val pickMultiPhotoFromGalleyLauncher =
        fragment.registerPickMultiPhotoFromGalleyIntent { uris ->
            listener?.onSelectMultiPhoto(uris, ACTION_SELECT_MULTI_PHOTO)
        }

    private fun takePicture() {
        fileAndUri = createImageFileUri(fragment.requireContext())
        takePicturePreviewLauncher.launch(fileAndUri?.second)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        this.listener = fragment as? OnSelectPhotoListener
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        this.listener = null
    }

    private var listener: OnSelectPhotoListener? = null

    interface OnSelectPhotoListener {
        fun onSelectPhoto(uri: Uri, action: Int)
        fun onSelectMultiPhoto(uris: List<Uri>, action: Int) {}
    }

    fun createImageFileUri(context: Context): Pair<File, Uri>? {
        val photoFile = generateTempImageFile(context, JPG_EXTENSION)
        return try {
            photoFile to FileProvider.getUriForFile(context, APP_FILE_PROVIDER, photoFile)
        } catch (e: Throwable) {
            photoFile.deleteIfExist()
            null
        }
    }

    private fun generateTempImageFile(context: Context, extension: String): File {
        val privateTempDir = File(context.cacheDir, PRE_FILE_NAME)
        if (!privateTempDir.exists()) privateTempDir.mkdirs()
        val name = PRE_FILE_NAME + System.currentTimeMillis() + abs(Random.nextInt())
        return File(privateTempDir, name + extension)
    }


    fun File.deleteIfExist() {
        if (exists()) delete()
    }

    fun Fragment.registerTakePicturePreviewIntent(
        onResult: (Boolean) -> Unit
    ): ActivityResultLauncher<Uri?> {
        return registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            onResult(isSuccess)
        }
    }

    fun Fragment.registerPickPhotoFromGalleryIntent(onResult: (Uri) -> Unit): ActivityResultLauncher<String?> {
        return registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let(onResult)
        }
    }

    fun Fragment.registerPickMultiPhotoFromGalleyIntent(onResult: (List<Uri>) -> Unit): ActivityResultLauncher<String> {
        return registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            uris?.filterNotNull()?.let(onResult)
        }
    }

    companion object {
        const val ACTION_TAKE_PHOTO = 0
        const val ACTION_SELECT_PHOTO = 1
        const val ACTION_OTHER = 2
        const val ACTION_SELECT_MULTI_PHOTO = 3

        private const val JPG_EXTENSION = ".jpg"
        private const val PNG_EXTENSION = ".png"
        private const val PRE_FILE_NAME = "QRCode"
        private const val APP_IMAGE_FOLDER = "/QRCode/"
        private const val CACHE = "cache"
        const val IMAGE_TYPE = "image/*"
    }
}
