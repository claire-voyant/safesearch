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
    println("arg1: -g [GRIDWORLD]")
    println("arg2: -a [ASTAR] | -l [LSSLRTASTAR] | -T [RUN_TESTS]")
    println("arg3: [ITERATIONS] | -s [SAFETY_FLAG]")
    args.forEachIndexed { i, s -> println("\t[$i] $s") }
    val startState : State<GridWorldState>
    if (args[0] == "-g") {
        startState = readGridWorldDomain(Scanner(File("./input/vehicle/vehicle0.v")))
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
            runLssLrtaStar(startState, 10)
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
        } else if (args[1] == "-l") {
            runLssLrtaStar(startState, args[2].toInt())
        }
    } else {
        println("unsupported function")
    }
}

fun showActions(actions: ArrayList<Action>): Unit {
    actions.forEach {
        println("\t $it")
    }
}