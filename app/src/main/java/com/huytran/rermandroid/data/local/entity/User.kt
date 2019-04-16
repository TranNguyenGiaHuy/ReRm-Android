package com.huytran.rermandroid.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "user",
    indices = [
        Index(value = ["name", "svId"], unique = true)
    ]
)
data class User (
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "svId") val svId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "avatar_id") val avatarId: Long,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "id_card") val idCard: String,
    @ColumnInfo(name = "ts_card_dated") val tsCardDated: Long,
    @ColumnInfo(name = "ts_date_of_birth") val tsDateOfBirth: Long,
    @ColumnInfo(name = "place_of_permanent") val placeOfPermanent: String
)