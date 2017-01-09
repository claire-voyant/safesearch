package edu.unh.cs.ai

import java.util.*

/**
 * vehicle find the goal
 * while avoiding obstacles
 * Created by doylew on 12/30/16
 */

private val invalidState = VehicleState(Dimensions(-1, -1), Pair(-1, -1), Pair(-1, -1), ArrayList<Pair>(), ArrayList<Pair>())
private val obstacleVelocities = ArrayList<Pair>(1000).apply {
    forEach {
        if (random.nextBoolean()) {
            it.x = random.nextInt() % 1 + 1
            it.y = 0
        } else {
            it.x = 0
            it.y = random.nextInt() % 1 + 1
        }
    }
}

class VehicleState(val dimensions: Dimensions, val agentLocation: Pair, val goalLocation: Pair,
                   val obstacles: ArrayList<Pair>, val bunkers: ArrayList<Pair>) : State<VehicleState>() {

    override fun hashCode(): Int {
        return agentLocation.hashCode() xor obstacles.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is VehicleState) {
            if (other.agentLocation.x == agentLocation.x && agentLocation.y == other.agentLocation.y) {
                if (other.obstacles.containsAll(obstacles)) {
                    return true
                }
            }
        }
        return false
    }

    override fun toString(): String {
        return "Agent@ : $agentLocation Goal@: $goalLocation heuristic@: ${heuristic()}"
    }

    override fun heuristic(): Double {
        return ((Math.abs(agentLocation.x - goalLocation.x)) +
                (Math.abs(agentLocation.y - goalLocation.y))).toDouble()
    }

    override fun isGoal(): Boolean {
        if (agentLocation.x == goalLocation.x && agentLocation.y == goalLocation.y) {
            return true
        }
        return false
    }

    override fun successors(): ArrayList<Node<VehicleState>> {
        val successors = ArrayList<Node<VehicleState>>()
        val possibleActions = ArrayList<Action>(Action.values().asList())
        possibleActions.forEach {
            val candidateSuccessor = transition(it)
            if (invalidState != candidateSuccessor) {
                successors.add(Node(null, candidateSuccessor, it, 0.0, 0.0, false, 0, 0.0))
            }
        }
        return successors
    }

    fun moveObstacles(obstacles: ArrayList<Pair>): ArrayList<Pair> {
        val newObstacles = ArrayList<Pair>()
        obstacles.forEachIndexed { i, pair ->
            val oldObstaclePair = Pair(pair.x, pair.y)
            val newObstaclePair = Pair(pair.x + obstacleVelocities[i].x, pair.y + obstacleVelocities[i].y)
            if (bunkers.contains(newObstaclePair)) {
                // if the new obstacle location would be a bunker
                // add the old location and bounce the velocities
                newObstacles.add(oldObstaclePair)
                obstacleVelocities[i].x *= -1
                obstacleVelocities[i].y *= -1
            } else {
                newObstacles.add(newObstaclePair)
            }
        }
        return newObstacles
    }

    override fun transition(action: Action): State<VehicleState> {
        val movedObstacles = moveObstacles(obstacles)
        val candidateState = VehicleState(dimensions, Pair(agentLocation.x, agentLocation.y), goalLocation, movedObstacles, bunkers)

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

    override fun validState(): Boolean {
        if (agentLocation.x >= 0 && agentLocation.x < dimensions.width) {
            if (agentLocation.y >= 0 && agentLocation.y < dimensions.height) {
                if (!obstacles.contains(Pair(agentLocation.x, agentLocation.y))) {
                    return true
                }
            }
        }
        return false
    }

    override fun visualize() {
        (0..dimensions.height - 1).forEach { y ->
            (0..dimensions.width - 1).forEach { x ->
                if (x == agentLocation.x && y == agentLocation.y) {
                    print('@')
                } else if (x == goalLocation.x && y == goalLocation.y) {
                    print('*')
                } else if (obstacles.contains(Pair(x, y))) {
                    print('#')
                } else if (bunkers.contains(Pair(x, y))) {
                    print('$')
                } else {
                    print('_')
                }
            }
        }
    }
}
//
//val invalidState = State(-1, -1, -1, -1, -1, -1, ArrayList<Pair>(), ArrayList<Pair>(), ArrayList<Pair>())
//
//enum class Action {
//    NORTH,
//    EAST,
//    SOUTH,
//    WEST,
//    WAIT,
//    START
//}
//
//fun isGoal(state: State): Boolean {
//    return state.agentX == state.goalX && state.agentY == state.goalY
//}
//
//fun moveObstacles(obstacles: ArrayList<Pair>, vels: ArrayList<Pair>, width: Int, height: Int, bunkers: ArrayList<Pair>): ArrayList<Pair> {
//    val newObstacles = ArrayList<Pair>()
//    obstacles.forEachIndexed { i, pair ->
//        var xPos = pair.x
//        var yPos = pair.y
//        if (pair.x + vels[i].x < width && pair.x + vels[i].x >= 0) {
//            xPos = pair.x + vels[i].x
//        }
//        if (pair.y + vels[i].y < height && pair.y + vels[i].y >= 0) {
//            yPos = pair.y + vels[i].y
//        }
//        if (!bunkers.contains(Pair(xPos, yPos))) {
//            newObstacles.add(Pair(xPos, yPos))
//        } else {
//            vels[i] = Pair(vels[i].x * -1, vels[i].y * -1)
//            newObstacles.add(Pair(pair.x, pair.y))
//        }
//    }
//    return newObstacles
//}
//
//fun transition(state: State, action: Action): State {
//    val movedObstacles = moveObstacles(state.obstacles, state.obstacleVels, state.width, state.height, state.bunkers)
//    val candidateState = State(state.width, state.height, state.agentX, state.agentY, state.goalX, state.goalY, movedObstacles, state.obstacleVels, state.bunkers)
//    if (action == Action.NORTH) {
//        candidateState.agentY -= 1
//        if (validState(candidateState)) {
//            return candidateState
//        }
//    } else if (action == Action.EAST) {
//        candidateState.agentX += 1
//        if (validState(candidateState)) {
//            return candidateState
//        }
//    } else if (action == Action.SOUTH) {
//        candidateState.agentY += 1
//        if (validState(candidateState)) {
//            return candidateState
//        }
//    } else if (action == Action.WEST) {
//        candidateState.agentX -= 1
//        if (validState(candidateState)) {
//            return candidateState
//        }
//    } else if (action == Action.WAIT) {
//        if (validState(candidateState)) {
//            return candidateState
//        }
//    }
//    return invalidState
//}
//
//fun validState(state: State): Boolean {
//    if (state.agentX >= 0 && state.agentX < state.width) {
//        if (state.agentY >= 0 && state.agentY < state.height) {
//            var obstacleCheck = true
//            state.obstacles.forEach {
//                if (it.x == state.agentX && it.y == state.agentY) {
//                    obstacleCheck = false
//                }
//            }
//            return obstacleCheck
//        }
//    }
//    return false
//}
//
//fun successors(state: State): ArrayList<Node> {
//    val successors = ArrayList<Node>()
//    val possibleActions = ArrayList<Action>(Action.values().asList())
//    possibleActions.forEach {
//        val candidateSuccessor = transition(state, it)
//        if (invalidState != candidateSuccessor) {
//            successors.add(Node(null, candidateSuccessor, it, 0.0, 0.0, false, 0, 0.0))
//        }
//    }
//    return successors
//}
