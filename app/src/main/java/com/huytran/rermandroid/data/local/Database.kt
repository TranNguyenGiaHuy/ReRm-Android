package com.huytran.rermandroid.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
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
        val DATABASE_NAME = "database.db"
    }
}