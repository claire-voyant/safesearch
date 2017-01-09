package edu.unh.cs.ai

/**
 * tests for safesearch
 * Created by willi on 1/2/2017.
 */

fun validStateTest(node: Node<GridWorldState>) : Boolean {
    println("validStateTest and successorTest")
    node.state.visualize()
    var success = true
    if(!node.state.validState()) {
       success = false
    }
    val successors = node.state.successors()
    successors.forEach {
        println("\tVisualzing successors...")
        println("\t${it.action}")
        println(it)
        if(!it.state.validState()) {
            success = false
        }
        it.state.visualize()
//        val nextSuccessors = it.state.successors()
//        nextSuccessors.forEach {
//            println("\t\tVisualizing successor of successors...")
//            println("\t\t${it.action}")
//            println(it)
//            if(!it.state.validState()) {
//                success = false
//            }
//            it.state.visualize()
//        }
    }
    return success
}

fun runTests(node: Node<GridWorldState>) : Unit {
    var success = true
    if(!validStateTest(node)) {success = false}
    if(success) {
        println("Tests successful!")
    } else {
        println("Unsuccessful tests see output")
    }
}
