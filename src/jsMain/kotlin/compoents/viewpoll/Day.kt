package compoents.viewpoll

import Poll
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
    var poll: Poll
    var user: User
    var voteMap: Map<String, Pair<List<Vote>, StateSetter<List<Vote>>>>
    var votesStoredInKafka: Boolean
}

val day = fc<DayProps> { props ->
    val (hover, setHover) = useState(false)
    val (voted, setVoted) = useState(false)
    val voteService = props.voteService
    val votesStoredInKafka = props.votesStoredInKafka

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
                val user = props.user
                val poll = props.poll

                // TODO horrible
                val voteState = props.voteMap[day.date]!!.first
                val setVoteState = props.voteMap[day.date]!!.second

                useEffectOnce {
                    if (!votesStoredInKafka) {
                        setVoteState(day.votes)
                    }
                }

                useEffect {
                    if (voteState.firstOrNull { it.sessionId == user.id } != null) {
                        setVoted(true)
                    } else {
                        setVoted(false)
                    }
                }

                attrs.onMouseOver = { setHover(true) }
                attrs.onMouseLeave = { setHover(false) }
                attrs.onClick = {
                    if (voted) {
                        val updatedVotes = voteState.toMutableList()
                        val vote = updatedVotes.first {
                            it.sessionId == user.id
                        }

                        updatedVotes.remove(vote)
                        day.votes.remove(vote)
                        setVoteState(updatedVotes)
                        setVoted(false)

                        if (votesStoredInKafka) {
                            voteService.cancelHttp(vote)
                        } else {
                            voteService.cancel(vote)
                        }

                        println("cancelled vote - ${vote.date} ${voteState.size}")
                    } else {
                        val newVote = Vote(poll.id!!, day.date)
                        newVote.sessionId = user.id
                        newVote.name = user.name

                        val updatedVotes = voteState.toMutableList()

                        updatedVotes.add(newVote)
                        day.votes.add(newVote)

                        setVoteState(updatedVotes)
                        setVoted(true)

                        if (votesStoredInKafka) {
                            voteService.sendHttp(newVote)
                        } else {
                            voteService.vote(newVote)
                        }

                        println("added vote - ${newVote.date} ${voteState.size}")
                    }
                }

                div {
                    css { marginTop = LinearDimension("5px") }
                    +displayDate(day)
                }
                h3 {
                    css { marginTop = LinearDimension("0") }
                    +voteState.size.toString()
                }
            }
        }
    }
}

private fun displayDate(day: Calendar.Day): String {
    val dateSplit = day.date.split("-")
    return "${dateSplit[2]}/${dateSplit[1]}"
}