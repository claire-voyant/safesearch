package edu.unh.cs.ai

import java.util.*

/**
 * vehicle domain represents the obstacles
 * and the agent position of a gridworld
 * Created by doylew on 12/30/16.
 */

enum class Action{
    NORTH,
    EAST,
    SOUTH,
    WEST,
    WAIT,
    START
}

fun isGoal(state: State) : Boolean {
    return false
}

fun successors(state: State) : ArrayList<Node> {

    return ArrayList<Node>()
}