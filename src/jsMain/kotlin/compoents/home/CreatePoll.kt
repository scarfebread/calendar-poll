package compoents.home

import Poll
import kotlinx.css.LinearDimension
import kotlinx.css.paddingRight
import kotlinx.css.width
import kotlinx.html.InputType.*
import org.w3c.dom.HTMLInputElement
import react.Props
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.tbody
import react.dom.html.ReactHTML.tr
import react.dom.onChange
import react.fc
import react.useState
import styled.css
import styled.styledInput as input
import styled.styledTd as td

external interface CreatePollProps : Props {
    var addPoll: (Poll) -> Unit
}

val createPoll = fc<CreatePollProps> { props ->
    val (title, setTitle) = useState("")
    val (startDate, setStartDate) = useState("")
    val (endDate, setEndDate) = useState("")
    val (includeWeekends, setIncludeWeekends) = useState(false)

    h2 { +"Create poll" }

    form {
        attrs.onSubmit = {
            it.preventDefault()
            props.addPoll(Poll(title, startDate, endDate, includeWeekends))
            setTitle("")
            setStartDate("")
            setEndDate("")
        }

        table {
            tbody {
                tr {
                    td {
                        css {
                            paddingRight = LinearDimension("150px")
                        }
                        +"Title"
                    }
                    td {
                        input {
                            attrs.onChange = {
                                setTitle((it.target as HTMLInputElement).value)
                            }
                            attrs.required = true
                            attrs.value = title
                        }
                    }
                }
                tr {
                    td { +"Start" }
                    td {
                        input(type = date) {
                            attrs.onChange = {
                                setStartDate((it.target as HTMLInputElement).value)
                            }
                            attrs.required = true
                            attrs.value = startDate
                        }
                    }
                }
                tr {
                    td { +"End" }
                    td {
                        input(type = date) {
                            attrs.onChange = {
                                setEndDate((it.target as HTMLInputElement).value)
                            }
                            attrs.required = true
                            attrs.value = endDate
                        }
                    }
                }
                tr {
                    td { +"Include weekends?" }
                    td {
                        input(type = checkBox) {
                            attrs.onChange = {
                                setIncludeWeekends((it.target as HTMLInputElement).value == "true")
                            }
                        }
                    }
                }
            }
        }

        input(type = submit) {
            css {
                width = LinearDimension("365px")
            }
        }
    }
}