package com.example.zxingwithroundedscanner

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

inline fun <reified B : ViewDataBinding> inflateView(
    @LayoutRes layoutRes: Int,
    parent: ViewGroup
): B {
    return DataBindingUtil.inflate(
        LayoutInflater.from(parent.context), layoutRes, parent, false
    )
}

@SuppressLint("QueryPermissionsNeeded")
fun Context.openAppDetailSettings() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
    }
}