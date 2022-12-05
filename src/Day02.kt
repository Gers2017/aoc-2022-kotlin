fun main() {
    val input = Input("Day02").lines()
    part1(input)
    part2(input)
}

typealias Move = Int

class Turn(val self: Move, val opp: Move) {}

fun Move.winnerAgainst(): Move = if (this - 1 >= 1) this - 1 else 3
fun Move.loserAgainst(): Move = if (this + 1 <= 3) this + 1 else 1

fun String.toMove(): Move = when (this) {
    "A", "X" -> 1
    "B", "Y" -> 2
    "C", "Z" -> 3
    else -> error("Invalid string \"$this\" to move")
}

fun String.toStrategy(oppMove: Move) = when (this) {
    "X" -> oppMove.winnerAgainst() // let opponent win
    "Y" -> oppMove // draw
    "Z" -> oppMove.loserAgainst() // win the match
    else -> error("Invalid string \"$this\" to move")
}

fun outcome(myMove: Move, oppMove: Move): Int = when (oppMove) {
    myMove.winnerAgainst() -> {
        myMove + 6 // win
    }

    myMove.loserAgainst() -> {
        myMove // loose
    }

    else -> {
        myMove + 3 // draw
    }
}

fun part1(input: List<String>) {
    val total = input.map { line ->
        val (a, b) = line.split(" ").map { it.toMove() }
        Turn(b, a)
    }.fold(0) { acc, turn ->
        acc + outcome(turn.self, turn.opp)
    }

    println("Part1: $total")
}

fun part2(input: List<String>) {
    val total = input.map { line ->
        val (a, b) = line.split(" ")
        val oppMove = a.toMove()
        val myMove = b.toStrategy(oppMove)
        Turn(myMove, oppMove)
    }.fold(0) { acc, turn ->
        acc + outcome(turn.self, turn.opp)
    }

    println("Part2: $total")
}