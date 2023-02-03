package compoents.viewpoll

import Poll
import User
import Vote
import service.VoteService
import kotlinx.css.*
import react.Props
import react.StateSetter
import react.dom.html.ReactHTML.h2
import react.fc
import react.useState
import styled.css
import styled.styledDiv as div

external interface ViewPollProps : Props {
    var poll: Poll
    var user: User
    var voteService: VoteService
}

val viewPoll = fc<ViewPollProps> { props ->
    val voteMap = mutableMapOf<String, Pair<Int, StateSetter<Int>>>()
    val poll = props.poll

    val daysInWeek = if (poll.weekends) 7 else 5

    var week = 1
    var renderCalendar = true

    // TODO duplicate iterating logic
    while (renderCalendar) {
        val days = poll.calendar!!.filter { day -> day.week == week }
        for (dayIndex in 1..daysInWeek) {
            val dayOfWeek = days.firstOrNull { day -> day.day == dayIndex }

            if (dayOfWeek != null) {
                val (votes, setVotes) = useState(0)
                voteMap[dayOfWeek.date] = Pair(votes, setVotes)
            }

            if (dayOfWeek != null && dayOfWeek.date == poll.end) {
                renderCalendar = false
                break
            }
        }
        week++
    }

    div {
        css {
            marginRight = LinearDimension("35px")
        }

        h2 { +props.poll.title }

        child(calendar) {
            attrs {
                this.poll = props.poll
                this.voteService = props.voteService
                this.user = props.user
                this.voteMap = voteMap
            }
        }
    }

    child(topVotes) {
        attrs {
            this.voteMap = voteMap
        }
    }
}