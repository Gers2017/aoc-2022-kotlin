fun main() {
    val input = Input("Day10").lines()
    val instructions = input.map { l ->
        if (l.startsWith("noop")) Noop.new() else Add.from(l)
    }

    // CPU
    var cycle = 0
    var x = 1
    var totalSigStrength = 0
    val cyclesToCap = (20..220).step(40)
    // CRT
    val crtWide = 40
    val crtHigh = 6
    var crtX = 0
    var crtY = 0
    val crtArea: List<MutableList<Char>> = (0 until crtHigh).map { (0 until crtWide).map { '.' }.toMutableList() }
    val endOfCrtCycles = (40..240).step(40)
    // sprite center = x, sprite left pixel: x - 1, sprite right pixel: x + 1
    fun getSpriteRange(): IntRange = (x - 1 .. x + 1)

    for (ins in instructions) {
        for (i in 0 until ins.cyclesToComplete) {
            cycle++

            // Draw CRT here...
            // if any pixel of sprite is visible
            if (crtX in getSpriteRange()) {
                crtArea[crtY][crtX] = '#'
            }

            // Increase crtX pos each cycle!
            crtX++

            if (crtX in endOfCrtCycles) { // if is end of row
                crtX = 0 // reset crtX pos to 0
                crtY++   // and increase the y value
            }

            // part1:
            if (cycle in cyclesToCap) {
                totalSigStrength += (cycle * x)
            }
        }

        x += ins.value
    }

    println("part1: $totalSigStrength")
    println("---".repeat(4))
    println("part2:")
    for (row in crtArea) {
        for (item in row) {
            print(item)
        }
        println()
    }
}

open class Instruction(val cyclesToComplete: Int, val value: Int)

class Noop : Instruction(1, 0) {
    companion object {
        fun new(): Noop = Noop()
    }
}

class Add(value: Int) : Instruction(2, value) {
    companion object {
        fun from(line: String): Add {
            val (_, value) = line.split(" ")
            return Add(value.toInt())
        }
    }
}