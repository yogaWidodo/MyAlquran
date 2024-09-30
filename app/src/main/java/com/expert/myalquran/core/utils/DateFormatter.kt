package com.expert.myalquran.core.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.TimeZone

object DateFormatter {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(currentString: String): String {
        val instant = Instant.parse(currentString)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm")
            .withZone(ZoneId.of(TimeZone.getDefault().id))

        return formatter.format(instant)
    }
}