package com.mtt.jaapmala.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object DateUtils {
    fun formatDate(inputDate: String): String {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val parsedDate = LocalDate.parse(inputDate, inputFormatter)
            parsedDate.format(outputFormatter)
        } catch (e: DateTimeParseException) {
            inputDate
        }
        catch (e: Exception) {
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

}