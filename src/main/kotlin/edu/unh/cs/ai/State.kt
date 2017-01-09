package edu.unh.cs.ai

/**
 * state specification
 * Created by willi on 12/16/2016.
 */

data class Pair(var x: Int, var y: Int)
data class Dimensions(val width: Int, val height: Int)
abstract class State<T> : Expandable<T>, Visual<T>



