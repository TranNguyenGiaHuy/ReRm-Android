package com.huytran.rermandroid.data.local.localbean

import com.stfalcon.chatkit.commons.models.IUser

class ChatUser(private val id: Long, private val name: String, private val avatarPath: String) : IUser {

    override fun getAvatar(): String = avatarPath

    override fun getName(): String = name

    override fun getId(): String = id.toString()

    data class Builder(var id: Long? = null, var name: String? = null, var avatarPath: String? = null) {

        fun id(id: Long) = apply { this.id = id }
        fun name(name: String) = apply { this.name = name }
        fun avatarPath(avatarPath: String) = apply { this.avatarPath = avatarPath }
        fun build() = ChatUser(id!!, name!!, avatarPath!!)

    }
}