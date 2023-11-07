package com.example.zxingwithroundedscanner

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.zxingwithroundedscanner.databinding.FragmentResultBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ResultFragment : Fragment() {
    private var result: String = ""
    lateinit var binding: FragmentResultBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_result, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            executePendingBindings()
            lifecycleOwner = this@ResultFragment.viewLifecycleOwner
        }
        binding.txtResult.text = result

        binding.imgBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.imgCopy.setOnClickListener {
            val clipboard =
                context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clipData = ClipData.newPlainText("code", result)
            clipboard?.setPrimaryClip(clipData)
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        binding.imgBrowser.setOnClickListener {
            activity?.launchBrowser(result)
        }

        binding.imgShare.setOnClickListener {
            startShareLink(result)
        }

        binding.txtCopy.setOnClickListener {
            val clipboard =
                context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clipData = ClipData.newPlainText("code", result)
            clipboard?.setPrimaryClip(clipData)
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        binding.txtBrowser.setOnClickListener {
            activity?.launchBrowser(result)
        }

        binding.txtShare.setOnClickListener {
            startShareLink(result)
        }
    }

    override fun onDestroy() {
        parentFragmentManager.setFragmentResult("CODE", bundleOf())
        super.onDestroy()
    }

    fun Activity.launchBrowser(url: String) {
        try {
            val defaultBrowser = Intent.makeMainSelectorActivity(
                Intent.ACTION_MAIN,
                Intent.CATEGORY_APP_BROWSER
            ).apply {
                data = Uri.parse(url.toStandardUrl())
            }
            startActivity(defaultBrowser)
        } catch (e: Exception) {
            CustomTabsHelper.launchCustomTab(this, url)
        }
    }

    fun startShareLink(link: String) {
        val imageUri = saveThumbnail(requireContext(), R.drawable.ic_back) ?: return
        val shareIntent = activity?.createShareIntent(
            "text/plain",
            imageUri,
            link,
            getString(R.string.app_name)
        )
        val intent = Intent.createChooser(shareIntent, null)
        this.startActivity(intent)
    }

    fun getClipDataThumbnail(uri: Uri, contentResolver: ContentResolver): ClipData? {
        return try {
            ClipData.newUri(contentResolver, null, uri)
        } catch (e: FileNotFoundException) {
            null
        } catch (e: IOException) {
            null
        }
    }

    fun Activity.createShareIntent(
        intentType: String,
        imageUri: Uri,
        content: String,
        title: String
    ): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            type = intentType
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_TITLE, title)
            val thumbnail = getClipDataThumbnail(imageUri, contentResolver)
            thumbnail?.let {
                this.clipData = it
                this.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            this.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
    }

    @Throws(IOException::class)
    fun saveThumbnail(
        context: Context,
        drawable: Int
    ): Uri? {
        var stream: FileOutputStream? = null
        var imagePath: File? = null
        var cachePath: File? = null
        return try {
            val filename = PRE_FILE_NAME + System.currentTimeMillis() + PNG_EXTENSION
            val bm: Bitmap? =
                ResourcesCompat.getDrawable(context.resources, drawable, null)?.toBitmap()
            cachePath = File(context.cacheDir, CACHE)
            cachePath.mkdirs()
            stream = FileOutputStream("$cachePath/$filename")
            bm?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            imagePath = File(context.cacheDir, CACHE)
            val newFile = File(imagePath, filename)
            FileProvider.getUriForFile(context, APP_FILE_PROVIDER, newFile)
        } catch (e: Throwable) {
            null
        } finally {
            stream?.flush()
            stream?.close()
            cachePath?.deleteIfExist()
            imagePath?.deleteIfExist()
        }
    }

    fun File.deleteIfExist() {
        if (exists()) delete()
    }

    companion object {
        private const val JPG_EXTENSION = ".jpg"
        private const val PNG_EXTENSION = ".png"
        private const val PRE_FILE_NAME = "Hizakurige"
        private const val APP_IMAGE_FOLDER = "/Hizakurige/"
        private const val CACHE = "cache"
        const val IMAGE_TYPE = "image/*"
        const val APP_FILE_PROVIDER = BuildConfig.APPLICATION_ID.plus(".provider")
        fun newInstance(result: String) = ResultFragment().apply {
            this.result = result
        }
    }
}
