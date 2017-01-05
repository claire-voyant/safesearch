package edu.unh.cs.ai

import java.util.*
import kotlin.system.measureTimeMillis

/**
 * Astar implementation
 * Created by doylew on 12/16/16.
 */
data class Edge(val node: Node, val action: Action)

data class ActionBundle(val action: Action, val cost: Double)
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

private val heuristicComparator = Comparator<Node> { lhs, rhs ->
    when {
        lhs.heuristic < rhs.heuristic -> -1
        lhs.heuristic > rhs.heuristic -> 1
        else -> 0
    }
}

var nodesGenerated = 0
var nodesExpanded = 0
var iterationCounter = 0
private var rootState: State? = null
private var aStarPopCounter = 0
private var dijkstraPopCounter = 0
private var aStarTimer = 0L
    get
private var dijkstraTimer = 0L
    get
val nodes = HashMap<State, Node>(1000000)
val openList = AdvancedPriorityQueue<Node>(1000000, fComparator)


fun initializeAStar(): Unit {
    iterationCounter++
    openList.clear()
}

var maximumIterations = 10

fun reachedTermination(): Boolean {
    if (iterationCounter == maximumIterations) {
        return true
    }
    return false
}

fun aStar(start: State): Node {
    initializeAStar()
    nodesGenerated++

    val node = Node(null, start, Action.START, 0.0, 0.0, false, iterationCounter, heuristic(start))
    val startState = start
    nodes[startState] = node
    var currentNode = node
    addToOpenList(node)
//    println("Beginning A* from $startState")

    val expandedNodes = measureInt({ nodesExpanded }) {
        while (!reachedTermination() && !isGoal(currentNode.state)) {
            aStarPopCounter++
            currentNode = popOpenList()
            expandNode(currentNode)
        }
    }

    if (node == currentNode && !isGoal(currentNode.state)) {
        //            throw InsufficientTerminationCriterionException("Not enough time to expand even one node")
    } else {
//        println("A* : expanded $expandedNodes nodes")
    }
//    println("Done with AStar at $currentNode")

    return currentNode
}

fun reset() {
    rootState = null
    aStarPopCounter = 0
    dijkstraPopCounter = 0
    aStarTimer = 0L
    dijkstraTimer = 0L
    clearOpenList()
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
        val successorState = successor.state
//        println("Expanding $successorState")
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

//                println("Expanding from $sourceNode --> $successorState :: open list size: ${openList.size}")
//                println("Adding it to to cost table with value ${successorNode.g}")

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

private fun clearOpenList() {
//    println("Clear open list")
    openList.applyAndClear {
        it.open = false
    }
}

inline fun measureInt(property: () -> Int, block: () -> Unit): Int {
    val initialPropertyValue = property()
    block()
    return property() - initialPropertyValue
}

private fun dijkstra() {
//    println("Start: Dijkstra")
    // Invalidate the current heuristic value by incrementing the counter
    iterationCounter++
    // change openList ordering to heuristic only`
    openList.reorder(heuristicComparator)
    // LSS-LRTA addition
    //        openList.toTypedArray().forEach {
    //            it.iteration = iterationCounter
    //        }
    while (!reachedTermination() && openList.isNotEmpty()) {
        // Closed list should be checked
        val node = popOpenList()
        node.iteration = iterationCounter
        val currentHeuristicValue = node.heuristic
        // update heuristic value for each predecessor
        for ((predecessorNode) in node.predecessors) {
            if (predecessorNode.iteration == iterationCounter && !predecessorNode.open) {
                // This node was already learned and closed in the current iteration
                continue
            }
            // Update if the node is outdated
            //                if (predecessorNode.iteration != iterationCounter) {
            //                    predecessorNode.heuristic = Double.POSITIVE_INFINITY
            //                    predecessorNode.iteration = iterationCounter
            //                }
            val predecessorHeuristicValue = predecessorNode.heuristic
            //                logger.debug { "Considering predecessor ${predecessor.node} with heuristic value $predecessorHeuristicValue" }
            //                logger.debug { "Node in closedList: ${predecessor.node in closedList}. Current heuristic: $predecessorHeuristicValue. Proposed new value: ${(currentHeuristicValue + predecessor.actionCost)}" }
            if (!predecessorNode.open) {
                // This node is not open yet, because it was not visited in the current planning iteration
                predecessorNode.heuristic = currentHeuristicValue + 1
                assert(predecessorNode.iteration == iterationCounter - 1)
                predecessorNode.iteration = iterationCounter
                addToOpenList(predecessorNode)
            } else if (predecessorHeuristicValue > currentHeuristicValue + 1) {
                // This node was visited in this learning phase, but the current path is better then the previous
                predecessorNode.heuristic = currentHeuristicValue + 1
                openList.update(predecessorNode) // Update priority
                // Frontier nodes could be also visited TODO
                //                    assert(predecessorNode.iteration == iterationCounter) {
                //                        "Expected iteration stamp $iterationCounter got ${predecessorNode.iteration}"
                //                    }
            }
        }
    }
    // update mode if done
    if (openList.isEmpty()) {
//        println("Done with Dijkstra")
    } else {
//        println("Incomplete learning step. Lists: Open(${openList.size})")
    }
}

private fun extractPlan(targetNode: Node, sourceState: State): List<ActionBundle> {
    val actions = ArrayList<ActionBundle>(1000)
    var currentNode = targetNode

//    println("Extracting plan")

    if (targetNode.state == sourceState) {
        return emptyList()
    }
    // keep on pushing actions to our queue until source state (our root) is reached
    do {
        actions.add(ActionBundle(currentNode.action, currentNode.g))
        currentNode = currentNode?.parent!!
    } while (currentNode.state != sourceState)

//    println({ "Plan extracted" })
    return actions.reversed()

}

fun selectAction(state: State): List<ActionBundle> {
    // Initiate for the first search
    if (rootState == null) {
        rootState = state
    } else if (state != rootState) {
        // The given state should be the last target
//        println("Inconsistent world state. Expected $rootState got $state")
    }
    if (isGoal(state)) {
        // The start state is the goal state
//        println("selectAction: The goal state is already found.")
        return emptyList()
    }
//    println("Root state: $state")
    // Every turn learn then A* until time expires
    // Learning phase
    if (openList.isNotEmpty()) {
        dijkstraTimer += measureTimeMillis { dijkstra() }
    }
    // Exploration phase
    var plan: List<ActionBundle>? = null
    aStarTimer += measureTimeMillis {
        val targetNode = aStar(state)
        plan = extractPlan(targetNode, state)
        rootState = targetNode.state
    }
    return plan!!
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

//while (openList.isNotEmpty()) {
//    val currentNode = openList.pop() ?: throw Exception("Goal is not reachable. Open list is empty")
//    println("visualizing currentNode")
//    visualize(currentNode.state)
//    expandNode(currentNode)
//
//    if (isGoal(currentNode.state)) {
//        return computeActions(currentNode, startState)
//    }
//}
//return ArrayList<Action>()