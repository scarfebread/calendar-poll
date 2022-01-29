package compoents

import kotlinx.css.LinearDimension
import kotlinx.css.paddingRight
import react.Props
import react.dom.html.InputType
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.tr
import react.fc
import styled.css
import styled.styledTd

val createPoll = fc<Props> {
    h2 { +"Create poll" }

    table {
        tr {
            styledTd {
                css {
                    paddingRight = LinearDimension("150px")
                }
                +"Start"
            }
            td {
                input {
                    attrs.type = InputType.date
                }
            }
        }
        tr {
            td { +"End" }
            td {
                input {
                    attrs.type = InputType.date
                }
            }
        }
        tr {
            td { +"Include weekends?" }
            td {
                input {
                    attrs.type = InputType.checkbox
                }
            }
        }
    }

    input {
        attrs.type = InputType.submit
    }
}