package com.huytran.rermandroid.data.local.dao


import androidx.room.Dao
import androidx.room.Query
import com.huytran.rermandroid.data.local.dao.base.BaseDAO
import com.huytran.rermandroid.data.local.entity.Avatar
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface AvatarDAO: BaseDAO<Avatar> {

    @Query("select * from avatar where id = :id")
    fun getById(id: Long): Maybe<Avatar>

    @Query("select * from avatar")
    fun getAll(): Single<List<Avatar>>

}