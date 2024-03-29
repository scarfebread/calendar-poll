import event.EventApi
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import repository.PollRepository
import repository.UserRepository
import routes.*
import session.UserSession
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.sqs.SqsClient


fun main() {
    embeddedServer(Netty, 9090) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowNonSimpleContentTypes = true
            allowHost("scarfebread.co.uk/", schemes = listOf("http", "https"))
            allowHost("scarfebread.co.uk", schemes = listOf("http", "https"))
        }
        install(Compression) {
            gzip()
        }
        install(Sessions) {
            // TODO extract from Server
            cookie<UserSession>(UserSession.COOKIE_NAME) {
                cookie.maxAgeInSeconds = 60 * 60 * 24 * 365 * 50 // TODO compile warning
            }
        }
        install(Authentication) {
            // TODO extract from Server
            session<UserSession> {
                validate { session ->
                    session
                }
                challenge {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

        val dynamo = DynamoDbClient.builder()
            .region(Region.EU_WEST_1)
            .build()

        val sqs = SqsClient.builder()
            .region(Region.EU_WEST_1)
            .build()
        val eventApi = EventApi(sqs)

        val userRepository = UserRepository(dynamo)
        val pollRepository = PollRepository(dynamo, eventApi)

        routing {
            index()
            poll(pollRepository, userRepository)
            user(userRepository)
            voteUpdates(pollRepository, userRepository)
        }
    }.start(wait = true)
}