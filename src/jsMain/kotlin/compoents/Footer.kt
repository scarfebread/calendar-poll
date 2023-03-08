package compoents

import kotlinx.css.*
import react.Props
import react.fc
import styled.css
import styled.styledDiv

val footer = fc<Props> {
    styledDiv {
        css {
            width = LinearDimension("100%")
            height = LinearDimension("100px")
            backgroundColor = Color.lightGray
            textAlign = TextAlign.center
            position = Position.fixed
            bottom = LinearDimension("0")
        }
    }
}