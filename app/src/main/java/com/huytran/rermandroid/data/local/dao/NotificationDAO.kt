package com.huytran.rermandroid.data.local.dao


import androidx.room.Dao
import androidx.room.Query
import com.huytran.rermandroid.data.local.dao.base.BaseDAO
import com.huytran.rermandroid.data.local.entity.Notification
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface NotificationDAO: BaseDAO<Notification> {

    @Query("select * from notification where id = :id")
    fun getById(id: Long): Maybe<Notification>

    @Query("select * from notification")
    fun getAll(): Single<List<Notification>>

    @Query("select * from notification")
    fun getAllSingle(): Single<List<Notification>>

}