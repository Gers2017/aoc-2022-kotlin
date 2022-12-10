fun main() {
    val motions = Input("Day09").lines().map { Motion.from(it) }
    val head = generateKnots(10) // part1: 2, part2: 10
    val rope = Rope(head)

    for (m in motions) {
        rope.moveHead(m)
    }

    printTailPositions(rope.tailPositions)
    println("tail positions: ${rope.tailPositions.size}")
}

fun generateKnots(amount: Int): Knot {
    var node = Knot()
    for (i in 1 until amount) {
        val lastNode = node
        node = Knot(lastNode)
    }
    return node
}

class Rope(private val head: Knot, val tailPositions: MutableSet<Vector2d> = mutableSetOf(Vector2d.zero())) {
    fun moveHead(m: Motion, print: Boolean = false) {
        for (i in 0 until m.amount) {
            // apply motion to head
            head.position += m.dir
            // update the rest of the knots
            head.next?.apply(head.position, tailPositions)
        }

        if (print) printKnots(head)
    }
}

class Knot(val next: Knot? = null, var position: Vector2d = Vector2d.zero()) {
    fun apply(parentPos: Vector2d, tailPositions: MutableSet<Vector2d>) {
        if (!isConnected(position, parentPos)) {
            val newTailPos = getClosestPoint(position, parentPos)
            position = newTailPos

            // add to tailPositions if this is the tail (next == null)
            if (next == null) {
                tailPositions.add(position)
            } else {
                next.apply(position, tailPositions)
            }
        }
    }
}

fun isConnected(tailPos: Vector2d, headPos: Vector2d): Boolean {
    for (y in -1..1) {
        for (x in -1..1) {
            val point = Vector2d(tailPos.x + x, tailPos.y + y)
            if (point == headPos) return true
        }
    }
    return false
}

fun getClosestPoint(from: Vector2d, target: Vector2d): Vector2d {
    var lowestDist = 14f
    var resultPoint = Vector2d.zero()
    for (y in -1..1) for (x in -1..1) {
        if (x == 0 && y == 0) continue
        val point = Vector2d(from.x + x, from.y + y)
        val dist = point.distanceTo(target)
        if (dist < lowestDist) {
            lowestDist = dist
            resultPoint = point
        }
    }
    return resultPoint
}

fun printTailPositions(tailPositions: MutableSet<Vector2d>) {
    for (y in -12..8) {
        for (x in -16..16) {
            val p = Vector2d(x.toFloat(), y.toFloat())
            if (tailPositions.contains(p)) {
                print("#")
            } else {
                print(".")
            }
        }
        println()
    }
}

fun getKnotList(k: Knot?, ls: MutableList<Knot>) {
    if (k != null) {
        ls.add(k)
        getKnotList(k.next, ls)
    }
}

fun printKnots(head: Knot) {
    val knots: MutableList<Knot> = mutableListOf()
    getKnotList(head, knots)

    for (y in -18..12) {
        for (x in -16..16) {
            val p = Vector2d(x.toFloat(), y.toFloat())
            val k = knots.firstOrNull { it.position == p }
            if (k != null) {
                if (k == head) print("H")
                else print("#")
            } else {
                print(".")
            }
        }
        println()
    }
    println("----".repeat(12))
}

class Motion(val dir: Vector2d, val amount: Int) {
    companion object {
        fun from(s: String): Motion {
            val (dirStr, amount) = s.split(" ")
            val dir = when (dirStr) {
                "U" -> Vector2d(0.0f, -1.0f)
                "L" -> Vector2d(-1.0f, 0.0f)
                "D" -> Vector2d(0.0f, 1.0f)
                "R" -> Vector2d(1.0f, 0.0f)
                else -> error("Invalid Direction: $dirStr")
            }
            return Motion(dir, amount.toInt())
        }
    }
}