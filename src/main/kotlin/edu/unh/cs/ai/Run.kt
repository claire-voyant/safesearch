package edu.unh.cs.ai

import kotlin.system.measureTimeMillis

/**
 * run algorithms from here
 * Created by willi on 1/5/2017.
 */

fun <T> runLssLrtaStar(start: State<T>, iterations: Int, safetyFlag: Boolean) {
    if (safetyFlag) println("Running Safe LssLrtaStar") else println("Running LssLrtaStar")
    val runner = LssLrtaStarRunner(start)
    var actionList: List<ActionBundle>
    var timeTaken: Long = 0
    val singleStepLookahead = true
    val actions: MutableList<Action> = arrayListOf()
    runner.maximumIterations = iterations

    var currentState = start
    while (!currentState.isGoal()) {
        timeTaken = measureTimeMillis {
            actionList = runner.selectAction(currentState)
            if (actionList.size > 1 && singleStepLookahead) {
                actionList = listOf(actionList.first()) // Trim the action list to one item
            }
            actionList.forEach {
                currentState = currentState.transition(it.action)
                actions.add(it.action)
            }
        }
//        println("Agent return actions: |${actionList.size}| to state $currentState")
    }
    val pathLength = actions.size
    println("$pathLength Actions taken:")
    actions.forEach(::println)
    println("Time taken: $timeTaken ms")
}

fun <T> runAStar(start: State<T>) {
    println("Running Astar")
    val runner = LssLrtaStarRunner(start)
    val singleStepLookahead = false
    var timeTaken: Long = 0
    var actionList: List<ActionBundle>
    val actions: MutableList<Action> = arrayListOf()
    runner.maximumIterations = kotlin.Int.MAX_VALUE

    var currentState = start
    while (!currentState.isGoal()) {
        timeTaken = measureTimeMillis {
            actionList = runner.selectAction(currentState)
            if (actionList.size > 1 && singleStepLookahead) {
                actionList = listOf(actionList.first()) // Trim the action list to one item
            }
            actionList.forEach {
                currentState = currentState.transition(it.action)
                actions.add(it.action)

            }
        }
//        println("Agent return actions: |${actionList.size}| to state $currentState")
    }
    val pathLength = actions.size
    println("$pathLength Actions taken:")
    actions.forEach(::println)
    println("Time taken: $timeTaken ms")
}

