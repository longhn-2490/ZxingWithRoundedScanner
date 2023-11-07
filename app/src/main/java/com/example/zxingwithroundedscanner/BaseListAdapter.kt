package com.example.zxingwithroundedscanner

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("UNCHECKED_CAST")
abstract class BaseListAdapter<T : Any, B : ViewDataBinding>(
    callBack: BaseDiffUtil<T> = BaseDiffUtil()
) : ListAdapter<T, BaseViewHolder<T, B>>(
    AsyncDifferConfig.Builder<T>(callBack)
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<T, B> {
        val holder = createCustomViewHolder(parent, viewType) as? BaseViewHolder<*, *>
            ?: throw ClassCastException("Please create BaseViewHolder")
        holder.create()
        return holder as BaseViewHolder<T, B>
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T, B>, position: Int) {
        getItem(position)?.let { holder.bind(it) }
        holder.binding.executePendingBindings()
    }

    fun submitItem(item: T?) {
        submitList(item?.let { listOf(it) })
    }

    fun submitItem(item: T?, commitCallback: () -> Unit) {
        submitList(item?.let { listOf(it) }) {
            commitCallback()
        }
    }

    suspend fun clearData() {
        return suspendCoroutine {
            submitList(null) { it.resume(Unit) }
        }
    }

    protected abstract fun createCustomViewHolder(parent: ViewGroup, viewType: Int = 0): Any
}
