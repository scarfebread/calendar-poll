package compoents.calendar

import Poll
import react.Props
import react.dom.html.ReactHTML.tr
import react.dom.table
import react.dom.tbody
import react.fc

external interface CalendarProps : Props {
    var poll: Poll
}

val calendar = fc<CalendarProps> { props ->
    val calendar = props.poll.calendar!!

    val daysInWeek = if (props.poll.weekends) 7 else 5

    var week = 1
    var renderCalendar = true

    table {
        tbody {
            while (renderCalendar) {
                tr {
                    val days = calendar.filter { day -> day.week == week }
                    for (dayIndex in 1..daysInWeek) {
                        val dayOfWeek = days.firstOrNull { day -> day.day == dayIndex }
                        child(day) {
                            attrs {
                                this.day = dayOfWeek
                            }
                        }
                        if (dayOfWeek != null && dayOfWeek.date == props.poll.end) {
                            renderCalendar = false
                            break
                        }
                    }
                    week++
                }
            }
        }
    }
}
