package compoents

import kotlinx.css.LinearDimension
import kotlinx.css.TextAlign
import kotlinx.css.marginTop
import kotlinx.css.textAlign
import react.Props
import react.dom.html.InputType
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.input
import react.fc
import react.useState
import styled.css
import styled.styledDiv as div

external interface CreateUserProps : Props {
    var createUser: (String) -> Unit
}

val createUser = fc<CreateUserProps> { props ->
    val (name, setName) = useState("")

    div {
        css {
            textAlign = TextAlign.center
            marginTop = LinearDimension("50px")
        }

        h3 { +"My name's James, but what is yours?" }
        form {
            attrs.onSubmit = {
                it.preventDefault()
                props.createUser(name)
            }

            input {
                attrs {
                    onChange = {
                        setName(it.target.value)
                    }
                    value = name
                }

            }
            input {
                attrs.type = InputType.submit
            }
        }
    }
}

