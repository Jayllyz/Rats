package com.rats.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun prettyDate(date: String): String? {
    val simpleFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formatedDate = simpleFormat.parse(date)

    val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.FRENCH)
    val formattedDate = formatedDate?.let { outputFormat.format(it) }
    return formattedDate
}
