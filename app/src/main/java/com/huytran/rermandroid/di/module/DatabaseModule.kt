package com.huytran.rermandroid.di.module

import android.content.Context
import androidx.room.Room
import com.huytran.rermandroid.data.local.Database
import com.huytran.rermandroid.data.local.dao.AvatarDAO
import com.huytran.rermandroid.data.local.dao.MessageDAO
import com.huytran.rermandroid.data.local.dao.NotificationDAO
import com.huytran.rermandroid.data.local.dao.UserDAO
import com.huytran.rermandroid.data.local.repository.AvatarRepository
import com.huytran.rermandroid.data.local.repository.MessageRepository
import com.huytran.rermandroid.data.local.repository.NotificationRepository
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.di.scope.ApplicationContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    internal fun provideDatabase(@ApplicationContext context: Context): Database{
        return Room.databaseBuilder(
            context,
            Database::class.java,
            Database.DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    internal fun provideUserDAO(database: Database): UserDAO {
        return database.userDAO()
    }

    @Singleton
    @Provides
    internal fun provideUserRepository(userDAO: UserDAO): UserRepository {
        return UserRepository(userDAO)
    }

    @Singleton
    @Provides
    internal fun provideAvatarDAO(database: Database): AvatarDAO {
        return database.avatarDAO()
    }

    @Singleton
    @Provides
    internal fun provideAvatarRepository(avatarDAO: AvatarDAO): AvatarRepository {
        return AvatarRepository(avatarDAO)
    }

    @Singleton
    @Provides
    internal fun provideMessageDAO(database: Database): MessageDAO {
        return database.messageDAO()
    }

    @Singleton
    @Provides
    internal fun provideMessageRepository(messageDAO: MessageDAO): MessageRepository {
        return MessageRepository(messageDAO)
    }

    @Singleton
    @Provides
    internal fun provideNotificationDAO(database: Database): NotificationDAO {
        return database.noticationDAO()
    }

    @Singleton
    @Provides
    internal fun provideNotificationRepository(notificationDAO: NotificationDAO): NotificationRepository {
        return NotificationRepository(notificationDAO)
    }

}