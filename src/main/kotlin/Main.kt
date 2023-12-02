import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import util.NumberResponse

fun main(args: Array<String>) {
// TODO: Add port parameter
    embeddedServer(Netty, 9000) {
        routing {
            get("/") {
                call.respondText("""{ "response" : "Hello, world!!" }""", ContentType.Application.Json)
            }
            post("/day01") {
                val input = call.receiveText()
                val solution1 = day01.Day01.part1(input)
                val solution2 = day01.Day01.part2(input)
                val response = NumberResponse(solution1, solution2)
                call.respondText(
                    Json.encodeToString(NumberResponse.serializer(), response),
                    ContentType.Application.Json
                )
            }
            post("/day02") {
                val input = call.receiveText()
                val solution1 = day02.Day02.part1(input)
                val solution2 = day02.Day02.part2(input)
                val response = NumberResponse(solution1.toBigInteger(), solution2)
                call.respondText(
                    Json.encodeToString(NumberResponse.serializer(), response),
                    ContentType.Application.Json
                )
            }
        }
    }.start(wait = true)
}
