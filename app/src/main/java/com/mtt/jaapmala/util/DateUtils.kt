package com.mtt.jaapmala.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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


}