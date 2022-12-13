fun main() {
    val items = Input("Day12").lines()
        .mapIndexed { r, line -> line.mapIndexed { c, char -> Cell(char, r, c) }.toMutableList() }

    val grid = Grid(items)
    val (strRow, strCol) = grid.find { it.ch == 'E' } ?: error("No E found")
    val (endRow, endCol) = grid.find { it.ch == 'S' } ?: error("No S found")
    grid.replace(endRow, endCol, Cell('a', endRow, endCol)) // S
    grid.replace(strRow, strCol, Cell('z', strRow, strCol)) // E

    val steps = grid.findTarget(strRow, strCol, endRow, endCol, 'a')
}

class Grid(val items: List<MutableList<Cell>>) {
    val rows = items.size
    val columns = items[0].size

    fun get(row: Int, col: Int): Cell = items[row][col]

    fun find(predicate: (Cell) -> Boolean): Pair<Int, Int>? {
        for ((y, row) in items.withIndex()) {
            for ((x, item) in row.withIndex()) {
                if (predicate(item)) return Pair(y, x)
            }
        }
        return null
    }

    fun replace(row: Int, col: Int, cell: Cell) {
        items[row][col] = cell
    }

    private fun getNeighbors(row: Int, col: Int): List<Cell> {
        val neighbors = mutableListOf<Cell>()
        val dirs = listOf(Pair(0, -1), Pair(1, 0), Pair(-1, 0), Pair(0, 1))
        for ((dRow, dCol) in dirs) {
            if (isInRange(row + dRow, col + dCol)) {
                neighbors.add(get(row + dRow, col + dCol))
            }
        }

        return neighbors
    }

    private fun isInRange(r: Int, c: Int): Boolean {
        return (r in 0 until rows) && (c in 0 until columns)
    }

    fun findTarget(
        strRow: Int,
        strCol: Int,
        endRow: Int,
        endCol: Int,
        chTarget: Char? = null
    ): List<Cell> {
        val queue = mutableListOf<Cell>()
        val seen = mutableSetOf<Pair<Int, Int>>()
        val parent = mutableMapOf<Cell, Cell?>()

        // init
        val start = get(strRow, strCol)
        val target = get(endRow, endCol)
        queue.add(start)
        seen.add(start.toPair())
        parent[start] = null
        var i = 1

        while (queue.size > 0) {
            val cell = queue.removeFirst()
            if (chTarget == cell.ch || cell.row == target.row && cell.col == target.col) {
                var p = parent[cell]
                val path = mutableListOf<Cell>()
                while (p != null) {
                    path.add(p)
                    p = parent[p]
                }

                path.reversed()

                printPath(this, start, path)
                println("Cycles: $i")
                println("Distance from start: ${cell.distanceFromStart}")
                return path
            }

            for (nCell in getNeighbors(cell.row, cell.col)) {
                if (seen.contains(nCell.toPair())) continue
                // 'b' - 'a' == 1 | 'a' - 'b' == -1
                // 'c' - 'a' == 2 | 'a' - 'c' ==  -2
                if (nCell.ch - cell.ch < -1) continue

                nCell.distanceFromStart = cell.distanceFromStart + 1
                seen.add(nCell.toPair())
                queue.add(nCell)
                parent[nCell] = cell
            }

            i++
        }

        return listOf()
    }
}

data class Cell(val ch: Char, val row: Int, val col: Int, var distanceFromStart: Int = 0) {
    fun toPair(): Pair<Int, Int> = Pair(row, col)
}

fun printPath(grid:Grid, start: Cell, path:List<Cell>){
    for (y in 0 until grid.rows) {
        for (x in 0 until grid.columns) {
            if (y == start.row && x == start.col) {
                print("$")
                continue
            }

            val c = path.find { it.row == y && it.col == x }

            if (c != null) {
                print(grid.get(c.row, c.col).ch)
            } else {
                print(".")
            }
        }
        println()
    }
}