package edu.unh.cs.ai

import java.util.*

/**
 * Astar implementation
 * Created by doylew on 12/16/16.
 */

data class Node(var parent: Node?, val state: State, var action: Action, var g: Double, var f: Double)

var nodesGenerated = 0
var nodesExpanded  = 0

fun astar(start: Node) : ArrayList<Action> {

    val nodes = ArrayList<Node>()
    val queue = PriorityQueue<Node>()
    val startState = start.state
    nodesGenerated++
    queue.add(start)
    nodes.add(start)

    while(queue.isNotEmpty()) {
        nodesExpanded++
        val currentNode = queue.poll()

        if(isGoal(currentNode.state)){
            return computeActions(currentNode, startState)
        }

        successors(currentNode.state).forEach {
            if(it.state != currentNode.state) {

                val newCost = 1 + currentNode.g
                val successorNode = Node(currentNode, it.state, it.action, newCost, newCost + heuristic(it.state))

                if(!nodes.contains(it)) {
                    /** we've not seen the node before add it*/
                    nodes.add(successorNode)
                    queue.add(successorNode)
                } else {
                    /** we've seen it before update it*/
                    /** retrieve from the nodes array and update the pointer*/
                    val seenSuccessorNode = nodes.get(nodes.indexOf(successorNode))
                    if(successorNode.g > newCost) {
                        seenSuccessorNode.parent = currentNode
                        seenSuccessorNode.action = it.action
                        successorNode.g = newCost
                        successorNode.f = newCost + heuristic(it.state)
                        queue.remove(successorNode)
                        queue.add(seenSuccessorNode)
                    }
                }
            }
        }
    }
    return ArrayList<Action>()
}

fun computeActions(node: Node, startState: State) : ArrayList<Action> {
    var currentNode : Node? = node
    val actions = ArrayList<Action>()
    while(startState != currentNode!!.state) {
        actions.add(currentNode.action)
        currentNode = currentNode.parent
    }
    actions.reverse()
    return actions
}
