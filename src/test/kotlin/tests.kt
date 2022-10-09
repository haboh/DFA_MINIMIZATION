import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.assertEquals

fun probabilisticCheckDfaEquivalence(
    A: DFA,
    B: DFA,
) {
    assert(A.sigma == B.sigma)

    val MAX_LENGTH = 25u
    val ROUNDS = 10000

    for (round in 0 until ROUNDS) {
        val wordLength = (Random.nextUInt() % MAX_LENGTH).toInt()
        val word = List(wordLength) { Random.nextInt() % A.sigma }
        assertEquals(A.accept(word), B.accept(word))
    }
}

class Tests {
    @Test
    fun `optimal two vertexes`() {
        val dfa = DFA(
            2,
            2,
            0,
            listOf(
                mapOf(
                    Pair(0, 1),
                    Pair(1, 1),
                ),
                mapOf(
                    Pair(0, 1),
                    Pair(1, 1),
                )
            ),
            setOf(1),
        )

        val result = minimiseDfa(dfa)
        probabilisticCheckDfaEquivalence(dfa, result)
    }

    @Test
    fun `test1`() {
        val dfa = DFA(
            5,
            2,
            0,
            listOf(
                mapOf(
                    Pair(0, 1),
                    Pair(1, 3),
                ),
                mapOf(
                    Pair(0, 2),
                    Pair(1, 4),
                ),
                mapOf(
                    Pair(0, 1),
                    Pair(1, 4),
                ),
                mapOf(
                    Pair(0, 2),
                    Pair(1, 4),
                ),
                mapOf(
                    Pair(0, 4),
                    Pair(1, 4),
                ),
            ),
            setOf(2, 4)
        )

        val optimalDFA = DFA(
            4,
            2,
            0,
            listOf(
                mapOf(
                    Pair(0, 1),
                    Pair(1, 1),
                ),
                mapOf(
                    Pair(0, 2),
                    Pair(1, 3),
                ),
                mapOf(
                    Pair(0, 1),
                    Pair(1, 3),
                ),
                mapOf(
                    Pair(0, 3),
                    Pair(1, 3),
                ),
            ),
            setOf(2, 3)
        )

        val result = minimiseDfa(dfa)
        probabilisticCheckDfaEquivalence(dfa, result)
        probabilisticCheckDfaEquivalence(optimalDFA, result)
        probabilisticCheckDfaEquivalence(optimalDFA, dfa)
        assertEquals(optimalDFA.q, result.q)
    }

    @Test
    fun `test2`() {
        val dfa = DFA(
            6,
            2,
            0,
            listOf(
                mapOf(
                    Pair(0, 2),
                    Pair(1, 1),
                ),
                mapOf(
                    Pair(0, 2),
                    Pair(1, 0),
                ),
                mapOf(
                    Pair(0, 3),
                    Pair(1, 4),
                ),
                mapOf(
                    Pair(0, 4),
                    Pair(1, 5),
                ),
                mapOf(
                    Pair(0, 4),
                    Pair(1, 4),
                ),
                mapOf(
                    Pair(0, 5),
                    Pair(1, 4),
                ),
            ),
            setOf(5, 4)
        )

        val optimalDFA = DFA(
            4,
            2,
            0,
            listOf(
                mapOf(
                    Pair(0, 1),
                    Pair(1, 0),
                ),
                mapOf(
                    Pair(0, 2),
                    Pair(1, 3),
                ),
                mapOf(
                    Pair(0, 3),
                    Pair(1, 3),
                ),
                mapOf(
                    Pair(0, 3),
                    Pair(1, 3),
                ),
            ),
            setOf(3)
        )

        val result = minimiseDfa(dfa)
        probabilisticCheckDfaEquivalence(dfa, result)
        probabilisticCheckDfaEquivalence(optimalDFA, result)
        probabilisticCheckDfaEquivalence(optimalDFA, dfa)
        assertEquals(optimalDFA.q, result.q)
    }

    @Test
    fun random_dfa() {
        val MAX_STATE = 25u
        val MAX_ALPHABET = 10u
        val ROUNDS = 100

        for (round in 0 until ROUNDS) {
            val alphabet = (Random.nextUInt() % (MAX_ALPHABET - 1u) + 1u).toInt()
            val states = (Random.nextUInt() % (MAX_STATE - 1u) + 1u).toInt()
            val delta = MutableList(states) { mutableMapOf<Int, Int>() }
            for (from in 0 until states) {
                for (char in 0 until alphabet) {
                    val to = (Random.nextUInt() % states.toUInt()).toInt()
                    delta[from][char] = to
                }
            }
            val q0 = 0
            val F = (0 until states).filter { Random.nextBoolean() }.toSet()
            
            if (F.isEmpty()) {
                continue
            }
            val dfa = DFA(
                states,
                alphabet,
                q0,
                delta,
                F
            )

            val result = minimiseDfa(dfa)
            probabilisticCheckDfaEquivalence(dfa, result)
            assert(dfa.q >= result.q)
        }
    }
}