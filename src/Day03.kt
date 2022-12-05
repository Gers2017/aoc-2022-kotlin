fun main() {
    val input = Input("Day03").lines()
        .map { line -> line.map { charToPriority(it) } }
    part1(input)
    part2(input)
}

fun part1(input: List<List<UInt>>) {
    val sum = input.fold(0) { acc, list ->
        val (left, right) = list.chunked(list.size / 2)
        val common = left.firstOrNull { right.contains(it) } ?: error("No common item found")
        acc + common.toInt()
    }

    println("Part1: $sum")
}

fun part2(input: List<List<UInt>>) {
    val sum = input.chunked(3).fold(0) { acc, chunk ->
        val (a, b, c) = chunk
        val common = a.firstOrNull { b.contains(it) && c.contains(it) } ?: error("No common item found")
        acc + common.toInt()
    }

    println("Part2: $sum")
}

fun charToPriority(ch: Char): UInt =
    if (ch.isLowerCase()) {
        (ch.code - 'a'.code + 1).toUInt()
    } else {
        (ch.code - 'A'.code + 27).toUInt()
    }