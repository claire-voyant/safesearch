package edu.unh.cs.ai

import java.util.*

/**
 * basic gridworld find goal
 * stationary obstacles
 * Created by doylew on 1/9/17.
 */

private val invalidState = GridWorldState(Dimensions(-1, -1), Pair(-1, -1), Pair(-1, -1), ArrayList<Pair>())

class GridWorldState(val dimension: Dimensions, val agentLocation: Pair, val goalLocation: Pair, val obstacles: ArrayList<Pair>)
    : State<GridWorldState>() {

    override fun hashCode(): Int {
        return agentLocation.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is GridWorldState) {
            if (other.agentLocation.x == agentLocation.x && agentLocation.y == other.agentLocation.y) {
                return true
            }
        }
        return false
    }

    override fun toString(): String {
        return "Agent@ : $agentLocation Goal@: $goalLocation heuristic@: ${heuristic()}"
    }

    override fun isSafe(): Boolean {
        return isGoal()
    }

    override fun visualize(): Unit {
        (0..dimension.height - 1).forEach { y ->
            (0..dimension.width - 1).forEach { x ->
                if (x == agentLocation.x && y == agentLocation.y) {
                    print('@')
                } else if (x == goalLocation.x && y == goalLocation.y) {
                    print('*')
                } else if (obstacles.contains(Pair(x, y))) {
                    print('#')
                } else {
                    print('_')
                }
            }
            println()
        }
        println()
    }

    override fun transition(action: Action): State<GridWorldState> {
        val candidateState = GridWorldState(dimension, Pair(agentLocation.x, agentLocation.y), goalLocation, obstacles)
        if (action == Action.NORTH) {
            candidateState.agentLocation.y -= 1
            if (candidateState.validState()) {
                return candidateState
            }
        } else if (action == Action.EAST) {
            candidateState.agentLocation.x += 1
            if (candidateState.validState()) {
                return candidateState
            }
        } else if (action == Action.SOUTH) {
            candidateState.agentLocation.y += 1
            if (candidateState.validState()) {
                return candidateState
            }
        } else if (action == Action.WEST) {
            candidateState.agentLocation.x -= 1
            if (candidateState.validState()) {
                return candidateState
            }
        } else if (action == Action.WAIT) {
            if (candidateState.validState()) {
                return candidateState
            }
        }
        return invalidState
    }

    override fun heuristic(): Double {
        return ((Math.abs(agentLocation.x - goalLocation.x)) +
                (Math.abs(agentLocation.y - goalLocation.y))).toDouble()
    }

    override fun successors(): ArrayList<Node<GridWorldState>> {
        val successors = ArrayList<Node<GridWorldState>>()
        val possibleActions = ArrayList<Action>(Action.values().asList())
        possibleActions.forEach {
            val candidateSuccessor = transition(it)
            if (invalidState != candidateSuccessor) {
                successors.add(Node(null, candidateSuccessor, it, 0.0, 0.0, false, 0, 0.0))
            }
        }
        return successors
    }

    override fun safe_successors(): ArrayList<SafeNode<GridWorldState>> {
        val successors = ArrayList<SafeNode<GridWorldState>>()
        val possibleActions = ArrayList<Action>(Action.values().asList())
        possibleActions.forEach {
            val candidateSuccessor = transition(it)
            if (invalidState != candidateSuccessor) {
                successors.add(SafeNode(null, candidateSuccessor, it, 0.0, 0.0, false, 0, 0.0, isSafe()))
            }
        }
        return successors
    }

    override fun isGoal(): Boolean {
        if (this != invalidState) {
            if (agentLocation.x == goalLocation.x && agentLocation.y == goalLocation.y) {
                return true
            }
        }
        return false
    }

    override fun validState(): Boolean {
        if (agentLocation.x >= 0 && agentLocation.x < dimension.width) {
            if (agentLocation.y >= 0 && agentLocation.y < dimension.height) {
                if (!obstacles.contains(Pair(agentLocation.x, agentLocation.y))) {
                    return true
                }
            }
        }
        return false
    }
}


