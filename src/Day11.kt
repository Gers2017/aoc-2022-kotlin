fun main() {
    val monkeys = Input("Day11")
        .doubleLines().map { chunk ->
            Monkey.fromChunk(chunk.lines())
        }.toMutableList()

    // val relief = 3u.toULong() // Part1
    val p = monkeys.map { it.test.divisor }.reduceRight { d, acc -> d * acc }

    for (round in 1..10000) {
        for (monkeyIndex in monkeys.indices) {
            val monkey = monkeys[monkeyIndex]
            monkey.inspect(monkeys, p)
        }
    }

    val activeMonkeys = monkeys.map { it.inspectedCount }.sortedDescending()
    val mostActive = activeMonkeys.take(2)
    println("Most active monkeys: $mostActive from $activeMonkeys")
    println("Monkey Business: ${mostActive[0] * mostActive[1]}")
}

typealias Operation = (x: ULong, p: ULong) -> ULong

class Monkey(
    private val id: Int,
    private val items: MutableList<ULong>,
    val operation: Operation,
    val test: MonkeyTest,
) {

    var inspectedCount: ULong = 0.toULong()

    fun inspect(monkeys: MutableList<Monkey>, p: ULong, relief: ULong = 0.toULong()) {
        while (items.size > 0) {
            val item = items.removeFirst()
            var worryLevel = operation(item, p)
            if (relief > 0.toULong())
                worryLevel /= relief

            val targetMonkeyID = test.getMonkeyID(worryLevel)

            // throw to target
            monkeys[targetMonkeyID].items.add(worryLevel)
            inspectedCount++
        }
    }

    companion object {
        fun fromChunk(chunk: List<String>): Monkey {
            val lines = chunk.map { it.trim().lowercase() }
            val iter = lines.iterator()

            var id = 0
            val items = mutableListOf<ULong>()
            var operation: Operation = fun(x: ULong, p: ULong): ULong = x % p
            var test = MonkeyTest(0u, 0, 0)

            while (iter.hasNext()) {
                val m = iter.next()
                if (m.startsWith("monkey")) {
                    val idStr = m.removePrefix("monkey ").removeSuffix(":").trim()
                    id = idStr.toInt()
                } else if (m.startsWith("starting items:")) {
                    val values = m.removePrefix("starting items: ")
                        .split(",").map { it.trim().toULong() }
                    items.addAll(values)
                } else
                    if (m.startsWith("operation")) {
                        val (op, other) = m.removePrefix("operation: new = old ").trim().split(" ")
                        val isNumeric = other.all { it.isDigit() }

                        operation = if (isNumeric) {
                            val y = other.toULong()
                            when (op) {
                                "*" -> fun(x: ULong, p: ULong): ULong = ((x % p) * (y % p)).mod(p)
                                "+" -> fun(x: ULong, p: ULong): ULong = ((x % p) + (y % p)).mod(p)
                                else -> error("Invalid operator")
                            }
                        } else {
                               fun(x: ULong, p: ULong): ULong = ((x % p) * (x % p)).mod(p)
                        }

                    } else if (m.startsWith("test: ")) {
                        val divisor = m.removePrefix("test: ").trim().split(" ").last().toULong()
                        var okVal = 0
                        var errVal = 0

                        while (iter.hasNext()) {
                            val line = iter.next()

                            fun getMonkeyId(line: String): Int {
                                return line.split(" ").lastOrNull()?.toInt() ?: error("Unknown line: $line")
                            }

                            if (line.startsWith("if true")) {
                                okVal = getMonkeyId(line)
                            } else if (line.startsWith("if false")) {
                                errVal = getMonkeyId(line)
                            } else {
                                error("Unknown condition at: $m $line")
                            }
                        }

                        test = MonkeyTest(divisor, okVal, errVal)
                    }
            }

            return Monkey(id, items, operation, test)
        }
    }

    override fun toString(): String {
        return "monkey $id:\n$items\n$operation\n$test"
    }
}


class MonkeyTest(val divisor: ULong, private val okMonkeyId: Int, private val errMonkeyId: Int) {
    fun getMonkeyID(dividend: ULong): Int =
        if ((dividend % divisor) == 0.toULong()) okMonkeyId else errMonkeyId

    override fun toString(): String = "Test: if N isDivBy $divisor ? $okMonkeyId : $errMonkeyId"
}