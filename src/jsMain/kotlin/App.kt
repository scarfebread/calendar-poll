import compoents.*
import compoents.footer
import compoents.header
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import react.Props
import react.fc
import react.useEffectOnce
import react.useState
import styled.css
import styled.styledDiv as div

private val scope = MainScope()

val app = fc<Props> {
    var polls by useState(emptyList<Poll>())
    var user by useState<User?>(null)
    val (currentPoll, setCurrentPoll) = useState<Poll?>(null)

    useEffectOnce {
        scope.launch {
            polls = getPolls()
            user = getUser()
        }
    }

    div {
        css {
            fontFamily = "Courier New"
        }

        child(header)

        div {
            css {
                marginLeft = LinearDimension("50px")
            }

            if (user?.name == null) {
                child(createUser) {
                    attrs.createUser = { name ->
                        scope.launch {
                            // TODO should be the response from post to make sure the user is created
                            createUser(User(null, name))
                            user = User(null, name)
                        }
                    }
                }
            } else {
                if (currentPoll == null) {
                    polls.forEach { poll ->
                        if (poll.current) {
                            setCurrentPoll(poll)
                        }
                    }
                }

                child(loggedInView) {
                    attrs {
                        addPoll = { poll ->
                            scope.launch {
                                // TODO naming
                                addPollApi(poll)
                                polls = getPolls()
                            }
                        }
                        this.polls = polls
                        this.currentPoll = currentPoll
                    }
                }
            }
        }

        child(footer)
    }
}