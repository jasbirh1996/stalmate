package com.nayaeducation.user.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class MessageModel(
    val chats: Chats,
    val message: String,
    val result: Boolean
)

data class Chats(
    val current_page: Int,
    val `data`: ArrayList<Message>,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: Any,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    var messageid: Long,
    @ColumnInfo(name = "reciever_id")
    val reciever_id: String,
    @ColumnInfo(name = "sender_id")
    val sender_id: String,
    @ColumnInfo(name = "sender_type")
    val sender_type: String,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "user_name")
    val user_name: String,
    @ColumnInfo(name = "file_type")
    var file_type:String,
    @ColumnInfo(name = "is_file")
    var is_file:Int,
    @ColumnInfo(name = "position")
    var position:String,
    @ColumnInfo(name = "status")
var status:String
)