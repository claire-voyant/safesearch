package edu.unh.cs.ai

/**
 * tests for safesearch
 * Created by willi on 1/2/2017.
 */

fun validStateTest(node: Node) : Boolean {
    println("validStateTest")
    visualize(node.state)
    var success = true
    if(!validState(node.state)) {
       success = false
    }
    val successors = successors(node.state)
    successors.forEach {
        println("\tVisualzing successors...")
        if(!validState(it.state)) {
            success = false
        }
        visualize(it.state)
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
