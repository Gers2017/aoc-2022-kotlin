fun main() {
    val input = Input("Day01").doubleLines().map { chunk -> chunk.lines().map { it.toInt() } }
    part1(input)
    part2(input)
}

fun part1(input: List<List<Int>>) {
    val maximum = input.maxOf { g -> g.sumOf { it } }
    println("Part1: $maximum")
}

fun part2(input: List<List<Int>>) {
    val total = input.map { g -> g.sumOf { it } }.sorted().takeLast(3).sum()
    println("Part2: $total")
}