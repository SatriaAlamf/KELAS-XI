package com.example.whatsapp.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromListString(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringMap(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType) ?: emptyMap()
    }

    @TypeConverter
    fun fromMapString(map: Map<String, String>): String {
        return gson.toJson(map)
    }

    @TypeConverter
    fun fromStringIntMap(value: String): Map<String, Int> {
        val mapType = object : TypeToken<Map<String, Int>>() {}.type
        return gson.fromJson(value, mapType) ?: emptyMap()
    }

    @TypeConverter
    fun fromIntMapString(map: Map<String, Int>): String {
        return gson.toJson(map)
    }

    @TypeConverter
    fun fromStringDateMap(value: String): Map<String, Date> {
        val mapType = object : TypeToken<Map<String, Long>>() {}.type
        val longMap: Map<String, Long> = gson.fromJson(value, mapType) ?: emptyMap()
        return longMap.mapValues { Date(it.value) }
    }

    @TypeConverter
    fun fromDateMapString(map: Map<String, Date>): String {
        val longMap = map.mapValues { it.value.time }
        return gson.toJson(longMap)
    }
}