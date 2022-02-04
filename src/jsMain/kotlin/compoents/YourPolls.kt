package compoents

import Poll
import react.Props
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ul
import react.fc

external interface YourPollsProps : Props {
    var polls: List<Poll>
}

val yourPolls = fc<YourPollsProps> { props ->
    h2 {
        +"Your polls"
    }

    ul {
        props.polls.forEach { item ->
            li {
                key = item.toString()
                +item.title
            }
        }
    }
}