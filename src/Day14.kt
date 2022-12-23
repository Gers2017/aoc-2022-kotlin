fun main() {
    val listOfRanges = Input("Day14").lines().map { pointsFromLine(it) }

    var minX = Float.MAX_VALUE
    var maxX = 0f
    var maxY = 0f

    for (p in listOfRanges.flatten()) {
        if (p.x > maxX) maxX = p.x
        if (p.x < minX) minX = p.x
        if (p.y > maxY) maxY = p.y
    }

    val blocks = mutableSetOf<Vector2d>()
    for (points in listOfRanges) {
        for (i in 0 until points.size - 1) {
            val current = points[i]
            val target = points[i + 1]
            addStepsToBlocks(current, target, blocks)
        }
    }

    // val sandPoints = simulateSand(blocks, -1f, maxY)
    val sandPoints = simulateSand(blocks, maxY + 2, 0f) // Part2

    // print stuff
    printMap(sandPoints, blocks, maxX, minX, maxY + 2)
    println(sandPoints.size)
}


fun simulateSand(blocks: MutableSet<Vector2d>, floor: Float, stopY: Float): MutableSet<Vector2d> {
    val DOWN = Vector2d(0f, 1f)
    val DOWN_LEFT = Vector2d(-1f, 1f)
    val DOWN_RIGHT = Vector2d(1f, 1f)
    val sandPoints = mutableSetOf<Vector2d>()
    var canSimulate = true

    while (canSimulate) {
        // Move a single sand point
        var sandPos = Vector2d(500f, 0f)
        var canMove = true

        while (canMove) {
            if (sandPos + DOWN in blocks || sandPos.y + 1 == floor) {
                if (sandPos + DOWN_LEFT !in blocks && sandPos.y + 1 != floor) {
                    sandPos += DOWN_LEFT
                } else if (sandPos + DOWN_RIGHT !in blocks && sandPos.y + 1 != floor) {
                    sandPos += DOWN_RIGHT
                } else { // stop moving
                    blocks.add(sandPos)
                    sandPoints.add(sandPos)
                    canMove = false
                }
            } else {
                sandPos += DOWN
            }

            if (sandPos.y == stopY) {
                canSimulate = false
                break
            }
        }
    }

    return sandPoints
}

fun printMap(
    sandPoints: MutableSet<Vector2d>,
    blocks: MutableSet<Vector2d>,
    maxX: Float,
    minX: Float,
    maxY: Float
) {
    for (y in 0..maxY.toInt()) {
        for (x in minX.toInt() - 4..maxX.toInt() + 4) {
            when (Vector2d(x.toFloat(), y.toFloat())) {
                in sandPoints -> print("O")
                in blocks -> print("#")
                else -> print(".")
            }
        }
        println()
    }
}

fun pointsFromLine(line: String): List<Vector2d> {
    val ranges = line.split("->").map { it.trim().split(",") }
    return ranges.map { (x, y) -> Vector2d(x.toFloat(), y.toFloat()) }
}

fun addStepsToBlocks(from: Vector2d, target: Vector2d, blocks: MutableSet<Vector2d>) {
    var current = from
    val dir = current.directionTo(target).ceil()

    while (current != target) {
        blocks.add(current)
        current += dir
    }

    blocks.add(target)
}
