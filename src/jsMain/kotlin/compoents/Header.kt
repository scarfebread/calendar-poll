package compoents

import Poll
import kotlinx.css.*
import react.Props
import react.StateSetter
import react.dom.html.ReactHTML.a
import react.fc
import styled.css
import styled.styledSpan as span
import styled.styledDiv as div
import styled.styledH1 as h1

external interface HeaderProps : Props {
    var currentPoll: Poll?
    var setCurrentPoll: StateSetter<Poll?>
}

val header = fc<HeaderProps> { props ->
    div {
        css {
            width = LinearDimension("100%")
            height = LinearDimension("100px")
            backgroundColor = Color.lightGray
            textAlign = TextAlign.center
        }

        if (props.currentPoll != null) {
            a {
                span {
                    val iconSize = LinearDimension("35px")
                    val iconPosition = LinearDimension("30px")

                    css {
                        width = iconSize
                        height = iconSize
                        position = Position.absolute;
                        left = iconPosition
                        top = iconPosition
                        background = "url(images/home_black_24dp.svg)"
                        backgroundSize = iconSize.value
                    }
                }

                attrs.onClick = {
                    props.setCurrentPoll(null)
                }
            }
        }

        h1 {
            css {
                padding = "30px"
                marginTop = LinearDimension("0")
            }
            +"James Poll"
        }
    }
}