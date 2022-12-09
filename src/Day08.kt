import kotlin.math.max

fun main() {
    val grid = Input("Day08").lines().map { it.toCharArray().map { ch -> ch.digitToInt(10) } }
    var visibleCount = grid.size * 2 + (grid[0].size - 2) * 2
    var maxScore = 0

    for (y in 1 until grid.size - 1){
        for (x in 1 until grid[y].size - 1) {
            if (isVisible(grid, x, y)) visibleCount++
            if (grid[y][x] == 9) continue
            maxScore = max(maxScore, getScenicScore(grid, x, y))
        }
    }

    println("$visibleCount")
    println("$maxScore")
}

fun isVisible(grid: List<List<Int>>, x: Int, y: Int): Boolean {
    val current = grid[y][x]
    val left = (x - 1 downTo 0).all { grid[y][it] < current }
    val right = (x + 1 until grid[0].size).all { grid[y][it] < current }
    val top = (y - 1 downTo 0).all { grid[it][x] < current }
    val bottom = (y + 1 until grid.size).all { grid[it][x] < current }
    return left || right || top || bottom
}

fun getScenicScore(grid: List<List<Int>>, x: Int, y: Int): Int {
    val current = grid[y][x]
    var top = 0
    var left = 0
    var bottom = 0
    var right = 0

    for (dx in x - 1 downTo 0) {
        left++
        if (grid[y][dx] >= current) break
    }

    for (dx in x + 1 until grid[0].size) {
        right++
        if (grid[y][dx] >= current) break
    }

    for (dy in y - 1 downTo 0) {
       top++
       if (grid[dy][x] >= current) break
    }

    for (dy in y + 1 until grid.size) {
        bottom++
        if (grid[dy][x] >= current) break
    }

    return top * left * bottom * right
}