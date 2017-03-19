package writers_readers

class ByNameSeeker(val id: Int, val addrBook: Map<String, String>) {

    fun getNumberFromName(name: String) = "By name seeker $id have found ${addrBook[name]}"
}

class ByPhoneSeeker(val id: Int, val addrBook: Map<String, String>) {

    fun getNameFromNumber(numberToMap: String) : String? {
        var nameToMap : String? = null

        for ((name, number) in addrBook) {
            if (number == numberToMap) {
                nameToMap = name
                break
            }
        }
        return "By phone seeker $id have found $nameToMap"
    }
}

class Modifier(val id: Int, val addrBook: MutableMap<String, String>) {

    fun addRecord(name : String, number : String) {
        addrBook[name] = number
        println("Modifier $id have added record with a name $name")
    }

    fun removeRecord(nameToRemove : String) {
        val removed = addrBook.remove(nameToRemove)
        val res = if (removed != null) nameToRemove else (-1).toString()
        println("Modifier $id have removed record with a name $res")
    }
}
