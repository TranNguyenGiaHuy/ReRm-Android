package com.huytran.rermandroid.data.local.repository

import com.huytran.rermandroid.data.local.dao.MessageDAO
import com.huytran.rermandroid.data.local.dao.UserDAO
import com.huytran.rermandroid.data.local.entity.Message
import com.huytran.rermandroid.data.local.entity.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

class MessageRepository(private val messageDAO: MessageDAO) {

    fun addMessage(message: Message): Completable {
        return messageDAO.insert(message)
    }

    fun addMessageSingle(message: Message): Single<Long> {
        return messageDAO.insertSingle(message)
    }

    fun getById(id: Long): Message = messageDAO.getById(id)

    fun getAll(): List<Message> = messageDAO.getAll()

    fun getAllSingle(): Single<List<Message>> = messageDAO.getAllSingle()

    fun getAllFlowable(): Flowable<List<Message>> = messageDAO.getAllFlowable()

    fun getAllOfUserMaybe(userId: Long): Maybe<List<Message>> = messageDAO.getAllOfUserMaybe(userId)

    fun getAllOfUserFlowable(userId: Long): Flowable<List<Message>> = messageDAO.getAllOfUserFlowable(userId)

    fun getLastOfUserFlowable(userId: Long): Flowable<Message> = messageDAO.getLastOfUserFlowable(userId)

    fun countUnseenMessageOfUser(userId: Long): Long = messageDAO.countUnseenMessageOfUser(userId)

}