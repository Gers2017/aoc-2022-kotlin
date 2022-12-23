import kotlin.math.absoluteValue
import kotlin.math.min

private data class CaveState(val shapeIndex: Int, val jetIndex: Int, val ceiling: List<Int>)
private data class CaveSnapshot(val height: Int, val rockCount: Int)
private class Day17(line: String) {
    val jets = jetsToVecList(line)
    val shapes = getRockShapes()
    var rockCount = 0
    var shapeIndex = 0
    var jetIndex = 0
    val roomWidth = 7
    var topYPoint = 0
    var roomPoints: MutableSet<Vec2dInt> = (0 until roomWidth).map { Vec2dInt(it, 0) }.toMutableSet()
    val stateMap = mutableMapOf<CaveState, CaveSnapshot>()

    fun getHeight(): Int {
        return topYPoint.absoluteValue
    }

    fun increaseJetIndex() {
        jetIndex = (jetIndex + 1) % jets.size
    }

    fun increaseShapeIndex() {
        shapeIndex = (shapeIndex + 1) % shapes.size
    }

    fun getShape(): Set<Vec2dInt> {
        val shape = shapes[shapeIndex]
        val start = Vec2dInt(2, topYPoint - 4) // start: 0,0
        return shape.map { it + start }.toSet()
    }

    fun getJetDirection(): Vec2dInt {
        return jets[jetIndex]
    }

    fun getTopYRoomImage(): List<Int> {
        return roomPoints
            .groupBy { it.x }
            .entries.sortedBy { it.key }
            .map { e -> e.value.minOf { p -> p.y } }
            .map { y -> topYPoint - y }
    }

    fun simulate() {
        var shape = getShape()

        while (shape.intersect(roomPoints).isEmpty()) {
            val jetDir = getJetDirection()
            increaseJetIndex()

            val pushedShape = shape.move(jetDir)
            // skip if shape is out of bounds
            if (pushedShape.isInside(0 until roomWidth) && roomPoints.intersect(pushedShape).isEmpty()) {
                shape = pushedShape
            }

            // down = (0, 1)
            shape = shape.down()
        }

        // correct for previous y position
        shape = shape.up()

        increaseShapeIndex()
        rockCount++

        val shapeTopY = shape.topY()
        topYPoint = min(topYPoint, shapeTopY)
        roomPoints.addAll(shape)
    }

    fun simulateWithJump(targetRockCount: Long): Long {
        while (true) {
            // simulate until cycle
            simulate()

            val state = CaveState(shapeIndex, jetIndex, getTopYRoomImage())
            if (state in stateMap) { // found cycle
                val snapshot = stateMap[state]!!
                val heightDiff = getHeight() - snapshot.height
                val rockCountDiff = rockCount - snapshot.rockCount
                val cyclesToSkip = (targetRockCount / rockCountDiff) - 1

                // Skipped from start of second cycle to n cycle
                val heightSkipped = heightDiff * cyclesToSkip
                val rocksSkipped = rockCountDiff * cyclesToSkip

                while ((rockCount - rockCountDiff + rocksSkipped) < targetRockCount) {
                    simulate()
                }

                return (getHeight() - heightDiff) + heightSkipped
            }

            stateMap[state] = CaveSnapshot(getHeight(), rockCount)
        }
    }

    fun printBoard() {
        for (y in topYPoint..-1) {
            for (x in 0 until roomWidth) {
                if (Vec2dInt(x, y) in roomPoints) print("#")
                else print(".")
            }
            println()
        }
        println("-".repeat(roomWidth))
    }
}

typealias Points = Set<Vec2dInt>

fun Points.move(source: Vec2dInt): Points = map { source + it }.toSet()

fun Points.up(): Points = map { it + Vec2dInt(0, -1) }.toSet()

fun Points.down(): Points = map { it + Vec2dInt(0, 1) }.toSet()

fun Points.isInside(r: IntRange): Boolean = all { it.x in r }

fun Points.topY(): Int = minOf { it.y }

fun Points.bottomY(): Int = maxOf { it.y }

fun Points.getByMinYPoints(): List<Vec2dInt> =
    sortedBy { it.x }.groupBy { it.x }.entries.map { e -> Vec2dInt(e.key, e.value.minOf { it.y }) }

fun main() {
    val input = Input("Day17").raw()
    part2(input)
}

fun part1(input: String) {
    val day17 = Day17(input)

    repeat(10) {
        day17.simulate()
    }

    println(day17.getHeight())
}

fun part2(input: String) {
    val day17 = Day17(input)
    val res = day17.simulateWithJump(1000000000000L)
    println(res)
}

fun jetsToVecList(line: String): List<Vec2dInt> =
    line.trim().map {
        when (it) {
            '>' -> Vec2dInt(1, 0)
            '<' -> Vec2dInt(-1, 0)
            else -> error("Invalid jet \"$it\"")
        }
    }

fun getRockShapes(): List<Set<Vec2dInt>> {
    return listOf(
        setOf(Vec2dInt(0, 0), Vec2dInt(1, 0), Vec2dInt(2, 0), Vec2dInt(3, 0)), // horizontal line
        setOf(Vec2dInt(0, -1), Vec2dInt(1, 0), Vec2dInt(1, -1), Vec2dInt(2, -1), Vec2dInt(1, -2)), // + shape
        setOf(Vec2dInt(0, 0), Vec2dInt(1, 0), Vec2dInt(2, 0), Vec2dInt(2, -1), Vec2dInt(2, -2)), // inverted L shape
        setOf(Vec2dInt(0, 0), Vec2dInt(0, -1), Vec2dInt(0, -2), Vec2dInt(0, -3)),  // vertical line
        setOf(Vec2dInt(0, 0), Vec2dInt(1, 0), Vec2dInt(0, -1), Vec2dInt(1, -1)), // square 2x2
    )
}
