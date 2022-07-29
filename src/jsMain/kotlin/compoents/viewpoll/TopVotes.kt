package compoents.viewpoll

import kotlinx.css.*
import react.Props
import react.StateSetter
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
    var voteMap: MutableMap<String, Pair<Int, StateSetter<Int>>>
}

val topVotes = fc<TopVotesProps> { props ->
    val days = props.voteMap
        .map { (date, state) -> date to state.first }
        .filter { (_, votes) -> votes > 0 }
        .sortedWith(
            compareByDescending { (_, votes) -> votes }
        )
        .toMap()

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
                days.forEach { (date, votes) ->
                    tr {
                        td {
                            css {
                                paddingRight = cellPadding
                            }
                            +formatDate(date)
                        }
                        td { +votes.toString() }
                    }
                }
            }
        }
    }
}
