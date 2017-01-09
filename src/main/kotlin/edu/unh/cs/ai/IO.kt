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
            }
            ++col
        }
        ++row
        col = 0
    }
    val obstacleVels = ArrayList<Pair>()
    obstacles.forEach {
        val xVel = random.nextInt(1) + 1
        val yVel = random.nextInt(1) + 1
        val coin = random.nextBoolean()
        obstacleVels.add(Pair(if (coin) {
            xVel
        } else {
            0
        }, if (!coin) {
            yVel
        } else {
            0
        }))
    }
    return GridWorldState(Dimensions(width, height), Pair(agentX, agentY), Pair(goalX, goalY), obstacles)
}