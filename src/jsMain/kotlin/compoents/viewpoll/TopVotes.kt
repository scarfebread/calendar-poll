package compoents.viewpoll

import Poll
import kotlinx.css.*
import react.Props
import react.dom.h2
import react.dom.tbody
import react.fc
import service.formatDate
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
            compareByDescending { day -> day.votes.size }
        )

    div {
        css {
            marginLeft = LinearDimension("35px")
        }

        h2 { +"Top Votes" }
        table {
            val cellPadding = LinearDimension("50px")

            css {
                textAlign = TextAlign.center
            }
            tbody {
                tr {
                    td {
                        css {
                            paddingRight = cellPadding
                        }
                        +"Date"
                    }
                    td { +"Votes" }
                }
                days.forEach { day ->
                    tr {
                        td {
                            css {
                                paddingRight = cellPadding
                            }
                            +formatDate(day.date)
                        }
                        td { +day.votes.size.toString() }
                    }
                }
            }
        }
    }
}
