/**
 * Basic grid world implementation
 * Created by willi on 12/16/2016.
 */

package edu.unh.cs.ai

import java.util.*

data class Pair(val x: Int, val  y: Int)
data class Domain(val agentX: Int, val agentY: Int, val goalX: Int, val goalY: Int, val obstacles: ArrayList<Pair>)

fun readDomain(input: Scanner): Domain {
    var row = 0
    var col = 0
    var agentX = 0
    var agentY = 0
    var goalX = 0
    var goalY = 0
    val obstacles = ArrayList<Pair>()
    while(input.hasNextLine()) {
        val nextLine = input.nextLine()
        nextLine.forEach {
            if (it == '@') {
                agentX = col
                agentY = row
            } else if (it == '*') {
                goalX = col
                goalY = row
            } else if (it == '#') {
                val newPair = Pair(col, row)
                obstacles.add(newPair)
            }
            ++col
        }
        ++row
    }
    return Domain(agentX, agentY, goalX, goalY, obstacles)
}


