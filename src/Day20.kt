data class ItemIndexed(val index: Int, val value: Long)

fun main() {
    val values = Input("Day20").lines().map { it.trim().toLong() }
    part1(values)
    part2(values)
}

fun part1(values: List<Long>) {
    values.mapIndexed { i, item -> ItemIndexed(i, item) }
        .toMutableList()
        .apply { mixDecrypt(this) }
        .also {
            // println(it.map { item -> item.value }.joinToString(", "))
            println("part1: ${getGroveCoordinates(it)}")
        }
}

fun part2(values: List<Long>) {
    val dk = 811589153
    values.mapIndexed { i, item -> ItemIndexed(i, item * dk) }
        .toMutableList()
        .apply { repeat(10) { mixDecrypt(this) } }
        .also {
            println("part2: ${getGroveCoordinates(it)}")
        }

}

fun mixDecrypt(items: MutableList<ItemIndexed>) {
    for (index in items.indices) {
        val indexInMut = items.indexOfFirst { it.index == index }
        val removed = items.removeAt(indexInMut)
        val nextIndex = (indexInMut + removed.value).mod(items.size)
        items.add(nextIndex, removed)
    }
}

fun getGroveCoordinates(items: MutableList<ItemIndexed>): Long {
    val startZero = items.indexOfFirst { it.value == 0L }
    return listOf(1000, 2000, 3000).map { (startZero + it).mod(items.size) }.sumOf { items[it].value }
}
