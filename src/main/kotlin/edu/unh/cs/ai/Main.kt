package edu.unh.cs.ai
import java.io.File
import java.util.*
import kotlin.system.exitProcess

/**
 * Main function to start the program
 * Created by willi on 12/16/2016.
 */

val random = Random()

fun main(args: Array<String>) {
    println("Safe Real-Time Search")
    println("arg1: -g [GRIDWORLD] | -v [VEHICLE]")
    println("arg2: -a [ASTAR] | -l [LSSLRTASTAR] | -T [RUN_TESTS]")
    println("arg3: [ITERATIONS] | -s [SAFETY_FLAG]")
    args.forEachIndexed { i, s -> println("\t[$i] $s") }
    val startState : State<GridWorldState>
    if (args[0] == "-g") {
        startState = readGridWorldDomain(Scanner(System.`in`))
    } else {
        println("unsupported function")
        exitProcess(-1)
    }
    println("Given problem: ")
    startState.visualize()
    if (args.size == 2) {
        if (args[1] == "-a") {
            runAStar(startState)
        } else if (args[1] == "-l") {
            runLssLrtaStar(startState, 10, false)
        } else if (args[1] == "-T") {
            println("Running tests....")
            println("Printing start state \n\t$startState...")
            runTests(Node(null, startState, Action.START, 0.0, 0.0, false, 0, startState.heuristic()))
        } else {
            print(args[1])
            println("not recognized/implemented")
        }
    } else if (args.size == 3) {
        if (args[1] == "-l" && args[2] == "-s") {
            /** TODO: run safe search*/
            runLssLrtaStar(startState, 10, true)
        } else if (args[1] == "-l") {
            runLssLrtaStar(startState, args[2].toInt(), false)
        }
    } else if (args.size == 4) {
        runLssLrtaStar(startState, args[2].toInt(), args[3].toBoolean())
    } else {
        println("unsupported function")
    }
}

