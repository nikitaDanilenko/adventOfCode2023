package day20

sealed interface Module {
    val name: String
    val destinations: List<String>

    data class Broadcaster(
        override val destinations: List<String>
    ) : Module {
        override val name: String = "broadcaster"
    }

    sealed interface State {
        data object On : State
        data object Off : State
    }

    sealed interface Pulse {
        data object High : Pulse
        data object Low : Pulse
    }

    data class FlipFlop(
        override val name: String,
        val state: State,
        override val destinations: List<String>
    ) : Module

    data class Conjunction(
        override val name: String,
        override val destinations: List<String>,
        val memory: Map<String, Pulse>
    ) : Module

    companion object {
        fun parse(line: String): Module {
            val parts = line.split(" -> ")
            val destinations = parts[1].split(", ")

            return when (parts.first()) {
                "broadcaster" -> Broadcaster(destinations)
                else -> {
                    val name = parts.first().drop(1)
                    when (parts.first().first()) {
                        '%' -> FlipFlop(name, State.Off, destinations)
                        else -> Conjunction(name, destinations, emptyMap<String, Pulse>().withDefault { Pulse.Low })
                    }
                }
            }
        }
    }
}