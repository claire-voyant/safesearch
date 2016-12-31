package edu.unh.cs.ai

import java.util.*

/**
 * Astar implementation
 * Created by doylew on 12/16/16.
 */

data class Node(val parent: Node?, val state: State, val g: Double, val f: Double)

fun astar(start: Node) {

    val queue = PriorityQueue<Node>()

    queue.add(start)

    while(queue.isNotEmpty()) {


        val topOfOpen = queue.poll()

    }



}
