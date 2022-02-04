package compoents

import Poll
import react.Props
import react.StateSetter
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.b
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.tr
import react.dom.table
import react.dom.tbody
import react.fc

external interface YourPollsProps : Props {
    var polls: List<Poll>
    var setCurrentPoll: StateSetter<Poll?>
}

val yourPolls = fc<YourPollsProps> { props ->
    h2 {
        +"Your polls"
    }

    table {
        tbody {
            props.polls.forEach { poll ->
                tr {
                    td {
                        b {
                            a {
                                +poll.title
                                attrs.onClick = {
                                    it.preventDefault()
                                    props.setCurrentPoll(poll)
                                }
                                attrs.href = poll.id
                            }
                        }
                    }
                    td { +poll.start }
                    td { +"-" }
                    td { +poll.end }
                }
            }
        }
    }
}