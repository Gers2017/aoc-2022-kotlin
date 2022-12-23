fun main() {
    val cubes: Set<Vec3d> =
        Input("Day18").lines().map { line ->
            val (x, y, z) = line.split(",").map { it.toInt() }.toList()
            Vec3d(x, y, z)
        }.toSet()

    val totalFaces = cubes.size * 6
    val visibleFaces = cubes.sumOf { cube -> 6 - cube.getNeighbors().count { it in cubes } }

    println("total faces = $totalFaces\nvisible faces = $visibleFaces")

    val xRange = cubes.getRange { it.x }
    val yRange = cubes.getRange { it.y }
    val zRange = cubes.getRange { it.z }

    val minPos = Vec3d(xRange.first, yRange.first, zRange.first)
    val queue = mutableListOf<Vec3d>(minPos)
    val seen = mutableSetOf<Vec3d>()
    var exposedFaces = 0

    while (queue.isNotEmpty()) {
        val point = queue.removeFirst()

        if (point in seen) continue

        for (neighbor in point.getNeighbors().filter { it.x in xRange && it.y in yRange && it.z in zRange }) {
            seen.add(point)
            if (neighbor in cubes) {
                exposedFaces++
            } else {
                queue.add(neighbor)
            }
        }
    }

    println("exposed faces = $exposedFaces")
}

data class Vec3d(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: Vec3d): Vec3d {
        return Vec3d(x + other.x, y + other.y, z + other.z)
    }

    operator fun minus(other: Vec3d): Vec3d {
        return Vec3d(x - other.x, y - other.y, z - other.z)
    }

    fun getNeighbors(): List<Vec3d> {
        val dirs3d = listOf(
            Vec3d(0, 1, 0), Vec3d(0, -1, 0),
            Vec3d(0, 0, 1), Vec3d(0, 0, -1),
            Vec3d(1, 0, 0), Vec3d(-1, 0, 0),
        )

        return dirs3d.map { dir -> this + dir }.toList()
    }
}

fun Set<Vec3d>.getRange(selector: (Vec3d) -> Int): IntRange {
    return this.minOf(selector) - 1..this.maxOf(selector) + 1
}
