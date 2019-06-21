package com.huytran.rermandroid.data.local.dao.base
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import io.reactivex.Completable
import io.reactivex.Single

interface BaseDAO<T> {

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insert(obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: T): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingle(obj: T): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg obj: T)

    @Update
    fun update(obj: T)

    @Delete
    fun delete(obj: T)

}