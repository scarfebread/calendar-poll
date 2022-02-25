package compoents.calendar

import User
import Vote
import service.VoteService
import kotlinx.css.*
import react.Props
import react.dom.html.ReactHTML.td
import react.dom.onClick
import react.dom.onMouseLeave
import react.dom.onMouseOver
import react.fc
import react.useEffectOnce
import react.useState
import styled.css
import styled.styledH3 as h3
import styled.styledDiv as div

external interface DayProps : Props {
    var day: Calendar.Day?
    var voteService: VoteService
    var pollId: String
    var user: User
}

val day = fc<DayProps> { props ->
    val (hover, setHover) = useState(false)
    val (voted, setVoted) = useState(false)
    val (votes, setVotes) = useState(0)
    val voteService = props.voteService

    td {
        div {
            css {
                height = LinearDimension("50px")
                width= LinearDimension("90px")
                textAlign = TextAlign.center
                borderStyle = BorderStyle.solid
                borderColor = if (hover || voted) Color.black else Color.grey
                backgroundColor = if (props.day == null) {
                    Color.grey
                } else if (voted) {
                    Color.lightBlue
                } else {
                    Color.white
                }
                if (props.day != null) {
                    cursor = Cursor.pointer
                }
            }

            if (props.day != null) {
                val day = props.day!!

                useEffectOnce {
                    setVotes(day.votes.size)

                    if (day.votes.firstOrNull { it.sessionId == props.user.id } != null)  {
                        setVoted(true)
                    }
                }

                attrs.onMouseOver = { setHover(true) }
                attrs.onMouseLeave = { setHover(false) }
                attrs.onClick = {
                    if (voted) {
                        setVoted(false)
                        setVotes(votes - 1)
                        voteService.cancel(Vote(props.pollId, day.date))
                    } else {
                        setVoted(true)
                        setVotes(votes + 1)
                        voteService.vote(Vote(props.pollId, day.date))
                    }
                }

                div {
                    css { marginTop = LinearDimension("5px") }
                    +displayDate(day)
                }
                h3 {
                    css { marginTop = LinearDimension("0") }
                    +votes.toString()
                }
            }
        }
    }
}

private fun displayDate(day: Calendar.Day): String {
    val dateSplit = day.date.split("-")
    return "${dateSplit[2]}/${dateSplit[1]}"
}