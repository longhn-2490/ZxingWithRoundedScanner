package com.example.zxingwithroundedscanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.zxingwithroundedscanner.databinding.FragmentHistoryBinding
import com.google.gson.Gson

class HistoryFragment : Fragment() {

    lateinit var binding: FragmentHistoryBinding

    val adapter by lazy {
        HistoryAdapter(::onCodeClick, ::onDeleteClick)
    }

    private val sharedPrefApi by lazy {
        SharedPrefApi(requireContext(), Gson())
    }

    var historyItems = mutableListOf<CodeItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            executePendingBindings()
            lifecycleOwner = this@HistoryFragment.viewLifecycleOwner
        }
        setupRecyclerView()
        handleEvents()
        loadHistories()
        obserNewCode()
    }

    private fun handleEvents() {
        binding.imgDeleteAll.setOnClickListener {
            if (historyItems.isEmpty()) return@setOnClickListener
            (activity as? MainActivity)?.showAlert("Xóa tất cả", negativeButtonText = R.string.no, positiveButtonText = R.string.ok, onPositiveButtonClick = {
                adapter.submitList(null)
                historyItems.clear()
                sharedPrefApi[CODE] = emptyList<CodeItem>()
            })
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerHistory.adapter = adapter
        binding.recyclerHistory.isNestedScrollingEnabled = false
    }

    private fun loadHistories() {
        historyItems = sharedPrefApi.getList<CodeItem>(CODE).toMutableList()
        adapter.submitList(historyItems)
    }

    private fun obserNewCode() {
        Global.code.observe(viewLifecycleOwner ) {
            addItem(CodeItem(content = it))
        }
    }

    private fun onDeleteClick(item: CodeItem) {
        val currentItemIndex = adapter.currentList.indexOf(item)
        historyItems.removeAt(currentItemIndex)
        adapter.notifyItemRemoved(currentItemIndex)
        sharedPrefApi[CODE] = historyItems
    }

    private fun onCodeClick(item: CodeItem) {
    }

    private fun addItem(item: CodeItem) {
        historyItems.add(0, item)
        adapter.notifyItemInserted(0)
        sharedPrefApi[CODE] = historyItems
    }
}

