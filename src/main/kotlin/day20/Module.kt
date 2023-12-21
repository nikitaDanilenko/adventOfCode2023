package day20

import java.math.BigInteger

sealed interface Module {
    val name: String
    val destinations: List<String>

    data class Broadcaster(
        override val destinations: List<String>
    ) : Module {
        override val name: String = "broadcaster"
    }

    data object Button : Module {
        override val name: String = "button"
        override val destinations: List<String> = listOf("broadcaster")
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
                        else -> Conjunction(name, destinations, emptyMap())
                    }
                }
            }
        }

        data class Count(
            val low: BigInteger,
            val high: BigInteger
        ) {
            companion object {
                fun add(count1: Count, count2: Count): Count =
                    Count(
                        count1.low + count2.low,
                        count1.high + count2.high
                    )
            }
        }

        data class ProcessStep(
            val machine: Machine,
            val outgoingPulses: List<OutgoingPulse>,
            val count: Count
        )

        data class OutgoingPulse(
            val sender: String,
            val receiver: String,
            val pulse: Pulse
        )

        fun process(
            sender: String,
            module: Module,
            pulse: Pulse,
            machine: Machine
        ): ProcessStep {
            val (newModule, outgoingPulse) = when (module) {
                is Broadcaster -> module to pulse
                is Button -> module to pulse
                is FlipFlop -> {
                    when (pulse) {
                        Pulse.High -> module to null
                        Pulse.Low -> {
                            val (outgoingPulse, newState) = when (module.state) {
                                State.Off -> Pulse.High to State.On
                                State.On -> Pulse.Low to State.Off
                            }
                            module.copy(state = newState) to outgoingPulse
                        }
                    }
                }

                is Conjunction -> {
                    val newMemory = module.memory + (sender to pulse)
                    val outgoingPulse =
                        if (newMemory.values.all { it == Pulse.High }) Pulse.Low else Pulse.High
                    val newModule = module.copy(memory = newMemory)
                    newModule to outgoingPulse
                }
            }
            val outgoingPulses = outgoingPulse?.let { op ->
                module.destinations.map {
                    OutgoingPulse(
                        sender = module.name,
                        receiver = it,
                        pulse = op
                    )
                }
            } ?: emptyList()
            val newMachine = machine.copy(modules = machine.modules + (module.name to newModule))
            val newCount =
                // TODO: Could be done with one traversal
                Count(
                    low = outgoingPulses.count { it.pulse == Pulse.Low }.toBigInteger(),
                    high = outgoingPulses.count { it.pulse == Pulse.High }.toBigInteger()
                )

            return ProcessStep(newMachine, outgoingPulses, newCount)
        }

        fun iterateProcess(
            machine: Machine,
            count: Count
        ): Pair<Machine, Count> {

            tailrec fun recur(
                machine: Machine,
                countFromPreviousSteps: Count,
                outgoingPulses: List<OutgoingPulse>,
            ): Pair<Machine, Count> {
                if (outgoingPulses.isEmpty()) {
                    return machine to countFromPreviousSteps
                } else {

                    val (newCount, newMachine, newOutgoingPulses) = outgoingPulses.fold(
                        Triple(
                            countFromPreviousSteps,
                            machine,
                            emptyList<OutgoingPulse>()
                        )
                    ) { (c, m, op), outgoingPulse ->

                        val step = m.modules[outgoingPulse.receiver]?.let {
                            process(
                                sender = outgoingPulse.sender,
                                module = it,
                                pulse = outgoingPulse.pulse,
                                machine = m,
                            )
                        } ?: ProcessStep(m, emptyList(), Count(BigInteger.ZERO, BigInteger.ZERO))

                        Triple(
                            Count.add(c, step.count),
                            step.machine,
                            op.plus(step.outgoingPulses)
                        )
                    }
                    return recur(newMachine, newCount, newOutgoingPulses)
                }
            }

            return recur(machine, count, listOf(OutgoingPulse("none", "button", Pulse.Low)))
        }
    }
}