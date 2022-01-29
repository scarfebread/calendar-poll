package compoents

import kotlinx.css.*
import react.Props
import react.fc
import styled.css
import styled.styledDiv
import styled.styledH1

val header = fc<Props> {
    styledDiv {
        css {
            width = LinearDimension("100%")
            height = LinearDimension("100px")
            backgroundColor = Color.lightGray
            textAlign = TextAlign.center
        }

        styledH1 {
            css {
                padding = "30px"
                marginTop = LinearDimension("0")
            }
            +"James Poll"
        }
    }
}