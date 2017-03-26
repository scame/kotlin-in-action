package barries

import java.util.*
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors

class ArraysController(val array1: IntArray, val array2: IntArray, val array3: IntArray) {

    init {
        val rand = Random()
        for (i in 0..(ARRAY_SIZE - 1)) {
            array1[i] = rand.nextInt(RAND_INTERVAL)
            array2[i] = rand.nextInt(RAND_INTERVAL)
            array3[i] = rand.nextInt(RAND_INTERVAL)
        }
    }

    companion object {
        val THREADS_NUMBER = 3
        val ARRAY_SIZE = 3
        val RAND_INTERVAL = 5
    }

    private val pool = Executors.newFixedThreadPool(THREADS_NUMBER)
    private val barrier = CyclicBarrier(THREADS_NUMBER) {
        printEachSum()
        if (isSumEquals()) pool.shutdown()
    }

    private fun printEachSum() = println("${array1.sum()} ${array2.sum()} ${array3.sum()}")

    private fun isSumEquals() = (array1.sum() == array2.sum()) && (array2.sum() == array3.sum())

    private fun execInPool(func: () -> Unit) {
        pool.execute {
            while (!pool.isShutdown) {
                func()
                barrier.await()
            }
        }
    }

    private fun changeRandomElemRandomly(array : IntArray) {
        val rand = Random()
        array[rand.nextInt(ARRAY_SIZE)] = rand.nextInt(RAND_INTERVAL)
    }

    fun run() {
        execInPool { changeRandomElemRandomly(array1) }
        execInPool { changeRandomElemRandomly(array2) }
        execInPool { changeRandomElemRandomly(array3) }
    }
}

fun main(args: Array<String>) {
    ArraysController(IntArray(ArraysController.ARRAY_SIZE),
            IntArray(ArraysController.ARRAY_SIZE),
            IntArray(ArraysController.ARRAY_SIZE))
            .run()
}