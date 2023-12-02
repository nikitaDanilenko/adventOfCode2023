import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import util.NumberResponse
import java.math.BigInteger

fun main(args: Array<String>) {
// TODO: Add port parameter
    embeddedServer(Netty, 9000) {
        routing {
            get("/") {
                call.respondText("""{ "response" : "Hello, world!!" }""", ContentType.Application.Json)
            }
            post("/day01") {
                collectNumberSolutionWith(call, day01.Day01::part1, day01.Day01::part2)
            }
            post("/day02") {
                collectNumberSolutionWith(call, { i -> day02.Day02.part1(i).toBigInteger() }, day02.Day02::part2)
            }
        }
    }.start(wait = true)
}

private suspend fun collectNumberSolutionWith(
    call: ApplicationCall,
    solution1: (String) -> BigInteger, solution2: (String) -> BigInteger

): Unit {
    val input = call.receiveText()
    val response = NumberResponse(
        solution1(input),
        solution2(input)
    )
    return call.respondText(
        Json.encodeToString(NumberResponse.serializer(), response),
        ContentType.Application.Json
    )
}