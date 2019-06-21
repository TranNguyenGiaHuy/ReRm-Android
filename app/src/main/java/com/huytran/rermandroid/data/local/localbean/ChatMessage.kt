package com.huytran.rermandroid.data.local.localbean

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import java.util.*

class ChatMessage(
    private val id: Long,
    private val tsCreated: Long,
    private val user: ChatUser,
    private val content: String
) : IMessage {

    override fun getId(): String = id.toString()

    override fun getCreatedAt(): Date = Date(tsCreated)

    override fun getUser(): IUser = user

    override fun getText(): String = content

    data class Builder(
        var id: Long? = null,
        var tsCreated: Long? = null,
        var user: ChatUser? = null,
        var content: String? = null
    ) {
        fun id(id: Long) = apply { this.id = id }
        fun tsCreated(tsCreated: Long) = apply { this.tsCreated = tsCreated }
        fun user(user: ChatUser) = apply { this.user = user }
        fun content(content: String) = apply { this.content = content }
        fun build() = ChatMessage(id!!, tsCreated!!, user!!, content!!)
    }
}