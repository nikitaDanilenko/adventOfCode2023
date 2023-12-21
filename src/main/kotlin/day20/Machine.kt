package day20

data class Machine(
    val modules: Map<String, Module>
) {
    companion object {
        fun parse(input: String): Machine {
            val preModules = input
                .trim()
                .lines()
                .map {
                    Module.parse(it)
                }.plusElement(Module.Button)

            val modules = preModules.map { module ->
                when (module) {
                    is Module.Conjunction -> {
                        val initialMemory = preModules.filter { it.destinations.contains(module.name) }
                            .associate { it.name to Module.Pulse.Low }
                        module.copy(memory = initialMemory)
                    }

                    else -> module
                }
            }.associateBy { it.name }
            return Machine(modules)
        }
    }

}
