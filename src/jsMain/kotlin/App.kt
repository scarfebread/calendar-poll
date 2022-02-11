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
    val client = Client()

    var polls by useState(emptyList<Poll>())
    var user by useState<User?>(null)
    val (currentPoll, setCurrentPoll) = useState<Poll?>(null)
    val (loaded, setLoaded) = useState(false)

    useEffectOnce {
        scope.launch {
            polls = client.getPolls()
            user = client.getUser()
            setLoaded(true)
        }
    }

    div {
        css {
            fontFamily = "Courier New"
        }

        child(header) {
            attrs {
                this.setCurrentPoll = setCurrentPoll
                this.currentPoll = currentPoll
            }
        }

        div {
            css {
                marginLeft = LinearDimension("50px")
            }

            if (loaded && user?.name == null) {
                child(createUser) {
                    attrs.createUser = { name ->
                        scope.launch {
                            // TODO should be the response from post to make sure the user is created
                            client.createUser(User(null, name))
                            user = User(null, name)
                        }
                    }
                }
            } else if (loaded) {
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
                                client.addPoll(poll)
                                polls = client.getPolls()
                            }
                        }
                        this.polls = polls
                        this.user = user!!
                        this.currentPoll = currentPoll
                        this.setCurrentPoll = setCurrentPoll
                        this.vote = { vote ->
                            scope.launch {
                                client.vote(vote)
                            }
                        }
                    }
                }
            }
        }

        child(footer)
    }
}