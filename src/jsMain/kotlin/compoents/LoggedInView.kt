package compoents

import Poll
import User
import compoents.yourpolls.yourPolls
import kotlinx.css.*
import service.VoteService
import react.Props
import react.StateSetter
import react.fc
import styled.css
import styled.styledDiv as div

external interface LoggedInViewProps : Props {
    var polls: List<Poll>
    var currentPoll: Poll?
    var user: User
    var addPoll: (Poll) -> Unit
    var setCurrentPoll: StateSetter<Poll?>
    var voteService: VoteService
}

val loggedInView = fc<LoggedInViewProps> { props ->
    if (props.currentPoll != null) {
        div {
            css {
                width = LinearDimension("75%")
                margin = "auto"
                display = Display.flex
            }

            child(viewPoll) {
                attrs {
                    poll = props.currentPoll!!
                    voteService = props.voteService
                    user = props.user
                }
            }

            child(topVotes) {
                attrs {
                    poll = props.currentPoll!!
                }
            }
        }
    } else {
        child(createPoll) {
            attrs.addPoll = props.addPoll
        }

        if (props.polls.isNotEmpty()) {
            child(yourPolls) {
                attrs.polls = props.polls
                attrs.setCurrentPoll = props.setCurrentPoll
            }
        }
    }
}
