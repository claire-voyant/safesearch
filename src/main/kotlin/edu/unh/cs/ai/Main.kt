/**
 * Main function to start the program
 * Created by willi on 12/16/2016.
 */

package edu.unh.cs.ai

import java.io.File
import java.util.*

fun main(args: Array<String>) {
    println("Safe Real-Time Search")
    println("arg1: -a [ASTAR] | -l [LSSLRTASTAR] | -T [RUN_TESTS]")
    println("arg2: [ITERATIONS] | -s [SAFETY_FLAG]")
    args.forEachIndexed { i, s -> println("\t[$i] $s") }
    val startState = readDomain(Scanner(File("./input/vehicle/simple.v")))
    println("Given problem: ")
    visualize(startState)
    if (args.size == 1) {
        if (args[0] == "-a") {
            runAStar(startState)
        } else if (args[0] == "-l") {
            runLssLrtaStar(startState, 10)
        } else if (args[0] == "-T") {
            println("Running tests....")
            println("Printing start state \n\t$startState...")
            runTests(Node(null, startState, Action.START, 0.0, 0.0, false, 0, heuristic(startState)))
        } else {
            print(args[0])
            println("not recognized/implemented")
        }
    } else if (args.size == 2) {
        if (args[0] == "-l" && args[1] == "-s") {
            /** TODO: run safe search*/
        } else if (args[0] == "-l") {
           runLssLrtaStar(startState, args[1].toInt())
        }
    } else {
        println("unsupported function")
    }
}

fun showActions(actions: ArrayList<Action>) : Unit {
    actions.forEach {
        println("\t $it")
    }
}