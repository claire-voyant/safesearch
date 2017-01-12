package edu.unh.cs.ai

import java.util.*
import kotlin.system.exitProcess

/**
 * Main function to start the program
 * Created by willi on 12/16/2016.
 */

val random = Random()


fun main(args: Array<String>) {
    random.setSeed(0)
    println("Safe Real-Time Search")
    println("arg1: -g [GRIDWORLD] | -v [VEHICLE]")
    println("arg2: -a [ASTAR] | -l [LSSLRTASTAR] | -T [RUN_TESTS]")
    println("arg3: [ITERATIONS] | -s [SAFETY_FLAG]")
    println("provide problem in standard input < [PROBLEM-FILE]")
    args.forEachIndexed { i, s -> println("\t[$i] $s") }
    val inputFile = Scanner(System.`in`)
    var startGridWorld: State<GridWorldState> = initializeDummyGridWorld()
    var startVehicle: State<VehicleState> = initializeDummyVehicle()
    if (args[0] == "-g") {
        startGridWorld = readGridWorldDomain(inputFile)
        println("Running GridWorld...")
    } else if (args[0] == "-v") {
        startVehicle = readVehicleDomain(inputFile)
        println("Running Vehicle...")
    } else {
        println("unsupported function, exiting...")
        exitProcess(-1)
    }
    println("Given problem: ")
    if (args[0] == "-g") startGridWorld.visualize() else startVehicle.visualize()
    if (args.size == 2) {
        if (args[1] == "-a") {
            runAStar(if (args[0] == "-g") startGridWorld else startVehicle)
        } else if (args[1] == "-l") {
            runLssLrtaStar(if (args[0] == "-g") startGridWorld else startVehicle, 10)
        } else if (args[1] == "-T") {
            if (args[0] == "-v") {
                println("no tests for vehicle...exiting...")
                exitProcess(0)
            }
            println("Running tests....")
            println("Printing start state \n\t$startGridWorld...")
            runTests(Node(null, startGridWorld, Action.START, 0.0, 0.0, false, 0, startGridWorld.heuristic()))
        } else {
            print(args[1])
            println("unsupported function, exiting...")
            exitProcess(-1)
        }
    } else if (args.size == 3) {
        if (args[1] == "-l" && args[2] == "-s") {
            runSZero(if (args[0] == "-g") startGridWorld else startVehicle, 10)
        } else if (args[1] == "-l") {
            runLssLrtaStar(if (args[0] == "-g") startGridWorld else startVehicle, args[2].toInt())
        }
    } else if (args.size == 4) {
        runLssLrtaStar(if (args[0] == "-g") startGridWorld else startVehicle, args[2].toInt())
    } else {
        println("unsupported function, exiting...")
        exitProcess(-1)
    }
}

