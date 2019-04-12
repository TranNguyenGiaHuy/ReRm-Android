package com.huytran.rermandroid.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "contract",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class Contract (
    @PrimaryKey(autoGenerate = true) val id: Long? = null

)