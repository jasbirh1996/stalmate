package com.stalmate.user.view.dashboard.Chat

import androidx.room.*
import com.nayaeducation.user.model.Message


@Dao
interface MessageDao {
    @get:Query("SELECT * FROM messages")
    val all: List<Message>
    @Insert
    fun insert(task: Message)

    @Delete
    fun delete(task: Message)

    @Update
    fun update(task: Message)
}