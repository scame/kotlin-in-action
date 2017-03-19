package writers_readers

import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class AddrBookController {

    companion object {
        val numberOfEach = 10
        val generatorInterval = 30
        val sleepTime = 10L
    }

    val addrBook: MutableMap<String, String> = HashMap()

    val rwLock: ReentrantReadWriteLock = ReentrantReadWriteLock(true)

    val atomicCounter = AtomicInteger(0)
    val readersPool = Executors.newCachedThreadPool()
    val writersPool = Executors.newCachedThreadPool()

    fun runController() {
        runWriters()
        runReaders()
    }

    private fun runWriters() {
        execInWriteContext(numberOfEach) {
            val writer = Modifier(atomicCounter.getAndIncrement(), addrBook)
            writer.addRecord(Random().generateRndName(generatorInterval), Random().generateRndNumber(generatorInterval))
        }

        execInWriteContext(numberOfEach) {
            val writer = Modifier(atomicCounter.getAndIncrement(), addrBook)
            writer.removeRecord(Random().generateRndName(generatorInterval))
        }
    }

    private fun runReaders() {
        execInReadContext(numberOfEach) {
            val byNameSeeker = ByNameSeeker(atomicCounter.incrementAndGet(), addrBook)
            println(byNameSeeker.getNumberFromName(Random().generateRndName(generatorInterval)))
        }

        execInReadContext(numberOfEach) {
            val byPhoneSeeker = ByPhoneSeeker(atomicCounter.incrementAndGet(), addrBook)
            println(byPhoneSeeker.getNameFromNumber(Random().generateRndNumber(generatorInterval)))
        }
    }


    private fun execInWriteContext(repeatTimes: Int, body: () -> Unit) {
        repeat(repeatTimes) {
            writersPool.execute {
                while (true) rwLock.write { body(); Thread.sleep(sleepTime) }
            }
        }
    }

    private fun execInReadContext(repeatTimes: Int, body: () -> Unit) {
        repeat(repeatTimes) {
            readersPool.execute {
                while (true) rwLock.read { body(); Thread.sleep(sleepTime) }
            }
        }
    }
}

fun Random.generateRndName(charInterval: Int) = this.nextInt(65 + charInterval).toChar().toString()

fun Random.generateRndNumber(numberInterval: Int) = this.nextInt(65 + numberInterval).toString()

fun main(args: Array<String>) {
    val abc = AddrBookController()
    abc.runController()
}
