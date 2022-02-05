package service

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun generateCalendar(startDate: String, endDate: String, includeWeekends: Boolean): List<Calendar.Day> {
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    val start = LocalDate.parse(startDate, dateFormat);
    val end = LocalDate.parse(endDate, dateFormat);
    val daysInWeek = if (includeWeekends) 7 else 5
    var currentPosition = LocalDate.from(start)
    val calendar = mutableListOf<Calendar.Day>()
    var week = 1

    while (currentPosition <= end) {
        val dayOfWeek = currentPosition.dayOfWeek.value

        if (dayOfWeek <= daysInWeek) {
            calendar.add(
                Calendar.Day(currentPosition.format(dateFormat), week, currentPosition.dayOfWeek.value)
            )

            if (dayOfWeek == daysInWeek) {
                week++
            }
        }

        currentPosition = currentPosition.plusDays(1)
    }

    return calendar
}
