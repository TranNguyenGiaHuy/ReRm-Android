package com.huytran.rermandroid.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.huytran.rermandroid.data.local.dao.base.BaseDAO
import com.huytran.rermandroid.data.local.entity.Message
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface MessageDAO: BaseDAO<Message> {

    @Query("select * from message where id = :id")
    fun getById(id: Long): Message

    @Query("select * from message")
    fun getAll(): List<Message>

    @Query("select * from message")
    fun getAllFlowable(): Flowable<List<Message>>

    @Query("select * from message")
    fun getAllSingle(): Single<List<Message>>

    @Query("select * from message where user_id = :userId")
    fun getAllOfUserMaybe(userId: Long): Maybe<List<Message>>

    @Query("select * from message where user_id = :userId")
    fun getAllOfUserFlowable(userId: Long): Flowable<List<Message>>

    @Query("select * from message where user_id = :userId ORDER BY id DESC LIMIT 1")
    fun getLastOfUserFlowable(userId: Long): Flowable<Message>

    @Query("select count(*) from message where user_id = :userId and not is_seen")
    fun countUnseenMessageOfUser(userId: Long): Long

}