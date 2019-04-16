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
            if (dateString.isBlank()) return 0
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

    }

}