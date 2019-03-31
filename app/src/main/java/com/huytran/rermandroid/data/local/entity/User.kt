package com.huytran.rermandroid.data.local.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "user")
data class User (
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "avatar_id") val avatarId: Long,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "id_card") val idCard: String,
    @ColumnInfo(name = "ts_card_dated") val tsCardDated: Long,
    @ColumnInfo(name = "ts_date_of_birth") val tsDateOfBirth: Long
)