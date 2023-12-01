import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.engine.*

fun main(args: Array<String>) {
// TODO: Add port parameter
    embeddedServer(Netty, 9000) {
        routing {
            get("/") {
                call.respondText("""{ "response" : "Hello, world!!" }""", ContentType.Application.Json)
            }
        }
    }.start(wait = true)
}
