import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.math.ceil

data class Vector2d(var x: Float, var y: Float) {
    companion object {
        fun zero(): Vector2d = Vector2d(0.0f, 0.0f)
    }

    operator fun plus(other: Vector2d): Vector2d = Vector2d(x + other.x, y + other.y)
    operator fun minus(other: Vector2d): Vector2d = Vector2d(x - other.x, y - other.y)

    override fun toString(): String {
        return "($x, $y)"
    }

    fun distanceTo(other: Vector2d): Float {
        val dx = abs(x - other.x)
        val dy = abs(y - other.y)
        return sqrt(dx * dx + dy * dy)
    }

    fun directionTo(target: Vector2d): Vector2d {
        return (target - this).normalized()
    }

    fun magnitude(): Float {
        return sqrt(x * x + y * y)
    }

    fun normalized(): Vector2d {
        val m = magnitude()
        return Vector2d(x / m, y / m)
    }

    fun ceil(): Vector2d {
        return Vector2d(ceil(x), ceil(y))
    }

    fun difference(other: Vector2d): Float {
        return sqrt(abs(x - other.x) + abs(y - other.y))
    }

}