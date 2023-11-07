package com.example.zxingwithroundedscanner

import androidx.recyclerview.widget.DiffUtil

open class BaseDiffUtil<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return false
    }
}
