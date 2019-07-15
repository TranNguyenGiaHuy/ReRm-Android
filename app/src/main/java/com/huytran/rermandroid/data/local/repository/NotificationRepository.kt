package com.huytran.rermandroid.data.local.repository

import com.huytran.rermandroid.data.local.dao.NotificationDAO
import com.huytran.rermandroid.data.local.entity.Notification
import io.reactivex.Completable
import io.reactivex.Single

class NotificationRepository(private val notificationDAO: NotificationDAO) {

    fun getAll(): Single<List<Notification>> {
        return notificationDAO.getAll()
    }

    fun insert(notification: Notification): Completable {
        return notificationDAO.insert(notification)
    }

    fun getLastSingle(): Single<List<Notification>> {
        return notificationDAO.getAllSingle()
    }

}