package com.huytran.rermandroid.utilities

class AppConstants {

    companion object {

        const val REQUEST_TIMEOUT : Long = 8*1000
        const val KEY_AVATAR = "AVATAR"
        const val KEY_AVATAR_OF_USER = "AVATAR_"
        const val KEY_IMAGE_WITH_ID = "IMAGE_"
    }

    enum class RoomType(val raw: Long, name: String) {
        HOME(0, "Home"),
        ROOM(1, "Room"),
        DORM(2, "Dormitory")
    }

}