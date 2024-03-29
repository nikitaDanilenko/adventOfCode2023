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
            collectNumberSolutionWith(call, day01.Day01::solutions)
        }
        post("/day02") {
            collectNumberSolutionWith(call, day02.Day02::solutions)
        }
        post("/day03") {
            collectNumberSolutionWith(call, day03.Day03::solutions)
        }
        post("/day04") {
            collectNumberSolutionWith(call, day04.Day04::solutions)
        }
        post("/day05") {
            collectNumberSolutionWith(call, day05.Day05::solutions)
        }
        post("/day06") {
            collectNumberSolutionWith(call, day06.Day06::solutions)
        }
        post("/day07") {
            collectNumberSolutionWith(call, day07.Day07::solutions)
        }
        post("/day08") {
            collectNumberSolutionWith(call, day08.Day08::solutions)
        }
        post("/day09") {
            collectNumberSolutionWith(call, day09.Day09::solutions)
        }
        post("/day10") {
            collectNumberSolutionWith(call, day10.Day10::solutions)
        }
        post("/day11") {
            collectNumberSolutionWith(call, day11.Day11::solutions)
        }
        post("/day12") {
            collectNumberSolutionWith(call, day12.Day12::solutions)
        }
        post("/day13") {
            collectNumberSolutionWith(call, day13.Day13::solutions)
        }
        post("/day14") {
            collectNumberSolutionWith(call, day14.Day14::solutions)
        }
        post("/day15") {
            collectNumberSolutionWith(call, day15.Day15::solutions)
        }
        post("/day16") {
            collectNumberSolutionWith(call, day16.Day16::solutions)
        }
        post("/day17") {
            collectNumberSolutionWith(call, day17.Day17::solutions)
        }
        post("/day18") {
            collectNumberSolutionWith(call, day18.Day18::solutions)
        }
        post("/day19") {
            collectNumberSolutionWith(call, day19.Day19::solutions)
        }
        post("/day20") {
            collectNumberSolutionWith(call, day20.Day20::solutions)
        }
        post("/day21") {
            collectNumberSolutionWith(call, day21.Day21::solutions)
        }
        post("/day22") {
            collectNumberSolutionWith(call, day22.Day22::solutions)
        }
        post("/day23") {
            collectNumberSolutionWith(call, day23.Day23::solutions)
        }
        post("/day24") {
            collectNumberSolutionWith(call, day24.Day24::solutions)
        }
        post("/day25") {
            collectNumberSolutionWith(call, day25.Day25::solutions)
        }
    }
}

private suspend fun collectNumberSolutionWith(
    call: ApplicationCall,
    solution: (String) -> Pair<BigInteger, BigInteger>

): Unit {
    val input = call.receiveText()
    val solutions = solution(input)
    val response = NumberResponse(
        solutions.first,
        solutions.second
    )
    return call.respondText(
        Json.encodeToString(NumberResponse.serializer(), response),
        ContentType.Application.Json
    )
}