package compoents.yourpolls

import Poll
import config.Config
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.css.Cursor
import kotlinx.css.cursor
import kotlinx.css.opacity
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import react.Props
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.b
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.tr
import react.fc
import react.useState
import service.formatDate
import styled.css
import styled.styledSpan as span

external interface PollOverviewProps : Props {
    var poll: Poll
    var setCurrentPoll: (Poll?) -> Unit
    var config: Config
}

val pollOverview = fc<PollOverviewProps> { props ->
    val poll = props.poll
    val config = props.config

    val (copied, setCopied) = useState(false)

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
        td { +formatDate(poll.start) }
        td { +"-" }
        td { +formatDate(poll.end) }
        td {
            span {
                attrs.classes = setOf("material-icons")
                attrs.onClickFunction = {
                    setCopied(true)
                    window.navigator.clipboard.writeText(
                        config.host + Poll.path + "/${poll.id}"
                    )
                }

                css {
                    opacity = if (copied) 1 else 0.5
                    cursor = Cursor.pointer
                    hover {
                        opacity = 1
                    }
                }

                +"content_copy"
            }
        }
    }
}
