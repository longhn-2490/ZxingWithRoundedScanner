package com.example.zxingwithroundedscanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class ScanFragment : Fragment() {
    private lateinit var manager: BarcodeManager
    private lateinit var scanner: DecoratedBarcodeView
    private lateinit var txtResult: AppCompatTextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scanner = view.findViewById(R.id.bcScanner)
        txtResult = view.findViewById(R.id.txtResult)
        setUpCamera()
    }

    private fun setUpCamera() {
        manager = BarcodeManager(this, scanner)
        scanner.decodeContinuous { processCodeResult(it) }
    }

    private fun processCodeResult(barcodeResult: BarcodeResult) {
        txtResult.text = barcodeResult.text
    }
}
