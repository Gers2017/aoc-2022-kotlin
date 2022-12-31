import java.util.PriorityQueue
import kotlin.math.ceil

fun main() {
    val blueprints = Input("Day19").lines().map { line -> Blueprint.fromLine(line) }

    fun part1(blueprints: List<Blueprint>) {
        var total = 0
        for (blueprint in blueprints) {
            val maxGeodes = findMaxGeodes(blueprint, 24)
            total += (blueprint.id * maxGeodes)
            // println("Max geodes for blueprint ${blueprint.id} = $maxGeodes")
        }

        println("Part1 result: $total")
    }

    fun part2(blueprints: List<Blueprint>) {
        var total = 1

        for (blueprint in blueprints.take(3)) {
            val maxGeodes = findMaxGeodes(blueprint, 32)
            // println("Max geodes for blueprint ${blueprint.id} = $maxGeodes")
            total *= maxGeodes
        }

        println("Part2 result: $total")
    }

    // part1(blueprints)
    part2(blueprints)
}


fun findMaxGeodes(blueprint: Blueprint, maxTime: Int): Int {
    var maxGeodes = 0

    val queue = PriorityQueue<GameState>()
    queue.add(GameState())

    while (queue.isNotEmpty()) {
        val state = queue.poll()

        if (state.canProduceMoreGeodesThanMaxGeodes(maxGeodes, maxTime)) {
            queue.addAll(state.genNextGameStates(blueprint, maxTime))
        }

        maxGeodes = maxOf(state.geode, maxGeodes)
    }

    return maxGeodes
}

enum class Material { ORE, CLAY, OBSIDIAN, GEODE }

data class Blueprint(
    val id: Int,
    val robotRecipes: Map<Material, Recipe>,
    val maxOre: Int = robotRecipes.values.maxOf { it.oreNeeded },
    val maxClay: Int = robotRecipes.values.maxOf { it.clayNeeded },
    val maxObsidian: Int = robotRecipes.values.maxOf { it.obsidianNeeded },
) {
    companion object {

        fun fromLine(line: String): Blueprint {
            val p = """\d+""".toRegex()
            val numbers = p.findAll(line).map { it.value.toInt() }.toList()
            val (id, oreBotOreCost, clayBotOreCost) = numbers
            val (obsidianBotOreCost, obsidianBotClayCost) = numbers.subList(3, 5)
            val (geodeBotOreCost, geodeBotObsidianCost) = numbers.subList(5, numbers.size)

            return Blueprint(
                id,
                mapOf(
                    Material.ORE to Recipe(oreBotOreCost, 0, 0),
                    Material.CLAY to Recipe(clayBotOreCost, 0, 0),
                    Material.OBSIDIAN to Recipe(obsidianBotOreCost, obsidianBotClayCost, 0),
                    Material.GEODE to Recipe(geodeBotOreCost, 0, geodeBotObsidianCost),
                )
            )
        }
    }

    fun buildBot(state: GameState, robotType: Material): GameState {
        return robotRecipes[robotType]!!.getFutureState(state, robotType)
    }
}

fun calculateTurns(has: Int, needs: Int, increment: Int): Int {
    return if (has >= needs) 0 else ceil((needs - has) / increment.toFloat()).toInt()
}

data class Recipe(
    val oreNeeded: Int,
    val clayNeeded: Int,
    val obsidianNeeded: Int
) {
    fun getFutureState(gameState: GameState, robotType: Material): GameState {
        val waitTime = 1 + maxOf(
            calculateTurns(gameState.ore, oreNeeded, gameState.oreBots),
            calculateTurns(gameState.clay, clayNeeded, gameState.clayBots),
            calculateTurns(gameState.obsidian, obsidianNeeded, gameState.obsidianBots),
        )

        val robotsToBuild = Material.values().map { if (it == robotType) 1 else 0 }

        return GameState(
            time = gameState.time + waitTime,

            listOf(
                (gameState.ore - oreNeeded) + (waitTime * gameState.oreBots), // ore
                (gameState.clay - clayNeeded) + (waitTime * gameState.clayBots), // clay
                (gameState.obsidian - obsidianNeeded) + (waitTime * gameState.obsidianBots), // obsidian
                gameState.geode + (waitTime * gameState.geodeBots), // geode
            ),
            listOf(
                gameState.oreBots + robotsToBuild[0],
                gameState.clayBots + robotsToBuild[1],
                gameState.obsidianBots + robotsToBuild[2],
                gameState.geodeBots + robotsToBuild[3],
            )

        )
    }
}

fun GameState.canProduceMoreGeodesThanMaxGeodes(maxGeodes: Int, maxTime: Int): Boolean {
    val timeLeft = maxTime - time
    val geodesOnTimeLeft = (0 until timeLeft).sumOf { it + geodeBots }
    return geode + geodesOnTimeLeft > maxGeodes
}

data class GameState(
    val time: Int = 1,
    val materials: List<Int> = listOf(1, 0, 0, 0),
    val robots: List<Int> = listOf(1, 0, 0, 0),
) : Comparable<GameState> {

    val ore: Int get() = materials[0]
    val clay: Int get() = materials[1]
    val obsidian: Int get() = materials[2]
    val geode: Int get() = materials[3]

    val oreBots: Int get() = robots[0]
    val clayBots: Int get() = robots[1]
    val obsidianBots: Int get() = robots[2]
    val geodeBots: Int get() = robots[3]

    override fun compareTo(other: GameState): Int {
        return other.geode.compareTo(geode)
    }

    fun genNextGameStates(blueprint: Blueprint, maxTime: Int): List<GameState> {
        val states = mutableListOf<GameState>()

        fun addState(state: GameState) {
            if (state.time <= maxTime) states.add(state)
        }

        if (ore < blueprint.maxOre && ore > 0) {
            addState(blueprint.buildBot(this, Material.ORE)) // build ore bot
        }

        if (clay < blueprint.maxClay && ore > 0) {
            addState(blueprint.buildBot(this, Material.CLAY)) // build clay bot
        }

        if (obsidian < blueprint.maxObsidian && ore > 0 && clay > 0) {
            addState(blueprint.buildBot(this, Material.OBSIDIAN)) // build obsidian bot
        }

        if (ore > 0 && obsidian > 0) {
            addState(blueprint.buildBot(this, Material.GEODE)) // build geode bot
        }

        return states
    }
}
