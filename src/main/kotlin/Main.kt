import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import util.NumberResponse
import java.math.BigInteger

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
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
        post("/day03") {
            collectNumberSolutionWith(call, day03.Day03::part1, day03.Day03::part2)
        }
        post("/day04") {
            collectNumberSolutionWith(call, day04.Day04::part1, day04.Day04::part2)
        }
        post("/day05") {
            collectNumberSolutionWith(call, day05.Day05::part1, day05.Day05::part2)
        }
        post("/day06") {
            collectNumberSolutionWith(call, day06.Day06::part1, day06.Day06::part2)
        }
    }
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