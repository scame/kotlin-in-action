package gardener_rwlock

import java.util.*

class Gardener(val garden: Garden) {

    fun process() {
        garden.plantList.filter { !it.isPoured && Random().nextBoolean() }.forEach { it.isPoured = true }
    }
}

class Nature(val garden: Garden) {

    fun process() {
        garden.plantList.filter { it.isPoured && Random().nextBoolean() }.forEach { it.isPoured = false }
    }
}

class InMemReader(val garden: Garden, val gardenStateList : MutableList<Garden>) {

    fun makeSnapshot() {
        gardenStateList.add(garden.copy())

        if (gardenStateList.size % 100 == 0) {
            println("DUMP START_____________________________ \n " +
                    "$gardenStateList \n " +
                    "poured:  ${gardenStateList.flatMap { it.plantList }.count { it.isPoured }} "
                    + "not poured ${gardenStateList.flatMap { it.plantList }.count { !it.isPoured }}"
                    + "____________________________________END")
        }
    }
}

class Printer(val garden: Garden) {

    fun print() {
        println(garden.plantList.count { it.isPoured })
    }
}