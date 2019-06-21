package com.huytran.rermandroid.utilities

class AppConstants {

    companion object {

        const val REQUEST_TIMEOUT : Long = 8*1000
        const val KEY_AVATAR = "AVATAR"
        const val KEY_AVATAR_OF_USER = "AVATAR_"
        const val KEY_IMAGE_WITH_ID = "IMAGE_"
        const val SERVER_ADDRESS = "10.0.2.2"
    }

    enum class NotificationType(raw: Int) {
        MESSAGE_TYPE_MESSAGE(1),
        MESSAGE_TYPE_NOTIFICATION(2),
        MESSAGE_TYPE_VIDEO_CALL(3),
        MESSAGE_TYPE_CHAT(4);

        companion object {
            fun toRoomType(value: Int?): NotificationType {
                return when (value) {
                    1 -> MESSAGE_TYPE_MESSAGE
                    2 -> MESSAGE_TYPE_NOTIFICATION
                    3 -> MESSAGE_TYPE_VIDEO_CALL
                    4 -> MESSAGE_TYPE_CHAT
                    else -> MESSAGE_TYPE_MESSAGE
                }
            }
        }
    }

    enum class RoomType(val raw: Long, name: String) {
        HOME(0, "Home"),
        ROOM(1, "Room"),
        DORM(2, "Dormitory")
    }

}