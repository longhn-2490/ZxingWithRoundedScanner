package com.example.zxingwithroundedscanner

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.zxingwithroundedscanner.databinding.FragmentWebViewBinding

class WebViewFragment : Fragment() {
    private lateinit var webViewUrl: String
    lateinit var binding: FragmentWebViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_web_view, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            executePendingBindings()
            lifecycleOwner = this@WebViewFragment.viewLifecycleOwner
        }

        initDataAndViews()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initDataAndViews() {
        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                builtInZoomControls = true
                displayZoomControls = false
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
            }
            loadUrl(webViewUrl.toStandardUrl())
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                }
            }
        }
    }

    companion object {
        fun newInstance(url: String) = WebViewFragment().apply {
            this.webViewUrl = url
        }
    }
}
