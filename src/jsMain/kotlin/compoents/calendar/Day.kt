package compoents.calendar

import kotlinx.css.*
import react.Props
import react.dom.html.ReactHTML.td
import react.fc
import styled.css
import styled.styledDiv as div

external interface DayProps : Props {
    var day: Calendar.Day
}

val day = fc<DayProps> { props ->
    val dateSplit = props.day.date.split("-")
    val displayDate = "${dateSplit[2]}/${dateSplit[1]}"

    td {
        div {
            css {
                height = LinearDimension("50px")
                width= LinearDimension("70px")
                textAlign = TextAlign.center
                borderColor = Color.grey
                borderStyle = BorderStyle.solid
            }

            div { +displayDate }
        }
    }
}
