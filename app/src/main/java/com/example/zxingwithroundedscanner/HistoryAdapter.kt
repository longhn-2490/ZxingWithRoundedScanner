package com.example.zxingwithroundedscanner

import android.view.ViewGroup
import com.example.zxingwithroundedscanner.databinding.CodeItemBinding

class HistoryAdapter(
    private val onCodeClick: (CodeItem) -> Unit,
    private val onDeleteClick: (CodeItem) -> Unit
) : BaseListAdapter<CodeItem, CodeItemBinding>(object : BaseDiffUtil<CodeItem>() {
    override fun areItemsTheSame(oldItem: CodeItem, newItem: CodeItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CodeItem, newItem: CodeItem): Boolean {
        return oldItem == newItem
    }
}) {
    override fun createCustomViewHolder(parent: ViewGroup, viewType: Int): Any {
        return CodeViewHolder(parent, onCodeClick, onDeleteClick)
    }
}
