package com.example.zxingwithroundedscanner

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class SharedPrefApi(context: Context, val gson: Gson) {

    val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
    }

    operator fun <T> set(key: String, data: T) {
        val editor = sharedPreferences.edit()
        when (data) {
            is String -> editor.putString(key, data)
            is Boolean -> editor.putBoolean(key, data)
            is Float -> editor.putFloat(key, data)
            is Int -> editor.putInt(key, data)
            is Long -> editor.putLong(key, data)
            else -> editor.putString(key, gson.toJson(data))
        }
        editor.apply()
    }

    @Throws(JsonSyntaxException::class)
    inline operator fun <reified T> get(key: String, default: T? = null): T {
        return when (T::class) {
            String::class -> sharedPreferences.getString(key, default as? String ?: "") as T
            Boolean::class -> sharedPreferences.getBoolean(key, default as? Boolean ?: false) as T
            Float::class -> sharedPreferences.getFloat(key, default as? Float ?: INVALID_FLOAT) as T
            Int::class -> sharedPreferences.getInt(key, default as? Int ?: INVALID_INT) as T
            Long::class -> sharedPreferences.getLong(key, default as? Long ?: INVALID_LONG) as T
            else -> gson.fromJson(sharedPreferences.getString(key, ""), T::class.java)
        }
    }

    inline fun <reified T> getList(key: String): List<T> {
        val typeOfT = TypeToken.getParameterized(List::class.java, T::class.java).type
        return gson.fromJson(get<String>(key), typeOfT) ?: emptyList()
    }

    fun removeKey(vararg key: String) {
        key.forEach {
            sharedPreferences.edit().remove(it).apply()
        }
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}

const val CODE = "code"
const val INVALID_INT = -1
const val INVALID_LONG = -1L
const val INVALID_FLOAT = -1f
const val INVALID_DOUBLE = -1.0