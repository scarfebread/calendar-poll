import compoents.createPoll
import compoents.yourPolls
import compoents.header
import compoents.footer
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import react.Props
import react.fc
import react.useEffectOnce
import react.useState
import styled.css
import styled.styledDiv as div

private val scope = MainScope()

val app = fc<Props> {
    var polls by useState(emptyList<Poll>())

    useEffectOnce {
        scope.launch {
            polls = getPolls()
        }
    }

    div {
        css {
            fontFamily = "Courier New"
        }

        child(header)

        div {
            css {
                marginLeft = LinearDimension("50px")
            }

            child(createPoll)

            if (polls.isNotEmpty()) {
                child(yourPolls) {
                    attrs.polls = polls
                }
            }
        }

        child(footer)
    }
}