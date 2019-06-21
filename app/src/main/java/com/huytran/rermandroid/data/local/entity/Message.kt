package com.huytran.rermandroid.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "message",
    indices = [
        Index(value = ["user_id"], unique = false)
    ]
)
data class Message (
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "ts_created") val tsCreated: Long,
    @ColumnInfo(name = "is_receive") val isReceive: Boolean,
    @ColumnInfo(name = "is_seen") var isSeen: Boolean

)