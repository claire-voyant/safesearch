package edu.unh.cs.ai

/**
 * state specification
 * Created by willi on 12/16/2016.
 */

data class Pair(var x: Int, var y: Int) {
    override fun equals(other: Any?): Boolean {
        if (other is Pair) {
            return this.x == other.x && this.y == other.y
        }
        return false
    }

    override fun hashCode(): Int {
        return x xor y
    }
}
data class Obstacle(var x: Int, var y: Int, var dx: Int, var dy: Int) {
    override fun equals(other: Any?): Boolean {
        if (other is Obstacle) {
            return this.x == other.x && this.y == other.y && this.dx == other.dx && this.dy == other.dy
        }
        return false
    }

    override fun hashCode(): Int {
        return x xor y xor dx xor dy
    }
}
data class Dimensions(val width: Int, val height: Int)
abstract class State<T> : Expandable<T>, Visual<T>



