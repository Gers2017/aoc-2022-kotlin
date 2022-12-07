import java.io.File

/**
 * Reads lines from the given input txt file.
 */
fun getInput(name: String) = File("src/resources", "$name.txt").readLines()

fun getInputRaw(name: String) = File("src/resources", "$name.txt").readText()

class Input(private val filename: String) {
    private var text: String = File("src/resources", "$filename.txt").readText()
    fun raw() = text
    fun lines() = text.lines()
    fun doubleLines() = text.split("\n\n")
    fun chunked(size: Int) = lines().chunked(size)
}