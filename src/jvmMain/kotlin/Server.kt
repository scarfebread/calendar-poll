import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import repository.PollRepository
import repository.UserRepository
import routes.index
import routes.poll
import routes.user
import routes.vote
import session.UserSession
import session.configureSessionAuthentication
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient


fun main() {
    embeddedServer(Netty, 9090) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Put)
            method(HttpMethod.Delete)
            allowNonSimpleContentTypes = true
            host("scarfebread.co.uk/", listOf("http", "https"))
            host("scarfebread.co.uk", listOf("http", "https"))
        }
        install(Compression) {
            gzip()
        }
        install(Sessions) {
            cookie<UserSession>(UserSession.COOKIE_NAME) {
                cookie.maxAgeInSeconds = 60 * 60 * 24 * 365 * 50
            }
        }
        install(Authentication) {
            configureSessionAuthentication()
        }

        val mongo = KMongo.createClient(
            MongoClientSettings
                .builder()
                .applyConnectionString(ConnectionString("mongodb+srv://calendar-poll:lnHC9Lt8eoVJCAvC@cluster0.obaep.mongodb.net/calendar-poll?retryWrites=true&w=majority"))
                .build()
        ).coroutine
        val database = mongo.getDatabase("calendar-poll")

        val dynamo = DynamoDbClient.builder()
            .region(Region.EU_WEST_1)
            .build()

        val userRepository = UserRepository(database.getCollection(), dynamo)
        val pollRepository = PollRepository(database.getCollection(), dynamo)

        routing {
            index()
            poll(pollRepository, userRepository)
            user(userRepository)
            vote(pollRepository, userRepository)
        }
    }.start(wait = true)
}