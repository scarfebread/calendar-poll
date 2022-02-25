package service

import kotlin.js.Date

fun formatDate(date: String): String {
    return Date(date).toLocaleDateString("en-GB")
}