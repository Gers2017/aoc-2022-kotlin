fun main() {
    val input = Input("Day04").lines()
        .map { line-> line.split(",") }
        .map { ranges -> ranges.map { Range.fromLine(it) } }

    var total = input.count { (left, right) ->
        left.overlaps(right) || right.overlaps(left)
    }

    println("Part1: $total")

    total = input.count { (left, right) ->
        left.partiallyOverlaps(right) || right.partiallyOverlaps(left)
    }

    println("Part2: $total")
}

class Range(private val start: Int, private val end: Int) {
    companion object {
        fun fromLine(line: String): Range {
            val (start, end) = line.split("-").map { it.toInt() }
            return Range(start, end)
        }
    }
    fun overlaps(other: Range): Boolean =
        this.start <= other.start && this.end >= other.end

    fun partiallyOverlaps(other: Range): Boolean =
        this.start >= other.start && this.start <= other.end ||
                this.end >= other.start && this.end <= other.end
}