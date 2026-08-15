package com.example.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatters {

    private val numberFormatter = DecimalFormat("#,##,##0.##")

    fun formatTaka(amount: Double): String {
        return "৳ " + numberFormatter.format(amount)
    }

    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentMonthCode(): String {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return sdf.format(Date())
    }

    fun formatMonthBangla(monthCode: String): String {
        // monthCode like "2026-08"
        val parts = monthCode.split("-")
        if (parts.size != 2) return monthCode
        val year = parts[0]
        val monthNum = parts[1]

        val monthName = when (monthNum) {
            "01" -> "জানুয়ারি"
            "02" -> "ফেব্রুয়ারি"
            "03" -> "মার্চ"
            "04" -> "এপ্রিল"
            "05" -> "মে"
            "06" -> "জুন"
            "07" -> "জুলাই"
            "08" -> "আগস্ট"
            "09" -> "সেপ্টেম্বর"
            "10" -> "অক্টোবর"
            "11" -> "নভেম্বর"
            "12" -> "ডিসেম্বর"
            else -> monthNum
        }

        return "$monthName $year"
    }

    fun getRecentMonthCodes(): List<String> {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        for (i in 0..11) {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            list.add(sdf.format(cal.time))
            cal.add(Calendar.MONTH, -1)
        }
        return list
    }

    fun toBanglaDigits(input: String): String {
        val banglaDigits = mapOf(
            '0' to '০', '1' to '১', '2' to '২', '3' to '৩', '4' to '৪',
            '5' to '৫', '6' to '৬', '7' to '৭', '8' to '৮', '9' to '৯'
        )
        return input.map { banglaDigits[it] ?: it }.joinToString("")
    }
}
