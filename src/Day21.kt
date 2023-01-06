private fun main() {
    val extractOperationRegex = """[a-zA-Z+\-/*]+""".toRegex()
    val isOperationRegex = """\w+\s[+\-*/]\s\w+""".toRegex()
    val nodes = mutableMapOf<String, INode>()

    Input("Day21").lines()
        .map { it.split(": ") }
        .forEach { (key, line) ->
            if (isOperationRegex.matches(line)) {
                val (left, op, right) = extractOperationRegex.findAll(line).map { it.value }.toList()
                nodes[key] = OperationNode.fromValues(key, left, op, right)
            } else {
                nodes[key] = NumberNode(key, line.trim().toLong())
            }
        }

    nodes.forEach { (k, v) ->
        if (v is OperationNode) {
            v.left = nodes[v.leftKey]!!
            v.right = nodes[v.rightKey]!!
        }
    }

    val root: OperationNode = nodes["root"]!! as OperationNode

    fun part1() {
        println("part1: ${root.getValue()}")
    }

    fun part2() {
        val path = root.getPath("humn")
        var total = if (root.left in path) root.right.getValue() else root.left.getValue()

        for (node in path.filterIsInstance<OperationNode>().filter { it.getName() != "root" }) {
            if (node.left in path) {
                val r = node.right.getValue()
                total = node.invOperation(total, r)
            }

            if (node.right in path) {
                val l = node.left.getValue()
                total = node.invOperationRight(total, l)
            }
        }

        println("part2: x = $total")
    }

    part1()
    part2()
}

private interface INode {
    fun getName(): String
    fun getValue(): Long
    fun getPath(name: String): Set<INode>
}

private data class NumberNode(val id: String, val constant: Long) : INode {
    override fun getName(): String = id
    override fun getValue(): Long = constant
    override fun getPath(name: String): Set<INode> {
        return if (id == name) setOf(this) else emptySet()
    }
}

private data class OperationNode(
    val id: String,
    val leftKey: String,
    val rightKey: String,
    val operationId: String,
) : INode {
    lateinit var left: INode
    lateinit var right: INode

    companion object {
        fun fromValues(id: String, left: String, op: String, right: String): OperationNode {
            return OperationNode(id, left, right, op)
        }
    }

    override fun getName(): String = id

    override fun getValue(): Long {
        return operation(left.getValue(), right.getValue())
    }

    override fun getPath(name: String): Set<INode> {
        val leftPath = left.getPath(name)
        val rightPath = right.getPath(name)

        if (leftPath.isNotEmpty()) return setOf(this) + leftPath
        if (rightPath.isNotEmpty()) return setOf(this) + rightPath
        return emptySet()
    }

    fun operation(a: Long, b: Long): Long {
        return when (operationId) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> a / b
            else -> error("Invalid operation $operationId")
        }
    }

    fun invOperation(a: Long, b:Long): Long {
        return when(operationId) {
            "+" -> a - b
            "-" -> a + b
            "*" -> a / b
            "/" -> a * b
            else -> error("Invalid operation $operationId")
        }
    }

    fun invOperationRight(a: Long, b:Long): Long {
        return when(operationId) {
            "+" -> a - b
            "-" -> b - a
            "*" -> a / b
            "/" -> b / a
            else -> error("Invalid operation $operationId")
        }
    }
}
