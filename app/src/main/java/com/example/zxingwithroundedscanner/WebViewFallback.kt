package com.example.zxingwithroundedscanner

import android.app.Activity

interface CustomTabFallback {
    fun openUri(activity: Activity?, url: String)
}
class WebViewFallback : CustomTabFallback {
    override fun openUri(activity: Activity?, url: String) {
        (activity as? MainActivity)?.goToWebView(url)
    }
}