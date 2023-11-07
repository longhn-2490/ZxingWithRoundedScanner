package com.example.zxingwithroundedscanner

import java.util.Calendar

data class CodeItem(val id: Long = System.currentTimeMillis(), val content: String, val date: String = Calendar.getInstance().time.toString())
