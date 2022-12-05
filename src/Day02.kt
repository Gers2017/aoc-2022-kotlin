fun main() {
    val input = Input("Day02").lines().map { line -> line.split(" ") }
    part1(input)
    part2(input)
}

fun part1(input: List<List<String>>) {
    val total = input.map { moves -> moves.map { it.toMove() } }
        .fold(0) { acc, (opponent, player) ->
            acc + outcome(player, opponent)
        }
    println("Part1: $total")
}

fun part2(input: List<List<String>>) {
    val total = input.map { (opponent, player) ->
        val opponentMove = opponent.toMove()
        val playerMove = player.toStrategyMove(opponentMove)
        listOf(opponentMove, playerMove)
    }.fold(0) { acc, (opponentMove, playerMove) ->
        acc + outcome(playerMove, opponentMove)
    }

    println("Part2: $total")
}

typealias Move = Int

fun Move.getLooserMove(): Move = if (this - 1 >= 1) this - 1 else 3
fun Move.getWinnerMove(): Move = if (this + 1 <= 3) this + 1 else 1

fun String.toMove(): Move = when (this) {
    "A", "X" -> 1
    "B", "Y" -> 2
    "C", "Z" -> 3
    else -> error("Invalid string \"$this\" to move")
}

fun String.toStrategyMove(oppMove: Move) = when (this) {
    "X" -> oppMove.getLooserMove() // let opponent win
    "Y" -> oppMove // draw
    "Z" -> oppMove.getWinnerMove() // win the match
    else -> error("Invalid string \"$this\" to move")
}

fun outcome(player: Move, opponent: Move): Int = when (opponent) {
    player.getLooserMove() -> player + 6 // win
    player.getWinnerMove() -> player // loose
    else -> player + 3 // draw
}