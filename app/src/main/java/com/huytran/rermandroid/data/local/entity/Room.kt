package com.huytran.rermandroid.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "room",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class Room (
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "square") val square: Float,
    @ColumnInfo(name = "price") val price: Long
)