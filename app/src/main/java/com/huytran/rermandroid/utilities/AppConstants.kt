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
        MESSAGE_TYPE_CHAT(4),
        MESSAGE_TYPE_RENT_REQUEST(5),
        MESSAGE_TYPE_CANCEL_RENT_REQUEST_TO_OWNER(6),
        MESSAGE_TYPE_CANCEL_RENT_REQUEST_TO_RENTER(7),
        MESSAGE_TYPE_OWNER_ACCEPTED_ANOTHER_REQUEST(8),
        MESSAGE_TYPE_RENT_SUCCESS(9),
        MESSAGE_TYPE_CONTRACT_TERMINATE(10),
        MESSAGE_TYPE_ADD_PAYMENT(11),
        MESSAGE_TYPE_BILL(12),
        MESSAGE_TYPE_PAYMENT_REQUEST(13),
        MESSAGE_TYPE_CONFIRM_PAYMENT(14);

        companion object {
            fun toRoomType(value: Int?): NotificationType {
                return when (value) {
                    1 -> MESSAGE_TYPE_MESSAGE
                    2 -> MESSAGE_TYPE_NOTIFICATION
                    3 -> MESSAGE_TYPE_VIDEO_CALL
                    4 -> MESSAGE_TYPE_CHAT
                    5 -> MESSAGE_TYPE_RENT_REQUEST
                    6 -> MESSAGE_TYPE_CANCEL_RENT_REQUEST_TO_OWNER
                    7 -> MESSAGE_TYPE_CANCEL_RENT_REQUEST_TO_RENTER
                    8 -> MESSAGE_TYPE_OWNER_ACCEPTED_ANOTHER_REQUEST
                    9 -> MESSAGE_TYPE_RENT_SUCCESS
                    10 -> MESSAGE_TYPE_CONTRACT_TERMINATE
                    11 -> MESSAGE_TYPE_ADD_PAYMENT
                    12 -> MESSAGE_TYPE_BILL
                    13 -> MESSAGE_TYPE_PAYMENT_REQUEST
                    14 -> MESSAGE_TYPE_CONFIRM_PAYMENT
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

    enum class PaymentStatus(val raw: Int) {
        WAITING_BILL(0),
        WAITING_PAYMENT(1),
        WAITING_CONFIRM(2),
        DONE(3),
    }

}