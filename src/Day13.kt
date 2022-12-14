fun main() {
    val lines = Input("Day13").lines().filter { it.isNotEmpty() }
    // part1(lines)
    part2(lines)
}

fun part1(lines: List<String>) {
    val listOfPairs = lines.chunked(2).map { (a, b) -> Pair(packetFromLine(a), packetFromLine(b)) }

    var sum = 0
    for ((i, pair) in listOfPairs.withIndex()) {
        val (a, b) = pair
        val result = compareData(a, b)
        if (result == STATE.OK) sum += (i + 1)
    }

    println("Part1: $sum")
}

fun part2(lines: List<String>) {
    val packets = lines.map { packetFromLine(it) }.toMutableList()
    val p2 = listOf<Any>(listOf(2))
    val p6 = listOf<Any>(listOf(6))
    packets.add(p2)
    packets.add(p6)

    val sortedPackets = mergeSort(packets.toList())
    val p2Index = sortedPackets.indexOf(p2) + 1
    val p6Index = sortedPackets.indexOf(p6) + 1

    println("Part2: ${p2Index * p6Index}")
}

enum class STATE { OK, NEUTRAL, ERR }
typealias Packet = List<Any>
typealias PacketValue = Int

fun compareData(a: Any, b: Any): STATE {
    if (a is PacketValue && b is PacketValue) {
        return if (a == b) STATE.NEUTRAL
        else if (a < b) STATE.OK
        else STATE.ERR
    }

    if (a is List<*> && b is PacketValue)
        return compareData(a, listOf(b))

    if (a is PacketValue && b is List<*>)
        return compareData(listOf(a), b)

    if (a is List<*> && b is List<*>) {

        for ((itemA, itemB) in a.zip(b)) {
            val result = compareData(itemA as Any, itemB as Any)
            if (result != STATE.NEUTRAL)
                return result
        }

        return if (a.size == b.size) STATE.NEUTRAL
        else if (a.size < b.size) STATE.OK
        else STATE.ERR
    }

    error("Invalid state at: $a, $b")
}

fun packetFromLine(line: String): Packet {
    return parsePacket(MyIter(line))
}

fun parsePacket(iter: MyIter): Packet {
    val packet = mutableListOf<Any>()

    if (iter.peek() != '[') error("Invalid iter state, no [ found: $iter")

    while (iter.hasNext() && iter.peek() != ']') {
        iter.advance()

        if (iter.peek() == ',') continue

        if (iter.peek().isDigit()) {
            var result: String = iter.peek().toString()
            iter.advance()

            while (iter.peek().isDigit() && iter.peek() != ',') {
                result += iter.peek()
                iter.advance()
            }

            packet.add(result.toInt())

        } else if (iter.peek() == '[') { // recurse
            val child = parsePacket(iter)
            packet.add(child)
        }
    }

    if (iter.hasNext())
        iter.advance()

    return packet
}

class MyIter(source: String) {
    private val chars: List<Char> = source.toList()
    private var index = 0

    fun peek(): Char = chars[index]

    fun advance() {
        index++
    }

    fun hasNext(): Boolean = index < chars.size
}

fun mergeSort(list: List<Packet>): List<Packet> {
    if (list.size <= 1) return list
    val mid = list.size / 2
    val left = list.subList(0, mid)
    val right = list.subList(mid, list.size)
    return merge(mergeSort(left), mergeSort(right))
}

fun merge(left: List<Packet>, right: List<Packet>): List<Packet> {
    var l = 0
    var r = 0
    val newList = mutableListOf<Packet>()

    while (l < left.size && r < right.size) {
        if (compareData(left[l], right[r]) == STATE.OK) {
            newList.add(left[l])
            l++
        } else {
            newList.add(right[r])
            r++
        }
    }

    while (l < left.size) {
        newList.add(left[l])
        l++
    }

    while (r < right.size) {
        newList.add(right[r])
        r++
    }

    return newList
}