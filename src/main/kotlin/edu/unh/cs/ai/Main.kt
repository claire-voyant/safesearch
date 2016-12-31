/**
 * Main function to start the program
 * Created by willi on 12/16/2016.
 */

package edu.unh.cs.ai

import java.io.File
import java.util.*

fun main(args: Array<String>) {
    println("Safe Real-Time Search")
    println("arg1: -a [ASTAR] | -l [LSSLRTASTAR]")
    println("arg2: -s [SAFETY_FLAG]")
    args.forEachIndexed { i, s -> println("\t[$i] $s") }
    val startState = readDomain(Scanner(File("./input/vehicle/vehicle0.v")))
    val startNode = Node(null, startState, Action.START, 0.0, 0.0+heuristic(startState))
    print(startState)
    if (args.size == 1) {
        if (args[0] == "-a") {
            /** TODO:: run a* */
        } else if (args[0] == "-l") {
            /** TODO:: run lssrta* */
        } else {
            print(args[0])
            println(" not recognized/implemented")
        }
    } else if (args.size == 2) {
        if (args[0] == "-l" && args[1] == "-s") {
            /** TODO: run safe search*/
        }
    } else {
        println("unsupported function")
    }

}
