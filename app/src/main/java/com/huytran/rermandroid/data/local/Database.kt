package com.huytran.rermandroid.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.huytran.rermandroid.data.local.dao.AvatarDAO
import com.huytran.rermandroid.data.local.dao.UserDAO
import com.huytran.rermandroid.data.local.entity.Avatar
import com.huytran.rermandroid.data.local.entity.User

@Database(
    entities = [
        User::class,
        Avatar::class
    ],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {

    abstract fun userDAO(): UserDAO

    abstract fun avatarDAO(): AvatarDAO

    companion object {
        const val DATABASE_NAME = "database.db"
    }
}