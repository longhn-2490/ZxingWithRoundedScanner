package com.example.zxingwithroundedscanner

import android.view.ViewGroup
import com.example.zxingwithroundedscanner.databinding.CodeItemBinding

class CodeViewHolder(
    parent: ViewGroup,
    private val onCodeClick: (CodeItem) -> Unit,
    private val onDeleteClick: (CodeItem) -> Unit
) : BaseViewHolder<CodeItem, CodeItemBinding>(
    inflateView(R.layout.code_item, parent)
) {

    init {
        binding.txtCode.setOnClickListener {
            onCodeClick.invoke(binding.item!!)
        }

        binding.imgDelete.setOnClickListener {
            onDeleteClick.invoke(binding.item!!)
        }
    }
}
