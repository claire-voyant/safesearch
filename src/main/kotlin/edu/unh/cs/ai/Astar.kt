package edu.unh.cs.ai

import java.util.*
import kotlin.system.measureTimeMillis

/**
 * Astar implementation
 * Created by doylew on 12/16/16.
 */

data class ActionBundle(val action: Action, val cost: Double)

data class Node<T>(var parent: Node<T>?, val state: State<T>, var action: Action, var g: Double,
                   var f: Double, var open: Boolean, var iteration: Int, var heuristic: Double) : Indexable {
    override var index: Int = -1
    val predecessors: MutableList<LssLrtaStarRunner.Edge<T>> = arrayListOf()
    override fun toString(): String {
        return "Node: [State: $state h: $heuristic, g: $g, iteration: $iteration," +
                " parent: ${parent?.state}, open: $open ]"
    }
}

data class LssLrtaStarRunner<T>(val start: State<T>) {
    data class Edge<T>(val node: Node<T>, val action: Action)

    private val fComparator = Comparator<Node<T>> { lhs, rhs ->
        when {
            lhs.f < rhs.f -> -1
            lhs.f > rhs.f -> 1
            lhs.g > rhs.g -> -1
            lhs.g < rhs.g -> 1
            else -> 0
        }
    }

    private val heuristicComparator = Comparator<Node<T>> { lhs, rhs ->
        when {
            lhs.heuristic < rhs.heuristic -> -1
            lhs.heuristic > rhs.heuristic -> 1
            else -> 0
        }
    }

    var nodesGenerated = 0
    var nodesExpanded = 0
    var iterationCounter = 0
    private var rootState: State<T>? = null
    private var aStarPopCounter = 0
    private var dijkstraPopCounter = 0
    private var aStarTimer = 0L
        get
    private var dijkstraTimer = 0L
        get
    val nodes = HashMap<State<T>, Node<T>>(1000000)
    val openList = AdvancedPriorityQueue<Node<T>>(1000000, fComparator)


    fun initializeAStar(): Unit {
        iterationCounter++
        openList.clear()
    }

    var maximumIterations = 10

    fun reachedTermination(): Boolean {
        if (iterationCounter == maximumIterations) {
            iterationCounter = 0
            return true
        }
        return false
    }

    fun aStar(start: State<T>): Node<T> {
        initializeAStar()
        nodesGenerated++

        val node = Node(null, start, Action.START, 0.0, 0.0, false, iterationCounter, start.heuristic())
        val startState = start
        nodes[startState] = node
        var currentNode = node
        addToOpenList(node)
        measureInt({ nodesExpanded }) {
            while (!reachedTermination() && !currentNode.state.isGoal()) {
                aStarPopCounter++
                currentNode = popOpenList()
                expandNode(currentNode)
            }
        }

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

//    fun computeActions(node: Node<T>, startState: State<T>): ArrayList<Action> {
//        var currentNode: Node<T>? = node
//        val actions = ArrayList<Action>()
//        while (startState != currentNode!!.state) {
//            actions.add(currentNode.action)
//            currentNode = currentNode.parent
//        }
//        actions.reverse()
//        return actions
//    }

    fun expandNode(sourceNode: Node<T>) {
        nodesExpanded++
        val currentGValue = sourceNode.g
        sourceNode.state.successors().forEach { successor: Node<T> ->
            val successorState = successor.state
            val successorNode = getNode(sourceNode, successor)

            if(!successorNode.predecessors.contains(Edge(node = sourceNode, action = successor.action))){
                successorNode.predecessors.add(Edge(node = sourceNode, action = successor.action))
            }

            if (successorNode.iteration != iterationCounter) {
                successorNode.apply {
                    iteration = iterationCounter
                    predecessors.clear()
                    g = kotlin.Double.MAX_VALUE
                    open = false
                }
            }

            if (successorState != sourceNode.parent?.state) {
                val successorGValueFromCurrent = currentGValue + successor.g + 1
                if (successorNode.g > successorGValueFromCurrent) {
                    successorNode.apply {
                        g = successorGValueFromCurrent
                        parent = sourceNode
                        action = successor.action
                        f = g + heuristic
                    }

                    if (!successorNode.open) {
                        addToOpenList(successorNode) // Fresh node not on the open yet
                    } else {
                        openList.update(successorNode)
                    }
                }
            }
        }
    }

    private fun getNode(parent: Node<T>, successor: Node<T>): Node<T> {
        val successorState = successor.state
        val tempSuccessorNode = nodes[successorState]

        return if (tempSuccessorNode == null) {
            nodesGenerated++
            val undiscoveredNode = Node(
                    state = successorState,
                    heuristic = successorState.heuristic(),
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

    private fun popOpenList(): Node<T> {
        val node = openList.pop() ?: throw Exception("Goal not reachable. Open list is empty.")
        node.open = false
        return node
    }

    private fun addToOpenList(node: Node<T>) {
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

    private fun updateHeuristics() {
        iterationCounter++
        openList.reorder(heuristicComparator)
        while (!reachedTermination() && openList.isNotEmpty()) {
            val node = popOpenList()
            node.iteration = iterationCounter
            val currentHeuristicValue = node.heuristic
            for ((predecessorNode) in node.predecessors) {
                if (predecessorNode.iteration == iterationCounter && !predecessorNode.open) {
                    continue
                }
                val predecessorHeuristicValue = predecessorNode.heuristic
                if (!predecessorNode.open) {
                    predecessorNode.heuristic = currentHeuristicValue + 1
                    assert(predecessorNode.iteration == iterationCounter - 1)
                    predecessorNode.iteration = iterationCounter
                    addToOpenList(predecessorNode)
                } else if (predecessorHeuristicValue > currentHeuristicValue + 1) {
                    predecessorNode.heuristic = currentHeuristicValue + 1
                    openList.update(predecessorNode) // Update priority
                }
            }
        }
    }

    private fun extractPlan(targetNode: Node<T>, sourceState: State<T>): List<ActionBundle> {
        val actions = ArrayList<ActionBundle>(1000)
        var currentNode = targetNode

        if (targetNode.state == sourceState) {
            return emptyList()
        }
        do {
            actions.add(ActionBundle(currentNode.action, currentNode.g))
            currentNode = currentNode?.parent!!
        } while (currentNode.state != sourceState)

        return actions.reversed()
    }

    fun selectAction(state: State<T>): List<ActionBundle> {
        if (rootState == null) {
            rootState = state
        } else if (state != rootState) {
        }
        if (state.isGoal()) {
            return emptyList()
        }
        // Learning phase
        if (openList.isNotEmpty()) {
            dijkstraTimer += measureTimeMillis { updateHeuristics() }
        }
        // Exploration phase
        var plan: List<ActionBundle>? = null
        aStarTimer += measureTimeMillis {
            val targetNode = aStar(state)
            plan = extractPlan(targetNode, state)
            rootState = targetNode.state
        }
//        reset()
        return plan!!
    }
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