package com.huytran.rermandroid.di.module

import android.content.Context
import androidx.room.Room
import com.huytran.rermandroid.data.local.Database
import com.huytran.rermandroid.data.local.dao.UserDAO
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

}