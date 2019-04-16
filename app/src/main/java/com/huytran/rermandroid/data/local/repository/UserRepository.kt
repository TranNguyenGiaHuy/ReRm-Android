package com.huytran.rermandroid.data.local.repository

import com.huytran.rermandroid.data.local.dao.UserDAO
import com.huytran.rermandroid.data.local.entity.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

class UserRepository(private val userDAO: UserDAO) {

    fun addUser(user: User): Completable {
        return userDAO.insert(user)
    }

    fun getLastMaybe(): Maybe<User> {
        return userDAO.getLastMaybe()
    }

    fun getLastFlowable(): Flowable<User> {
        return userDAO.getLastFlowable()
    }

    fun getLastSingle(): Single<User> {
        return userDAO.getLastSingle()
    }

}