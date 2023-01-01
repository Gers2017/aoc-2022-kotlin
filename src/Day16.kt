fun main() {
    val pattern = """([A-Z]{2,}|\d+)""".toRegex()
    val valveRooms = Input("Day16Test")
        .lines()
        .map { l -> ValveRoom.fromList(pattern.findAll(l).map { it.value }.toList()) }
        .associateBy { it.label }

    val shortestPaths = getShortestPaths(valveRooms)
    // printPaths(shortestPaths)

    fun part1() {
        val highest = findHighestPressure("AA", 30, setOf("AA"), shortestPaths, valveRooms)
        println("Part1 $highest")
    }


    fun part2() {
        val pool = shortestPaths.keys.toList().filter { it != "AA" }
        val combinations = mutableListOf<Set<String>>()
        val depth = (pool.size / 2) - 1
        val keys = shortestPaths.keys

        println("pool: $pool depth: $depth")
        generateCombinations(0, emptySet(), pool, combinations, depth)

        var highest = 0

        for (comb in combinations) {
            val left = findHighestPressure("AA", 26, setOf("AA") + comb, shortestPaths, valveRooms)
            val right = findHighestPressure("AA", 26, setOf("AA") + (keys - comb), shortestPaths, valveRooms)
            highest = maxOf(left + right, highest)
        }

        println("Part2 $highest")
    }

    part2()
}


private fun printPaths(shortestPaths: Map<String, Map<String, Int>>) {
    for ((k, v) in shortestPaths) {
        println("$k: $v")
    }
}

fun findHighestPressure(
    start: String, maxTime: Int, seen: Set<String>,
    shortestPaths: Map<String, Map<String, Int>>, valveRooms: Map<String, ValveRoom>,
    timeElapsed: Int = 0, highestPressure: Int = 0
): Int {
    val results = mutableListOf<Int>()

    for ((nextValve, traversalCost) in shortestPaths[start]!!) {
        if (nextValve in seen || timeElapsed + traversalCost + 1 >= maxTime) continue
        val seenCopy = seen + nextValve
        val newTimeElapsed = timeElapsed + traversalCost + 1
        val rate = valveRooms[nextValve]!!.rate
        val newPressure = highestPressure + (maxTime - timeElapsed - traversalCost - 1) * rate

        val result =
            findHighestPressure(
                nextValve,
                maxTime,
                seenCopy,
                shortestPaths,
                valveRooms,
                newTimeElapsed,
                newPressure
            )

        results.add(result)
    }

    return if (results.size > 0) results.max() else highestPressure
}


data class ValveRoom(val label: String, val rate: Int, val tunnels: List<String>) {
    companion object {
        fun fromList(values: List<String>): ValveRoom {
            val (label, rate) = values
            val tunnels = values.subList(2, values.size)
            return ValveRoom(label, rate.toInt(), tunnels)
        }
    }
}

fun getShortestPaths(valveRooms: Map<String, ValveRoom>): Map<String, Map<String, Int>> {
    val nonZeroValves = valveRooms.values.filter { it.rate > 0 || it.label == "AA" }.map { it.label }
    val paths = mutableMapOf<String, MutableMap<String, Int>>()

    for (src in nonZeroValves) {
        for (target in nonZeroValves.filter { it != src }) {
            val distance = bsf(src, target, valveRooms)
                ?: error("Couldn't find path for $src to $target")

            if (src !in paths) {
                paths[src] = mutableMapOf()
            }

            paths[src]!![target] = distance
        }
    }

    return paths
}

data class ValveTraversal(val valve: String, val timeElapsed: Int) : Comparable<ValveTraversal> {
    override fun compareTo(other: ValveTraversal): Int {
        return other.timeElapsed.compareTo(timeElapsed)
    }
}

val bfsMemo = mutableMapOf<String, Int>()
fun bsf(source: String, target: String, valves: Map<String, ValveRoom>): Int? {
    val key = listOf(source, target).sorted().joinToString("-")
    if (key in bfsMemo) {
        return bfsMemo[key]!!
    }

    val queue = mutableListOf<ValveTraversal>()
    queue.add(ValveTraversal(source, 0))
    val seen = mutableSetOf(source)

    while (queue.isNotEmpty()) {
        val (current, timeElapsed) = queue.removeFirst()

        if (current == target) {
            bfsMemo[key] = timeElapsed
            return timeElapsed
        }

        for (next in valves[current]!!.tunnels) {
            if (next in seen) continue
            val time = timeElapsed + 1 // 1 step
            seen.add(next)
            queue.add(ValveTraversal(next, time))
        }
    }

    return null
}

fun generateCombinations(
    start: Int,
    previous: Set<String>,
    values: List<String>,
    results: MutableList<Set<String>>,
    depth: Int
) {
    for (i in start until values.size) {
        if (depth == 0) {
            results.add(previous + setOf(values[i]))
        } else {
            generateCombinations(i + 1, previous + setOf(values[i]), values, results, depth - 1)
        }
    }
}
