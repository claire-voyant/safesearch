package edu.unh.cs.ai

import java.util.*

/**
 * interface specifying valid state implementation
 * Created by doylew on 1/9/17.
 */

interface Expandable<T> {
    fun heuristic() : Double
    fun successors() : ArrayList<Node<T>>
    fun safe_successors() : ArrayList<SafeNode<T>>
    fun isGoal() : Boolean
    fun transition(action: Action) : State<T>?
    fun validState() : Boolean
    fun isSafe() : Boolean
    fun nonNegativePosition() : Boolean
    fun copy() : State<T>
}

interface Visual<T> {
    fun visualize() : Unit
}