package com.huytran.rermandroid.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "notification"
)
data class Notification (
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "ts_created") val tsCreated: Long,
    @ColumnInfo(name = "from") val from: Long,
    @ColumnInfo(name = "room_id") val roomId: Long,
    @ColumnInfo(name = "value") val value: Long,
    @ColumnInfo(name = "is_seen") var isSeen: Boolean,
    @ColumnInfo(name = "message") val message: String
)