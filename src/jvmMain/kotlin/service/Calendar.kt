package service

import Vote
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun generateCalendar(startDate: String, endDate: String, includeWeekends: Boolean, votes: List<Vote> = listOf()): List<Calendar.Day> {
    val daysInWeek = if (includeWeekends) 7 else 5
    var currentPosition = LocalDate.from(
        LocalDate.parse(startDate, FORMAT)
    )
    val calendar = mutableListOf<Calendar.Day>()
    var week = 1

    while (currentPosition <= LocalDate.parse(endDate, FORMAT)) {
        val dayOfWeek = currentPosition.dayOfWeek.value

        if (dayOfWeek <= daysInWeek) {
            val day = Calendar.Day(currentPosition.format(FORMAT), week, currentPosition.dayOfWeek.value)

            if (votes.isNotEmpty()) {
                day.votes.addAll(
                    votes.filter { it.date == day.date }
                )
            }

            calendar.add(day)

            if (dayOfWeek == daysInWeek) {
                week++
            }
        }

        currentPosition = currentPosition.plusDays(1)
    }

    return calendar
}

fun validateStart(date: String, weekends: Boolean): String {
    val start = LocalDate.parse(date, FORMAT)
    val dayOfWeek = start.dayOfWeek.value

    return if (!weekends && dayOfWeek > 5) {
        start.plusDays(8L - dayOfWeek).format(FORMAT)
    } else {
        date
    }
}

fun validateEnd(date: String, weekends: Boolean): String {
    val end = LocalDate.parse(date, FORMAT)
    val dayOfWeek = end.dayOfWeek.value

    return if (!weekends && dayOfWeek > 5) {
        end.minusDays(dayOfWeek - 5L).format(FORMAT)
    } else {
        date
    }
}
