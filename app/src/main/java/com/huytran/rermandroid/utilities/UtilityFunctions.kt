package com.huytran.rermandroid.utilities

import android.content.SharedPreferences
import io.grpc.Metadata

class UtilityFunctions {

    companion object {

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

    }

}