package compoents.viewpoll

import Poll
import User
import service.VoteService
import kotlinx.css.*
import react.Props
import react.dom.html.ReactHTML.h2
import react.fc
import styled.css
import styled.styledDiv as div

external interface ViewPollProps : Props {
    var poll: Poll
    var user: User
    var voteService: VoteService
}

val viewPoll = fc<ViewPollProps> { props ->
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
            }
        }
    }
}