package edu.unh.cs.ai

/**
 * tests for safesearch
 * Created by willi on 1/2/2017.
 */

fun validStateTest(node: Node) : Boolean {
    println("validStateTest and successorTest")
    visualize(node.state)
    var success = true
    if(!validState(node.state)) {
       success = false
    }
    val successors = successors(node.state)
    successors.forEach {
        println("\tVisualzing successors...")
        println("\t${it.action}")
        println(it)
        if(!validState(it.state)) {
            success = false
        }
        visualize(it.state)
        val nextSuccessors = successors(it.state)
        nextSuccessors.forEach {
            println("\t\tVisualizing successor of successors...")
            println("\t\t${it.action}")
            println(it)
            if(!validState(it.state)) {
                success = false
            }
            visualize(it.state)
        }
    }
    return success
}


fun runTests(node: Node) : Unit {
    var success = true
    if(!validStateTest(node)) {success = false}
    if(success) {
        println("Tests successful!")
    } else {
        println("Unsuccessful tests see output")
    }
}
