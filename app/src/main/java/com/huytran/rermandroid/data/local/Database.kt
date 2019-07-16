package com.huytran.rermandroid.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.huytran.rermandroid.data.local.dao.AvatarDAO
import com.huytran.rermandroid.data.local.dao.MessageDAO
import com.huytran.rermandroid.data.local.dao.NotificationDAO
import com.huytran.rermandroid.data.local.dao.UserDAO
import com.huytran.rermandroid.data.local.entity.Avatar
import com.huytran.rermandroid.data.local.entity.Message
import com.huytran.rermandroid.data.local.entity.Notification
import com.huytran.rermandroid.data.local.entity.User

@Database(
    entities = [
        User::class,
        Avatar::class,
        Message::class,
        Notification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {

    abstract fun userDAO(): UserDAO

    abstract fun avatarDAO(): AvatarDAO

    abstract fun messageDAO(): MessageDAO

    abstract fun noticationDAO(): NotificationDAO

    companion object {
        const val DATABASE_NAME = "database.db"
    }
}