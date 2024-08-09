package com.mj.core.ktx

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> String?.parseJson(): T? =
    Gson().fromJson(this)

inline fun <reified T> Gson.fromJson(json: String?): T =
    fromJson(json, object : TypeToken<T>() {}.type)

inline fun <reified T> T.toJson(): String =
    Gson().toJson(this, object : TypeToken<T>() {}.type)