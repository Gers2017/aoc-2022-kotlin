fun main() {
    val stream = Input("Day06").raw().trim().toList()
    val queue = mutableListOf<Char>()
    val chunkSize = 14

    for (i in stream.indices) {
        val ch = stream[i]
        queue.add(ch)

        if (queue.size > chunkSize) {
            queue.removeFirst()
        }

        if (queue.size == chunkSize) {
            if (queue.toSet().size == queue.size) {
                println("Found at ${i + 1}")
                break
            }
        }
    }
}