package gardener_rwlock

data class Plant(val id: Int, var isPoured: Boolean)

data class Garden(val plantList: List<Plant>)
