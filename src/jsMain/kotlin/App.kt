import client.Client
import compoents.*
import compoents.footer
import compoents.header
import compoents.home.createUser
import config.Config
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import react.Props
import react.fc
import react.useEffectOnce
import react.useState
import service.VoteService
import service.setPollCookie
import styled.css
import styled.styledDiv as div

private val scope = MainScope()

val app = fc<Props> {
    val config = Config()
    val client = Client(config)

    var polls by useState(emptyList<Poll>())
    var user by useState(User(null, null))
    var currentPoll by useState<Poll?>(null)
    val (loaded, setLoaded) = useState(false)

    fun setCurrentPoll(poll: Poll?) {
        currentPoll = poll
        setPollCookie(poll?.id)
    }

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
                this.setCurrentPoll = ::setCurrentPoll
                this.currentPoll = currentPoll
                updatePolls = {
                    scope.launch {
                        polls = client.getPolls()
                    }
                }
            }
        }

        div {
            if (loaded && user.newUser()) {
                child(createUser) {
                    attrs.createUser = { name ->
                        scope.launch {
                            user = client.createUser(User(null, name))
                        }
                    }
                }
            } else if (loaded) {
                if (currentPoll == null) {
                    polls.forEach { poll ->
                        if (poll.current) {
                            setCurrentPoll(poll)
                            poll.current = false
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
                        this.user = user
                        this.currentPoll = currentPoll
                        this.setCurrentPoll = ::setCurrentPoll
                        this.voteService = VoteService(scope, config)
                        this.config = config
                    }
                }
            }
        }

        child(footer)
    }
}