package com.huytran.rermandroid.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.huytran.rermandroid.data.local.dao.UserDAO
import com.huytran.rermandroid.data.local.entity.User

@Database(
    entities = [
        User::class
    ],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {

    abstract fun userDAO(): UserDAO

    companion object {
        const val DATABASE_NAME = "database.db"
    }
}