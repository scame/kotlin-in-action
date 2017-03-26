package barries

import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors

class StringsController(var str1: String, var str2: String, var str3: String, var str4: String) {

    companion object {
        val THREADS_NUMBER = 4
    }

    private val pool = Executors.newFixedThreadPool(THREADS_NUMBER)
    private val barrier = CyclicBarrier(THREADS_NUMBER) {
        val condition = atLeastThreeEquals()
        println("$condition")

        if (condition) pool.shutdown()
    }

    fun run() {
        execInPool { str1 = mutateString(str1) }
        execInPool { str2 = mutateString(str2) }
        execInPool { str3 = mutateString(str3) }
        execInPool { str4 = mutateString(str4) }
    }

    private fun execInPool(func: () -> Unit) {
        pool.execute {
            while (!pool.isShutdown) {
                func()
                barrier.await()
            }
        }
    }

    private fun mutateString(str: String): String {
        val builder = StringBuilder()

        for (symbol in str) {
            when (symbol) {
                'a' -> builder.append('c')
                'c' -> builder.append('a')
                'b' -> builder.append('d')
                'd' -> builder.append('b')
            }
        }
        return builder.toString()
    }

    private fun atLeastThreeEquals(): Boolean {
        val strList = listOf(str1, str2, str3, str4)
        return strList.map { countAandB(it) }.distinct().count() <= 2
    }

    private fun countAandB(str: String) = str.count { it == 'a' || it == 'b' }
}

fun main(args: Array<String>) {
    StringsController("abca", "bcda", "abcd", "acdb").run()
}