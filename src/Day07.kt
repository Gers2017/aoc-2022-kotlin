fun main() {
    // 1. Parse the lines to generate instructions
    // 2. Follow the instructions to generate the filesystem
    // 3. Use recursion to get the size of the directories

    val recorded = Input("Day07").lines()
    val rootNode = Dir("/")
    var currentDir: Dir? = rootNode

    val p = Parser(recorded)
    while (!p.isEnd()) {
        if (p.isCdCommand()) {
            val path = p.getCdPath()

            if (path == "..") {
                // One directory up
                currentDir = currentDir?.parent
            } else if (path != rootNode.name) {
                currentDir = currentDir?.findDir(path)
                if (currentDir == null) error("Unknown Directory $path")
            }

            p.advance()
        } else if (p.isLsCommand()) {
            p.advance()

            // take while !atEnd() and !line.isCommand()
            while (!p.isEnd() && !p.isCommand()) {
                val newEntry = if (p.isDir()) {
                    val dirName = p.getDirName()
                    Dir(dirName, parent = currentDir)
                } else {
                    val (fileSize, fileName) = p.getFile()
                    File(fileName, fileSize.toUInt())
                }

                currentDir?.addEntry(newEntry)
                p.advance()
            }
        }
    }

    val dirs = mutableListOf<Dir>()
    traverse(rootNode, dirs)
    val dirSizes = dirs.map { it.getSize() }

    // Part1
    val totalSize = dirSizes.filter { it <= 100_000u }.sum()
    println("Part 1, total size: $totalSize")

    // Part2
    val totalDiskSpace = 70_000_000u
    val updateDiskSpace = 30_000_000u
    val unusedSpace = totalDiskSpace - rootNode.getSize()
    val minDirSize = updateDiskSpace - unusedSpace
    val smallestDirSize = dirSizes.filter { it >= minDirSize }.min()
    println("Part2, size of smallest dir to delete: $smallestDirSize")
}

fun traverse(dir: Dir, dirs: MutableList<Dir>) {
    dirs.add(dir)
    for (entry in dir.entries) {
        if (entry is Dir) traverse(entry, dirs)
    }
}

class Parser(private val source: List<String>) {
    private var index = 0

    fun advance() = index++

    fun isEnd(): Boolean = index >= source.size

    private fun peek(): String = source[index]

    fun isCommand(): Boolean = peek().startsWith("$ ")

    fun isCdCommand(): Boolean = peek().startsWith("$ cd")

    fun isLsCommand(): Boolean = peek().startsWith("$ ls")

    private fun trimCommand(): String = peek().removePrefix("$").trimStart()

    fun getCdPath(): String = trimCommand().split(" ")[1]

    fun isDir(): Boolean = peek().startsWith("dir ")

    fun getDirName(): String = peek().removePrefix("dir").trimStart()

    fun getFile(): List<String> = peek().split(" ")
}

abstract class Entry(val name: String) {
    abstract fun getSize(): UInt
}

class Dir(
    name: String,
    val parent: Dir? = null,
    val entries: MutableList<Entry> = mutableListOf(),
) : Entry(name) {
    fun getRoot(): Entry = parent?.getRoot() ?: this

    fun addEntry(e: Entry) = entries.add(e)

    fun findDir(name: String): Dir? = entries.find { it is Dir && it.name == name } as Dir?

    override fun getSize(): UInt = entries.sumOf { entry -> entry.getSize() }
}

class File(name: String, private val size: UInt) : Entry(name) {
    override fun getSize(): UInt = size
}