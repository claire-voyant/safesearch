package edu.unh.cs.ai

import java.util.*

/**
 * input and output functions
 * Created by doylew on 1/9/17.
 */


fun readGridWorldDomain(input: Scanner): State<GridWorldState> {
    val width = input.nextInt()
    val height = input.nextInt()
    var row = 0
    var col = 0
    var agentX = 0
    var agentY = 0
    var goalX = 0
    var goalY = 0
    val obstacles = ArrayList<Pair>()
    input.nextLine()
    while (input.hasNextLine()) {
        val nextLine = input.nextLine()
        nextLine.forEach {
            if (it == '@') {
                agentX = col
                agentY = row
            } else if (it == '*') {
                goalX = col
                goalY = row
            } else if (it == '#') {
                val newObstacle = Pair(col, row)
                obstacles.add(newObstacle)
            } else {
                if (it != '_') {
                    throw InputMismatchException("corrupted input file")
                }
            }
            ++col
        }
        ++row
        col = 0
    }
    return GridWorldState(Dimensions(width, height), Pair(agentX, agentY), Pair(goalX, goalY), obstacles)
}

fun readVehicleDomain(input: Scanner): State<VehicleState> {
    val width = input.nextInt()
    val height = input.nextInt()
    var row = 0
    var col = 0
    var agentX = 0
    var agentY = 0
    var goalX = 0
    var goalY = 0
    val obstacles = ArrayList<Pair>()
    val bunkers = ArrayList<Pair>()
    input.nextLine()
    while (input.hasNextLine()) {
        val nextLine = input.nextLine()
        nextLine.forEach {
            if (it == '@') {
                agentX = col
                agentY = row
            } else if (it == '*') {
                goalX = col
                goalY = row
            } else if (it == '#') {
                val newObstacle = Pair(col, row)
                obstacles.add(newObstacle)
            } else if (it == '$') {
                val newBunker = Pair(col, row)
                bunkers.add(newBunker)
            } else {
                if (it != '_') {
                    throw InputMismatchException("corrupted input file")
                }
            }
            ++col
        }
        ++row
        col = 0
    }
    val velocities = ArrayList<Pair>(1000)
    (0..999).forEach {
        velocities.add(
                if (random.nextBoolean()) Pair(random.nextInt(1) + 1, 0)
                else Pair(0, random.nextInt(1) + 1)
        )
    }
    return VehicleState(Dimensions(width, height), Pair(agentX, agentY), Pair(goalX, goalY), obstacles, bunkers, velocities)
}

fun initializeDummyVehicle(): State<VehicleState> {
    return VehicleState(Dimensions(-1, -1), Pair(-1, -1), Pair(-1, -1), ArrayList<Pair>(), ArrayList<Pair>(), ArrayList<Pair>())
}

fun initializeDummyGridWorld(): State<GridWorldState> {
    return GridWorldState(Dimensions(-1, -1), Pair(-1, -1), Pair(-1, -1), ArrayList<Pair>())
}