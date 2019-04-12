package com.huytran.rermandroid.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "avatar",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class Avatar (
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "name") val name: String
)