package com.huytran.rermandroid.data.local.localbean

import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IUser

class ChatDialog(
    private val id: String,
    private val dialogPhoto: String,
    private val unreadCount: Int,
    private var lastMessage: ChatMessage,
    private val users: MutableList<ChatUser>,
    private val dialogName: String
) : IDialog<ChatMessage> {

    override fun getDialogPhoto() =  dialogPhoto

    override fun getUnreadCount() = unreadCount

    override fun setLastMessage(message: ChatMessage?) {
        message?.let {
            this.lastMessage = message
        }
    }

    override fun getId() = id

    override fun getUsers() = users

    override fun getLastMessage() = lastMessage

    override fun getDialogName() = dialogName

    data class Builder(
        var id: String? = null,
        var dialogPhoto: String? = null,
        var unreadCount: Int? = null,
        var lastMessage: ChatMessage? = null,
        var users: MutableList<ChatUser>? = null,
        var dialogName: String? = null) {

        fun id(id: String) = apply { this.id = id }
        fun dialogPhoto(dialogPhoto: String) = apply { this.dialogPhoto = dialogPhoto }
        fun unreadCount(unreadCount: Int) = apply { this.unreadCount = unreadCount }
        fun lastMessage(lastMessage: ChatMessage) = apply { this.lastMessage = lastMessage }
        fun users(users: MutableList<ChatUser>) = apply { this.users = users }
        fun dialogName(dialogName: String) = apply { this.dialogName = dialogName }
        fun build() = ChatDialog(id!!, dialogPhoto!!, unreadCount!!, lastMessage!!, users!!, dialogName!!)

    }

}