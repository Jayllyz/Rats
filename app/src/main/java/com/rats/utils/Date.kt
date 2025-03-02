package com.rats.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

fun prettyDate(date: String): String? {
    val simpleFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    simpleFormat.isLenient = false

    return try {
        val formatedDate = simpleFormat.parse(date)
        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.FRENCH)
        formatedDate?.let { outputFormat.format(it) }
    } catch (_: ParseException) {
        null
    }
}
