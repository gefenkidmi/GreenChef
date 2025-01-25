package com.example.greenchef.DB

import androidx.room.TypeConverter
import com.example.greenchef.DataClass.Comment
import com.example.greenchef.DataClass.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : com.google.common.reflect.TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromCommentList(comments: List<Comment>?): String? {
        return Gson().toJson(comments)
    }

    @TypeConverter
    fun toCommentList(commentsString: String?): List<Comment>? {
        return Gson().fromJson(commentsString, object : TypeToken<List<Comment>>() {}.type)
    }

    @TypeConverter
    fun fromUser(user: User): String {
        return Gson().toJson(user)
    }

    @TypeConverter
    fun toUser(json: String): User {
        return Gson().fromJson(json, User::class.java)
    }
}