package com.huytran.rermandroid.data.local.dao


import androidx.room.Dao
import androidx.room.Query
import com.huytran.rermandroid.data.local.dao.base.BaseDAO
import com.huytran.rermandroid.data.local.entity.User
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface UserDAO: BaseDAO<User> {

    @Query("select * from user where id = :id")
    fun getById(id: Long): User

    @Query("select * from user")
    fun getAll(): List<User>

    @Query("select * from user order by id desc limit 1")
    fun getLastMaybe(): Maybe<User>

    @Query("select * from user order by id desc limit 1")
    fun getLastFlowable(): Flowable<User>

    @Query("select * from user order by id desc limit 1")
    fun getLastSingle(): Single<User>

}