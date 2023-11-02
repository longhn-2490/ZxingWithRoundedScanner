package com.example.zxingwithroundedscanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.zxingwithroundedscanner.databinding.FragmentScanBinding
import com.journeyapps.barcodescanner.BarcodeResult

class ScanFragment : Fragment() {

    lateinit var binding: FragmentScanBinding

    private val manager by lazy {
        BarcodeManager(this, binding.bcScanner, onCameraPermissionDenied = {})
    }

    private var enableTouch = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
    }

    private fun processCodeResult(barcodeResult: BarcodeResult) {
        binding.txtResult.text = barcodeResult.text
        stopDecoding()
        parentFragmentManager.beginTransaction()
            .add(R.id.rootView, ResultFragment.newInstance(barcodeResult.text))
            .addToBackStack(null)
            .commit()
    }

    private fun enableCameraDecoding() {
        binding.bcScanner.decodeContinuous { processCodeResult(it) }
    }

    private fun stopDecoding() {
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

    private fun observeFragmentResult() {
        activity?.supportFragmentManager?.setFragmentResultListener(
            "CODE",
            viewLifecycleOwner
        ) { _, _ ->
            enableCameraDecoding()
        }
    }
}
