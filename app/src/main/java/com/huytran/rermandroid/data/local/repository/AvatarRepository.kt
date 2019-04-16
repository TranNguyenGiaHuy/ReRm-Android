package com.huytran.rermandroid.data.local.repository

import com.huytran.rermandroid.data.local.dao.AvatarDAO
import com.huytran.rermandroid.data.local.entity.Avatar
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class AvatarRepository(private val avatarDAO: AvatarDAO) {

    fun getAll(): Single<List<Avatar>> {
        return avatarDAO.getAll()
    }

    fun insert(avatar: Avatar): Completable {
        return avatarDAO.insert(avatar)
    }

    fun getLastFlowable(): Flowable<Avatar> {
        return avatarDAO.getLastFlowable()
    }

}