import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter

data class DFA(
    val q: Int,
    val sigma: Int,
    val q0: Int,
    val delta: List<Map<Int, Int>>,
    val F: Set<Int>,
) {
    private fun accept(curState: Int, input: List<Int>, pointer: Int): Boolean =
        if (pointer == input.size) {
            curState in F
        } else {
            val symbol = input[pointer]
            if (symbol !in delta[curState]) false
            else accept(delta[curState][symbol]!!, input, pointer + 1)
        }

    fun accept(input: List<Int>) = accept(q0, input, 0)
}

fun reachableFrom(dfa: DFA, q: Int, visited: MutableSet<Int>) {
    visited.add(q)
    dfa.delta[q].forEach { (_, to) ->
        if (to !in visited) reachableFrom(dfa, to, visited)
    }
}

fun minimiseDfa(dfa: DFA): DFA {
    val reachableFromStartStates = mutableSetOf<Int>()
    reachableFrom(dfa, dfa.q0, reachableFromStartStates)

    var equivalenceClasses = listOf(
        dfa.F.intersect(reachableFromStartStates),
        reachableFromStartStates - dfa.F
    ).filter { it.isNotEmpty() }

    val getClass = { state: Int ->
        equivalenceClasses.mapIndexedNotNull { index, equivalenceClasses ->
            if (state in equivalenceClasses) index else null
        }.first()
    }
    var somethingSplitted = true
    while (somethingSplitted) {
        somethingSplitted = false
        run tryAgain@{
            for (equivalenceClass in equivalenceClasses) {
                for (char in 0 until dfa.sigma) {
                    val partition = equivalenceClass.groupBy { state ->
                        getClass(dfa.delta[state][char]!!)
                    }.values.map { it.toSet() }

                    if (partition.size > 1) {
                        equivalenceClasses = equivalenceClasses.filter {
                            it != equivalenceClass
                        } + partition
                        somethingSplitted = true
                        return@tryAgain
                    }
                }
            }
        }
    }

    val final = reachableFromStartStates.intersect(dfa.F).map {
        getClass(it)
    }.toSet()

    val delta = List(equivalenceClasses.size) {
        val from = equivalenceClasses[it].first()
        (0 until dfa.sigma).associateWith { symbol ->
            getClass(dfa.delta[from][symbol]!!)
        }
    }

    return DFA(
        equivalenceClasses.size,
        dfa.sigma,
        getClass(dfa.q0),
        delta,
        final,
    )
}

fun main(args: Array<String>) {
    val file = BufferedReader(FileReader("input.txt"))
    val q = file.readLine()!!.toInt()
    val sigma = file.readLine()!!.toInt()

    val q0 = file.readLine()!!.toInt()
    val F = file.readLine()!!.split(" ").map { it.toInt() }.toHashSet()

    val delta = List(q) { mutableMapOf<Int, Int>() }

    val lines = file.readLines()
    for (i in 0 until lines.size - 1) {
        val (from, symbol, to) = lines[i].split(" ").map { it.toInt() }
        delta[from][symbol] = to
    }

    val dfa = DFA(
        q,
        sigma,
        q0,
        delta,
        F
    )

    val result = minimiseDfa(dfa)

    val output = FileWriter("output.txt")
    for (state in 0 until result.q) {
        result.delta[state].forEach { (symbol, to) ->
            output.write("$state $symbol $to\n")
        }
    }

    output.close()
}