import java.util.Stack

fun main() {
    val isCrateMover9001 = true
    val (inputPart1, inputPart2) = Input("Day05").doubleLines()
    val listOfCrates = inputPart1.lines().dropLast(1).map { line -> lineToCrates(line) }
    val columns = getColumns(listOfCrates)

    getProcedures(inputPart2.lines()).forEach { proc ->
        var items = (1..proc.amount).mapNotNull { columns[proc.from]?.pop() }
        items = if (isCrateMover9001) {
            items.asReversed()
        } else {
            items
        }

        columns[proc.to]?.addAll(items) ?: error("Key error: ${proc.from} is not a key")
    }

    val message = columns.values.joinToString("") { it.peek().removeSurrounding("[", "]") }
    val model = if (isCrateMover9001) "CrateMover9001" else "CrateMover9000"
    println("Message: $message with $model")
}

class Procedure(val amount: Int, val from: Int, val to: Int) {}

fun lineToCrates(line: String): List<String> =
    (3..line.length + 1 step 4).map { i ->
        val end = if (line.length < i) line.length else i
        line.slice(i - 3 until end)
    }

//class Pair<T, U>(val left: T, val right: U) {}

fun getColumns(listOfCrates: List<List<String>>): MutableMap<Int, Stack<String>> {
    return listOfCrates.reversed().flatMap { crates ->
        crates.mapIndexed { i, crate -> Pair(i, crate) }
    }.filter { it.second.isNotBlank() }
        .fold(mutableMapOf<Int, Stack<String>>()) { columns, pair ->
            if (columns.containsKey(pair.first)) {
                columns[pair.first]?.push(pair.second)
            } else {
                columns[pair.first] = Stack<String>()
                columns[pair.first]!!.push(pair.second)
            }

            columns
        }
}

fun getProcedures(lines: List<String>): List<Procedure> =
    lines.map { line ->
        val re = """move\s(\d+)\sfrom\s(\d+)\sto\s(\d+)""".toRegex()
        val match = re.find(line)?.groupValues?.drop(1)?.map { it.toInt() } ?: error("Invalid line: $line")
        val (amount, from, to) = match
        Procedure(amount, from - 1, to - 1)
    }