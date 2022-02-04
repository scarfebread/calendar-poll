package compoents

import Poll
import react.Props
import react.dom.div
import react.fc

external interface ViewPollProps : Props {
    var poll: Poll
}

val viewPoll = fc<ViewPollProps> { props ->
    div {
        +"Hi ${props.poll.title}"
    }
}
