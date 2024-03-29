package compoents.viewpoll

import Poll
import User
import kotlinx.browser.window
import service.VoteService
import kotlinx.css.TextAlign
import kotlinx.css.textAlign
import react.*
import react.dom.html.ReactHTML.td
import react.dom.table
import react.dom.tbody
import styled.css
import styled.styledTr as tr

external interface CalendarProps : Props {
    var poll: Poll
    var user: User
    var voteService: VoteService
    var voteMap: MutableMap<String, Pair<Int, StateSetter<Int>>>
}

val calendar = fc<CalendarProps> { props ->
    val poll = props.poll
    val calendar = props.poll.calendar!!
    val voteService = props.voteService
    val voteMap = props.voteMap

    val daysInWeek = if (props.poll.weekends) 7 else 5

    var week = 1
    var renderCalendar = true

    table {
        tbody {
            tr {
                css {
                    textAlign = TextAlign.center
                }

                td { +"Monday" }
                td { +"Tuesday" }
                td { +"Wednesday" }
                td { +"Thursday" }
                td { +"Friday" }

                if (props.poll.weekends) {
                    td { +"Saturday" }
                    td { +"Sunday" }
                }
            }

            while (renderCalendar) {
                tr {
                    val days = calendar.filter { day -> day.week == week }
                    for (dayIndex in 1..daysInWeek) {
                        val dayOfWeek = days.firstOrNull { day -> day.day == dayIndex }

                        child(day) {
                            attrs {
                                this.day = dayOfWeek
                                this.pollId = props.poll.id!!
                                this.voteService = props.voteService
                                this.user = props.user
                                this.voteMap = voteMap
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

            useEffectOnce {
                voteService.listen(poll, voteMap)
            }

            useEffect {
                window.onbeforeunload = {
                    voteService.stop()
                    null
                }
            }
        }
    }
}
