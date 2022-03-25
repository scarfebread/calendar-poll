package compoents.yourpolls

import Poll
import config.Config
import react.Props
import react.StateSetter
import react.dom.html.ReactHTML.h2
import react.dom.table
import react.dom.tbody
import react.fc

external interface YourPollsProps : Props {
    var polls: List<Poll>
    var setCurrentPoll: StateSetter<Poll?>
    var config: Config
}

val yourPolls = fc<YourPollsProps> { props ->
    h2 {
        +"Your polls"
    }

    table {
        tbody {
            props.polls.forEach { poll ->
                child(pollOverview) {
                    attrs {
                        this.poll = poll
                        setCurrentPoll = props.setCurrentPoll
                        config = props.config
                    }
                }
            }
        }
    }
}