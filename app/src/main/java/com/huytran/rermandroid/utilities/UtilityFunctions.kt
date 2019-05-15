package com.huytran.rermandroid.utilities

import android.content.SharedPreferences
import io.grpc.Metadata
import java.text.SimpleDateFormat

class UtilityFunctions {

    companion object {

        private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        fun tokenHeader(privatePreferences: SharedPreferences): Metadata? {
            val token = privatePreferences.getString("session", null) ?: return null

            val header = Metadata()
            header.put(
                Metadata.Key.of(
                    "Authorization",
                    Metadata.ASCII_STRING_MARSHALLER
                ),
                token
            )
            return header
        }

        fun stringToTimestamp(dateString: String): Long? {
            if (dateString.isBlank()) return null
            return try {
                simpleDateFormat.parse(dateString).time
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun timestampToString(timestamp: Long): String {
            if (timestamp <= 0) return ""
            return simpleDateFormat.format(timestamp)
        }

        fun longToRoomType(type: Int): AppConstants.RoomType {
            return when (type) {
                0 -> AppConstants.RoomType.HOME
                1 -> AppConstants.RoomType.ROOM
                2 -> AppConstants.RoomType.DORM
                else -> AppConstants.RoomType.HOME
            }
        }

    }

}