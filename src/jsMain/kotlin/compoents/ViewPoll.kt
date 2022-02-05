package compoents

import Poll
import compoents.calendar.day
import react.Props
import react.dom.div
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.tr
import react.dom.table
import react.dom.tbody
import react.fc

external interface ViewPollProps : Props {
    var poll: Poll
}

val viewPoll = fc<ViewPollProps> { props ->
    div {
        +"Hi ${props.poll.title}"

        val calendar = props.poll.calendar!!

        val daysInWeek = if (props.poll.weekends) 7 else 5

        var week = 1
        var renderCalendar = true

        table {
            tbody {
                while (renderCalendar) {
                    tr {
                        val days = calendar.filter { day -> day.week == week }
                        for (dayIndex in 0..daysInWeek) {
                            val dayOfWeek = days.firstOrNull { day -> day.day == dayIndex }
                            if (dayOfWeek != null) {
                                child(day) {
                                    attrs {
                                        this.day = dayOfWeek
                                    }
                                }

                                if (dayOfWeek.date == props.poll.end) {
                                    renderCalendar = false
                                    break
                                }
                            } else {
                                td
                            }
                        }
                        week++
                    }
                }
            }
        }
    }
}