package compoents

import Poll
import kotlinx.css.*
import react.Props
import react.dom.h2
import react.dom.tbody
import react.fc
import styled.css
import styled.styledDiv as div
import styled.styledTable as table
import styled.styledTd as td
import styled.styledTr as tr

external interface TopVotesProps : Props {
    var poll: Poll
}

val topVotes = fc<TopVotesProps> { props ->
    val days = props.poll.calendar!!
        .filter { day -> day.votes.size > 0 }
        .sortedWith(
            compareBy { day -> day.votes.size }
        )

    div {
        css {
            marginLeft = LinearDimension("auto")
            marginRight = LinearDimension("auto")
        }

        h2 { +"Top Votes" }
        table {
            css {
                textAlign = TextAlign.center
            }
            tbody {
                tr {
                    td {
                        css {
                            paddingRight = LinearDimension("100px")
                        }
                        +"Date"
                    }
                    td { +"Votes" }
                }
                days.forEach { day ->
                    tr {
                        td {
                            css {
                                paddingRight = LinearDimension("100px")
                            }
                            +day.date
                        }
                        td { +day.votes.size.toString() }
                    }
                }
            }
        }
    }
}