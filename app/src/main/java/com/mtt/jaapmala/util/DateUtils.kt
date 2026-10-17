package com.mtt.jaapmala.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale

object DateUtils {
    fun formatDate(inputDate: String): String {
        return try {
            val isoDate = toIsoDate(inputDate)
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val parsedDate = LocalDate.parse(isoDate, inputFormatter)
            parsedDate.format(outputFormatter)
        } catch (e: DateTimeParseException) {
            inputDate
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    fun formatTimeTo12Hour(time24: String): String {
        return try {
            val parser = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = parser.parse(time24)
            date?.let { formatter.format(it) } ?: time24
        } catch (e: Exception) {
            time24 // fallback if parsing fails
        }
    }

    fun formatMillisToDate(millis: Long?): String {
        if (millis == null) return ""
        return try {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatter.format(Date(millis))
        } catch (e: Exception) {
            ""
        }
    }

    fun getTodayDate(): String {
        return LocalDate.now().toString()
    }

    fun toIsoDate(dateStr: String): String {
        if (dateStr.isBlank()) return dateStr
        return try {
            if (dateStr.matches(Regex("\\d{2}-\\d{2}-\\d{4}"))) {
                val parsed = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                parsed.toString()
            } else {
                dateStr
            }
        } catch (e: Exception) {
            dateStr
        }
    }

    fun generateDateRange(
        startDate: String,
        endDate: String
    ): List<String> {
        val start = LocalDate.parse(toIsoDate(startDate))
        val end = LocalDate.parse(toIsoDate(endDate))

        if (start.isAfter(end)) {
            return emptyList()
        }

        return generateSequence(start) { current ->
            if (current.isBefore(end)) {
                current.plusDays(1)
            } else {
                null
            }
        }.map(LocalDate::toString)
            .toList()
    }
}
