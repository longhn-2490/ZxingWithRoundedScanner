package com.example.zxingwithroundedscanner

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("isVisible")
    fun isVisible(view: View, isShowing: Boolean?) {
        view.isVisible = isShowing == true
    }
}