package com.example.zxingwithroundedscanner

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.zxingwithroundedscanner.databinding.FragmentScanBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.journeyapps.barcodescanner.BarcodeResult
import java.io.IOException


class ScanFragment : Fragment(), SelectPhotoHelper.OnSelectPhotoListener, OnFragmentChangeListener {

    lateinit var binding: FragmentScanBinding

    private val manager by lazy {
        BarcodeManager(this, binding.bcScanner, onCameraPermissionDenied = ::onPermissionDenied)
    }

    private val selectPhotoHelper = SelectPhotoHelper(this)

    private var enableTouch = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            executePendingBindings()
            lifecycleOwner = this@ScanFragment.viewLifecycleOwner
        }
        if (savedInstanceState == null) {
            manager.onViewCreated()
        }
        enableCameraDecoding()
        observeFragmentResult()
        binding.imgTorch.setOnClickListener {
            toggleTorch()
        }

        binding.imgQRcode.setOnClickListener {
            selectPhotoHelper.selectPhotoByAction(SelectPhotoHelper.ACTION_SELECT_PHOTO)
        }

        binding.layoutGotoSetting.setOnClickListener {
            requireActivity().openAppDetailSettings()
        }
    }

    override fun onResume() {
        super.onResume()
        if (requireContext().hasCameraPermission()) {
            binding.layoutGotoSetting.gone()
        } else {
            binding.layoutGotoSetting.visible()
        }
    }

    private fun processCodeResult(barcodeResult: BarcodeResult) {
        stopDecoding()
        gotoResultFragment(barcodeResult.text)
    }

    private fun gotoResultFragment(result: String) {
        parentFragmentManager.beginTransaction()
            .add(R.id.rootView, ResultFragment.newInstance(result))
            .addToBackStack(null)
            .commit()
        (activity as MainActivity)
    }

    fun enableCameraDecoding() {
        binding.bcScanner.decodeContinuous { processCodeResult(it) }
    }

    fun stopDecoding() {
        binding.bcScanner.barcodeView.stopDecoding()
    }

    private fun toggleTorch() {
        if (enableTouch) {
            binding.bcScanner.setTorchOff()
        } else {
            binding.bcScanner.setTorchOn()
        }
        enableTouch = !enableTouch
    }

    fun torchOff() {
        binding.bcScanner.setTorchOff()
        enableTouch = false
    }

    private fun observeFragmentResult() {
        activity?.supportFragmentManager?.setFragmentResultListener(
            "CODE",
            viewLifecycleOwner
        ) { _, _ ->
            enableCameraDecoding()
        }
    }

    override fun onSelectPhoto(uri: Uri, action: Int) {
        stopDecoding()
        val code = decodeQRCode(uri, requireContext())
        if (code.isNullOrEmpty()) {
            enableCameraDecoding()
            return
        }
        Log.e("eee", "has $code")
        Global.code.postValue(code)
        gotoResultFragment(code)
    }

    private fun decodeQRCode(imageUri: Uri, context: Context): String? {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(imageUri)

        return try {
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (bitmap != null) {
                decodeQRCodeFromBitmap(bitmap)
            } else {
                Log.e("ImageLoadError", "Failed to load bitmap from URI")
                null
            }
        } catch (e: Exception) {
            Log.e("ImageLoadError", "Error loading image from URI: ${e.message}")
            null
        } finally {
            inputStream?.close()
        }
    }

    private fun decodeQRCodeFromBitmap(bitmap: Bitmap): String? {
        val qrCodeReader = QRCodeReader()
        val hints = mapOf(
            DecodeHintType.TRY_HARDER to BarcodeFormat.QR_CODE
        )

        try {
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            val source: LuminanceSource = RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
            val binarizer = HybridBinarizer(source)
            val binaryBitmap = BinaryBitmap(binarizer)
            val result = qrCodeReader.decode(binaryBitmap, hints)
            return result.text
        } catch (e: NotFoundException) {
            Log.e("QRCodeDecoder", "QR Code not found in the image")
        } catch (e: IOException) {
            Log.e("QRCodeDecoder", "Error decoding QR Code")
        }

        return null
    }

    override fun onChange() {
        enableCameraDecoding()
    }

    private fun onPermissionDenied() {
        binding.layoutGotoSetting.visible()
    }
}

