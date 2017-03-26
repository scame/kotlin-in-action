package barries

import java.util.*
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors

class SoldiersController(val soldiersArray: BooleanArray) {

    init {
        randomize()
    }

    companion object {
        val SOLDIERS_NUMBER = 200
        val THREADS_NUMBER = 4
    }

    private val pool = Executors.newFixedThreadPool(THREADS_NUMBER)
    private val barrier = CyclicBarrier(THREADS_NUMBER) {

        var counter = 0
        (0 until 3)
                .map { (it + 1) * 50 - 1 }
                .forEach {
                    synchronized(this) { // flash memory
                        if (soldiersArray[it] == soldiersArray[it + 1]) ++counter
                    }
                }

        println(counter)

        if (counter == THREADS_NUMBER - 1) pool.shutdown()
    }

    private fun execInPool(func: () -> Unit) {
        pool.execute {
            while (!pool.isShutdown) {
                func()
                barrier.await()
            }
        }
    }

    fun run() {
        for (i in 0 until 4) {
            execInPool {
                randomize()

                val startInd = i * 50
                val endInd = (i + 1) * 50 - 1

                while (!equalsBetween(startInd, endInd)) {
                    lineUp(startInd, endInd)
                }
            }
        }
    }

    private fun randomize() {
        val rand = Random()
        for (i in 0..(SOLDIERS_NUMBER - 1)) {
            soldiersArray[i] = rand.nextBoolean()
        }
    }

    private fun equalsBetween(start: Int, end: Int): Boolean {
        var i = start
        while (i < end) {
            if (soldiersArray[i] != soldiersArray[i + 1]) return false
            ++i
        }
        return true
    }

    private fun lineUp(startIndex: Int, endIndex: Int) {
        val seed = Random().nextBoolean()

        var i = startIndex
        while (i <= endIndex) {
            soldiersArray[i] = if (seed) generateAlmostAlwaysTrue() else generateAlmostAlwaysFalse()
            ++i
        }
    }

    private fun generateAlmostAlwaysTrue(): Boolean {
        val rand = Random()
        return !(rand.nextBoolean() && rand.nextBoolean() && rand.nextBoolean() && rand.nextBoolean() && rand.nextBoolean())
    }

    private fun generateAlmostAlwaysFalse(): Boolean {
        val rand = Random()
        return rand.nextBoolean() && rand.nextBoolean() && rand.nextBoolean() && rand.nextBoolean() && rand.nextBoolean()
    }
}

fun main(args: Array<String>) {
    SoldiersController(BooleanArray(SoldiersController.SOLDIERS_NUMBER)).run()
}