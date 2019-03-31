package com.huytran.rermandroid.data.local.repository

import com.huytran.rermandroid.data.local.dao.UserDAO
import com.huytran.rermandroid.data.local.entity.User
import io.reactivex.Observable

class UserRepository(private val userDAO: UserDAO) {

    fun addUser(user: User) {
        Observable.fromCallable {
            userDAO.insert(user)
        }
    }

    fun getLast(): User {
        return userDAO.getLast()
    }

}