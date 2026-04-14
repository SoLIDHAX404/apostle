package com.solidhax.apostle.ui.setting

import com.google.gson.Gson
import com.google.gson.JsonElement

interface Savable {
    fun read(element: JsonElement, gson: Gson)
    fun write(gson: Gson): JsonElement
}