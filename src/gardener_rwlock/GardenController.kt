package gardener_rwlock

import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class GardenController {

    companion object {
        val entitiesNumber = 4
        val plantsNumber = 30
        val sleepTime = 1L
    }

    val commonPool = Executors.newFixedThreadPool(entitiesNumber)

    val garden = Garden(MutableList(plantsNumber, { index -> Plant(index, false) }))

    val gardener = Gardener(garden)
    val nature = Nature(garden)
    val inMemReader = InMemReader(garden, mutableListOf())
    val printReader = Printer(garden)

    val rwLock : ReentrantReadWriteLock = ReentrantReadWriteLock(true)

    private fun runInReadContextIndefinitely(body : () -> Unit) {
        commonPool.execute {
            while (true) rwLock.read { body() }
        }
    }


    private fun runInWriteContextIndefinitely(body : () -> Unit) {
        commonPool.execute {
            while (true) rwLock.write { body() }
        }
    }

    fun run() {
        runInReadContextIndefinitely { printReader.print(); Thread.sleep(sleepTime) }
        runInReadContextIndefinitely { inMemReader.makeSnapshot(); Thread.sleep(sleepTime) }
        runInWriteContextIndefinitely { gardener.process(); Thread.sleep(sleepTime) }
        runInWriteContextIndefinitely { nature.process(); Thread.sleep(sleepTime) }
    }
}

fun main(args: Array<String>) {
    val gc = GardenController()
    gc.run()
}