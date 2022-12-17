import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.system.exitProcess

fun main() {
    val isTest = false
    val filename = if (isTest) "Day15Test" else "Day15"
    val minY = 0
    val maxY = if (isTest) 20 else 4000000
    val targetY = if (isTest) 10 else 2000000

    val p = """-?\d+""".toRegex()
    val lines = Input(filename).lines().map {
        p.findAll(it).map { m -> m.value.toInt() }.toList()
    }

    val scanners: List<Scanner> = lines.map { Scanner.fromPoints(it) }

    for (Y in minY..maxY) {
        if (Y % 1000000 == 0) println("ping $Y")
        val ranges = mutableListOf<IntRange>()

        for (s in scanners) {
            val dist = s.dist
            val head = s.y - dist
            val tail = s.y + dist

            if (Y !in head..tail) continue

            val right = s.x + dist
            val left = s.x - dist

            when (Y) {
                s.y -> ranges.add(left..right)
                in head until s.y -> {
                    val lx = getXIntersect(s.x, head, left, s.y, Y) // down right -1
                    val rx = getXIntersect(s.x, head, right, s.y, Y) // down left 1
                    ranges.add(lx..rx)
                }

                in s.y + 1..tail -> {
                    val lx = getXIntersect(s.x, tail, left, s.y, Y) // down left 1
                    val rx = getXIntersect(s.x, tail, right, s.y, Y) // down right -1
                    ranges.add(lx..rx)
                }
            }
        }

        if (ranges.first().first < 0 && ranges.first().last < 0) continue

        // part1
//        if (Y != targetY) continue
//        ranges.sortBy { it.first() }
//        val merged = mergeRanges(ranges)
//        part1(Y, merged)


        // part2
        ranges.sortBy { it.first() }
        val merged = mergeRanges(ranges)
        if (merged.size > 1) part2(Y, merged)
    }

}

private fun part1(currentY: Int, merged: MutableList<IntRange>) {
    println("$currentY -> $merged")
    val total = merged.sumOf { abs(it.first) + abs(it.last) }
    println("total: $total")
    exitProcess(0)
}

private fun part2(currentY: Int, merged: MutableList<IntRange>) {
    println("merged from: $currentY: $merged")
    val r1 = merged.first()
    val lastPointX: ULong = r1.last.toULong() + 1u
    val result = (lastPointX) * 4000000u + currentY.toULong()
    println("Result: $result")
    exitProcess(0)
}

class Scanner(val x: Int, val y: Int, val dist: Int) {
    companion object {
        fun fromPoints(points: List<Int>): Scanner {
            val (sx, sy, bx, by) = points
            val dist = abs(sx - bx) + abs(sy - by)
            return Scanner(sx, sy, dist)
        }
    }
}

fun M(x1: Int, y1: Int, x2: Int, y2: Int): Int {
    return (y1 - y2) / (x1 - x2)
}

fun getX(x: Int, y: Int, m: Int, Y: Int): Int {
    return x + ((Y - y) / m)
}

fun getXIntersect(x1: Int, y1: Int, x2: Int, y2: Int, Y: Int): Int {
    val m = M(x1, y1, x2, y2)
    return getX(x1, y1, m, Y)
}

fun mergeRanges(ranges: MutableList<IntRange>): MutableList<IntRange> {
    if (ranges.size <= 1) return ranges
    val result = mutableListOf<IntRange>()
    var current = ranges.removeFirst()

    for ((i, other) in ranges.withIndex()) {
        current = if (other.first in current.first..(current.last + 1)) {
            min(current.first, other.first)..max(current.last, other.last)
        } else {
            result.add(current)
            other
        }

        if (i == ranges.size - 1) {
            result.add(current)
        }
    }

    return result
}
