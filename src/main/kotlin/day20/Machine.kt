package day20

data class Machine(
    val modules: List<Module>
) {
    companion object {
        fun parse(input: String): Machine {
            val modules = input
                .trim()
                .lines()
                .map { Module.parse(it) }
            return Machine(modules)
        }
    }
    
}
