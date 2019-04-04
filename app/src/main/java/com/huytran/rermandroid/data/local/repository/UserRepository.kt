package com.huytran.rermandroid.data.local.repository

import com.huytran.rermandroid.data.local.dao.UserDAO
import com.huytran.rermandroid.data.local.entity.User
import io.reactivex.Completable
import io.reactivex.Maybe

class UserRepository(private val userDAO: UserDAO) {

    fun addUser(user: User): Completable {
        return userDAO.insert(user)
    }

    fun getLast(): Maybe<User> {
        return userDAO.getLast()
    }

}