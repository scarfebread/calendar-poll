package compoents

import Poll
import kotlinx.css.*
import kotlinx.html.classes
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
            marginBottom = LinearDimension("20px")
        }

        if (props.currentPoll != null) {
            a {
                span {
                    val iconPosition = LinearDimension("30px")

                    attrs.classes = setOf("material-icons")

                    css {
                        position = Position.absolute;
                        left = iconPosition
                        top = iconPosition
                        opacity = 0.5
                        fontSize = LinearDimension("35px")
                        cursor = Cursor.pointer

                        hover {
                            opacity = 1
                        }
                    }

                    +"home"
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