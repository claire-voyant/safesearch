package edu.unh.cs.ai

import java.util.*

/**
 * Astar implementation
 * Created by doylew on 12/16/16.
 */
data class Edge(val node: Node, val action: Action)

data class Node(var parent: Node?, val state: State, var action: Action, var g: Double,
                var f: Double, var open: Boolean, var iteration: Int, var heuristic: Double) : Comparable<Node>, Indexable {
    override fun compareTo(other: Node): Int {
        if (parent == other.parent && state == other.state && action == other.action && g == other.g && f == other.f) {
            return 0
        } else if (parent == other.parent && state == other.state && action == other.action && f < other.f) {
            return -1
        } else {
            return 1
        }
    }

    override var index: Int = -1

    val predecessors: MutableList<Edge> = arrayListOf()
    override fun toString(): String {
        return "Node: [State: $state h: $heuristic, g: $g, iteration: $iteration," +
                " parent: ${parent?.state}, open: $open ]"
    }
}


private val fComparator = Comparator<Node> { lhs, rhs ->
    when {
        lhs.f < rhs.f -> -1
        lhs.f > rhs.f -> 1
        lhs.g > rhs.g -> -1
        lhs.g < rhs.g -> 1
        else -> 0
    }
}

var nodesGenerated = 0
var nodesExpanded = 0
var iterationCounter = 0
val nodes = HashMap<State, Node>(1000000)
val openList = AdvancedPriorityQueue<Node>(1000000, fComparator)


fun initializeAStar(): Unit {
    iterationCounter++
    openList.clear()
}

private val maximumIterations = 10

fun aStar(start: Node): ArrayList<Action> {
    initializeAStar()

    val startState = start.state
    nodesGenerated++
    openList.add(start)
    nodes[startState] = start
    println("Beginning A* from $startState")

    while (openList.isNotEmpty()) {
        val currentNode = openList.pop() ?: throw Exception("Goal is not reachable. Open list is empty")
        println("visualizing currentNode")
        visualize(currentNode.state)
        expandNode(currentNode)

        if (isGoal(currentNode.state)) {
            return computeActions(currentNode, startState)
        }
    }
    return ArrayList<Action>()
    /** empty openList means failure*/
}

fun computeActions(node: Node, startState: State): ArrayList<Action> {
    var currentNode: Node? = node
    val actions = ArrayList<Action>()
    while (startState != currentNode!!.state) {
        actions.add(currentNode.action)
        currentNode = currentNode.parent
    }
    actions.reverse()
    return actions
}

fun expandNode(sourceNode: Node) {
    nodesExpanded++
    val currentGValue = sourceNode.g
    successors(sourceNode.state).forEach { successor ->
        val successorState = sourceNode.state
        println("Expanding $successorState")
        val successorNode = getNode(sourceNode, successor)
        successorNode.predecessors.add(Edge(node = sourceNode, action = successor.action))
        if (successorNode.iteration != iterationCounter) {
            successorNode.apply {
                iteration = iterationCounter
                predecessors.clear()
                g = kotlin.Double.MAX_VALUE
                open = false
            }
        }
        if (successorState != sourceNode.parent?.state) {
            val successorGValueFromCurrent = currentGValue + successor.g
            if (successorNode.g > successorGValueFromCurrent) {
                // here we generate a state. We store it's g value and remember how to get here via the treePointers
                successorNode.apply {
                    g = successorGValueFromCurrent
                    parent = sourceNode
                    action = successor.action
                }

                println("Expanding from $sourceNode --> $successorState :: open list size: ${openList.size}")
                println("Adding it to to cost table with value ${successorNode.g}")

                if (!successorNode.open) {
                    addToOpenList(successorNode) // Fresh node not on the open yet
                } else {
                    openList.update(successorNode)
                }
            } else {
                println(
                        "Did not add, because it's cost is ${successorNode.g} compared to cost of predecessor ( ${sourceNode.g}"
                )
            }
        }
    }
}

private fun getNode(parent: Node, successor: Node): Node {
    val successorState = successor.state
    val tempSuccessorNode = nodes[successorState]

    return if (tempSuccessorNode == null) {
        nodesGenerated++
        val undiscoveredNode = Node(
                state = successorState,
                heuristic = heuristic(successorState),
                action = successor.action,
                parent = parent,
                g = kotlin.Double.MAX_VALUE,
                iteration = iterationCounter,
                open = false,
                f = kotlin.Double.MAX_VALUE)

        nodes[successorState] = undiscoveredNode
        undiscoveredNode
    } else {
        tempSuccessorNode
    }
}

private fun popOpenList(): Node {
    val node = openList.pop() ?: throw Exception("Goal not reachable. Open list is empty.")
    node.open = false
    return node
}

private fun addToOpenList(node: Node) {
    openList.add(node)
    node.open = true
}

/** Old code taken out during testing*/
//if (it.state != sourceNode.state) {
//    println("visualizing successor")
//    visualize(it.state)
//
//    val newCost = 1 + currentNode.g
//    val successorNode = Node(currentNode, it.state, it.action, newCost, newCost + heuristic(it.state))
//
//    if (!nodes.contains(it.state)) {
//        /** we've not seen the node before add it*/
//        nodes[successorNode.state] = successorNode
//        openList.add(successorNode)
//    } else {
//        /** we've seen it before update it*/
//        /** retrieve from the nodes array and update the pointer*/
//        val seenSuccessorNode = nodes[successorNode.state]
//        if (successorNode.g > newCost) {
//            seenSuccessorNode?.parent = currentNode
//            seenSuccessorNode?.action = it.action
//            successorNode.g = newCost
//            successorNode.f = newCost + heuristic(it.state)
//            openList.remove(successorNode)
//            openList.add(seenSuccessorNode)
//        }
//    }
//}