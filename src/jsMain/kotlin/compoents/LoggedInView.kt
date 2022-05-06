package compoents

import Poll
import User
import compoents.home.createPoll
import compoents.viewpoll.topVotes
import compoents.viewpoll.viewPoll
import compoents.yourpolls.yourPolls
import config.Config
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
    var setCurrentPoll: (Poll?) -> Unit
    var voteService: VoteService
    var config: Config
}

val loggedInView = fc<LoggedInViewProps> { props ->
    if (props.currentPoll != null) {
        div {
            css {
                width = LinearDimension("770px")
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
        div {
            css {
                width = LinearDimension("950px")
                margin = "auto"
                display = Display.flex
            }

            div {
                css {
                    marginRight = LinearDimension("35px")
                }

                child(createPoll) {
                    attrs.addPoll = props.addPoll
                }
            }

            div {
                css {
                    marginLeft = LinearDimension("35px")
                }

                child(yourPolls) {
                    attrs.polls = props.polls
                    attrs.setCurrentPoll = props.setCurrentPoll
                    attrs.config = props.config
                }
            }
        }
    }
}
