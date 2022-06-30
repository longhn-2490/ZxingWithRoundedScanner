package com.example.zxingwithroundedscanner

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.*

class BarcodeManager(
    private val fragment: Fragment,
    private val barcodeView: DecoratedBarcodeView
) : DefaultLifecycleObserver {

    private val activity: Activity
        get() = fragment.requireActivity()

    private lateinit var beepManager: BeepManager

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            barcodeView.pause()
            beepManager.playBeepSoundAndVibrate()
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    init {
        initViews()
    }

    private var askedPermission = false
    private fun openCameraWithPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            barcodeView.resume()
        } else if (!askedPermission) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                cameraPermissionReqCode
            )
            askedPermission = true
        } // else wait for permission result
    }

    private fun initViews() {
        fragment.lifecycle.addObserver(this)
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        barcodeView.decoderFactory =
//            DefaultDecoderFactory(ArrayList<BarcodeFormat>().apply { add(BarcodeFormat.QR_CODE) })
        beepManager = BeepManager(activity)
    }

    fun decode() {
        barcodeView.decodeSingle(callback)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        openCameraWithPermission()
    }

    override fun onPause(owner: LifecycleOwner) {
        barcodeView.pauseAndWait()
        super.onPause(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onDestroy()
        super.onDestroy(owner)
    }

    private fun onDestroy() {
        fragment.requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    companion object {
        private const val cameraPermissionReqCode = 250
        private val TAG = BarcodeManager::class.java.simpleName
    }
}
