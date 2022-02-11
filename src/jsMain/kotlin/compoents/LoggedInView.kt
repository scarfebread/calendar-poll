package compoents

import Poll
import User
import Vote
import react.Props
import react.StateSetter
import react.fc

external interface LoggedInViewProps : Props {
    var polls: List<Poll>
    var currentPoll: Poll?
    var user: User
    var addPoll: (Poll) -> Unit
    var setCurrentPoll: StateSetter<Poll?>
    var vote: (Vote) -> Unit
}

val loggedInView = fc<LoggedInViewProps> { props ->
    if (props.currentPoll != null) {
        child(viewPoll) {
            attrs {
                poll = props.currentPoll!!
                vote = props.vote
                user = props.user
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
