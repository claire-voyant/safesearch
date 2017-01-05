package edu.unh.cs.ai

import java.util.*

/**
 * vehicle domain represents the obstacles
 * and the agent position of a gridworld
 * Created by doylew on 12/30/16.
 */

val invalidState = State(-1, -1, -1, -1, -1, -1, ArrayList<Pair>(), ArrayList<Pair>(), ArrayList<Pair>())

enum class Action {
    NORTH,
    EAST,
    SOUTH,
    WEST,
    WAIT,
    START
}

fun isGoal(state: State): Boolean {
    return state.agentX == state.goalX && state.agentY == state.goalY
}

fun moveObstacles(obstacles: ArrayList<Pair>, vels: ArrayList<Pair>, width: Int, height: Int, bunkers: ArrayList<Pair>): ArrayList<Pair> {
    val newObstacles = ArrayList<Pair>()
    obstacles.forEachIndexed { i, pair ->
        var xPos = pair.x
        var yPos = pair.y
        if (pair.x + vels[i].x < width && pair.x + vels[i].x >= 0) {
            xPos = pair.x + vels[i].x
        }
        if (pair.y + vels[i].y < height && pair.y + vels[i].y >= 0) {
            yPos = pair.y + vels[i].y
        }
        if (!bunkers.contains(Pair(xPos, yPos))) {
            newObstacles.add(Pair(xPos, yPos))
        } else {
            vels[i] = Pair(vels[i].x * -1, vels[i].y * -1)
            newObstacles.add(Pair(pair.x, pair.y))
        }
    }
    return newObstacles
}

fun transition(state: State, action: Action): State {
    val movedObstacles = moveObstacles(state.obstacles, state.obstacleVels, state.width, state.height, state.bunkers)
    val candidateState = State(state.width, state.height, state.agentX, state.agentY, state.goalX, state.goalY, movedObstacles, state.obstacleVels, state.bunkers)
    if (action == Action.NORTH) {
        candidateState.agentY -= 1
        if (validState(candidateState)) {
            return candidateState
        }
    } else if (action == Action.EAST) {
        candidateState.agentX += 1
        if (validState(candidateState)) {
            return candidateState
        }
    } else if (action == Action.SOUTH) {
        candidateState.agentY += 1
        if (validState(candidateState)) {
            return candidateState
        }
    } else if (action == Action.WEST) {
        candidateState.agentX -= 1
        if (validState(candidateState)) {
            return candidateState
        }
    } else if (action == Action.WAIT) {
        if (validState(candidateState)) {
            return candidateState
        }
    }
    return invalidState
}

fun validState(state: State): Boolean {
    if (state.agentX >= 0 && state.agentX < state.width) {
        if (state.agentY >= 0 && state.agentY < state.height) {
            var obstacleCheck = true
            state.obstacles.forEach {
                if (it.x == state.agentX && it.y == state.agentY) {
                    obstacleCheck = false
                }
            }
            return obstacleCheck
        }
    }
    return false
}

fun successors(state: State): ArrayList<Node> {
    val successors = ArrayList<Node>()
    val possibleActions = ArrayList<Action>(Action.values().asList())
    possibleActions.forEach {
        val candidateSuccessor = transition(state, it)
        if (invalidState != candidateSuccessor) {
            successors.add(Node(null, candidateSuccessor, it, 0.0, 0.0, false, 0, 0.0))
        }
    }
    return successors
}

fun visualize(state: State): Unit {
    (0..state.height - 1).forEach { y ->
        (0..state.width - 1).forEach { x ->
            if (x == state.agentX && y == state.agentY) {
                print('@')
            } else if (x == state.goalX && y == state.goalY) {
                print('*')
            } else if (state.obstacles.contains(Pair(x, y))) {
                print('#')
            } else if (state.bunkers.contains(Pair(x, y))) {
                print('$')
            } else {
                print('_')
            }
        }
        println()
    }
    println()
}