package compoents

import Poll
import compoents.calendar.calendar
import react.Props
import react.dom.div
import react.dom.html.ReactHTML.h2
import react.fc

external interface ViewPollProps : Props {
    var poll: Poll
}

val viewPoll = fc<ViewPollProps> { props ->
    div {
        h2 { +props.poll.title }

        child(calendar) {
            attrs {
                this.poll = props.poll
            }
        }
    }
}