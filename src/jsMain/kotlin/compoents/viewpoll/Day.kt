package compoents.viewpoll

import User
import Vote
import service.VoteService
import kotlinx.css.*
import react.*
import react.dom.html.ReactHTML.td
import react.dom.onClick
import react.dom.onMouseLeave
import react.dom.onMouseOver
import styled.css
import styled.styledH3 as h3
import styled.styledDiv as div

external interface DayProps : Props {
    var day: Calendar.Day?
    var voteService: VoteService
    var pollId: String
    var user: User
    var voteMap: Map<String, Pair<Int, StateSetter<Int>>>
}

val day = fc<DayProps> { props ->
    val (hover, setHover) = useState(false)
    val (voted, setVoted) = useState(false)
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
                val votes = day.votes
                val user = props.user

                // TODO horrible
                val numberOfVotes = props.voteMap[day.date]!!.first
                val setNumberOfVotes = props.voteMap[day.date]!!.second

                useEffectOnce {
                    setNumberOfVotes(votes.size)
                }

                useEffect {
                    if (day.votes.firstOrNull { it.sessionId == user.id } != null)  {
                        setVoted(true)
                    }
                }

                attrs.onMouseOver = { setHover(true) }
                attrs.onMouseLeave = { setHover(false) }
                attrs.onClick = {
                    if (voted) {
                        setVoted(false)
                        setNumberOfVotes(numberOfVotes - 1)

                        val vote = votes.first{ it.sessionId == user.id }
                        voteService.cancel(vote) // TODO only send one request
                        voteService.cancelHttp(vote)
                    } else {
                        setVoted(true)
                        setNumberOfVotes(numberOfVotes + 1)

                        val vote = Vote(props.pollId, day.date)
                        voteService.vote(vote) // TODO only send one request
                        voteService.sendHttp(vote)
                    }
                }

                div {
                    css { marginTop = LinearDimension("5px") }
                    +displayDate(day)
                }
                h3 {
                    css { marginTop = LinearDimension("0") }
                    +numberOfVotes.toString()
                }
            }
        }
    }
}

private fun displayDate(day: Calendar.Day): String {
    val dateSplit = day.date.split("-")
    return "${dateSplit[2]}/${dateSplit[1]}"
}