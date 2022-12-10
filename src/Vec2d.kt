import kotlin.math.abs
import kotlin.math.sqrt

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
}