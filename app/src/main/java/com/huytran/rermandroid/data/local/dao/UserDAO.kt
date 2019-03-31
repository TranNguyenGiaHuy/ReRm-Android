package com.huytran.rermandroid.data.local.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.huytran.rermandroid.data.local.dao.base.BaseDAO
import com.huytran.rermandroid.data.local.entity.User

@Dao
interface UserDAO: BaseDAO<User> {

    @Query("select * from user where id = :id")
    fun getById(id: Long): User

    @Query("select * from user")
    fun getAll(): List<User>

    @Query("select * from user order by id desc limit 1")
    fun getLast(): User

}