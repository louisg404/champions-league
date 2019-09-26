package com.bitbuildr.bicloo

data class Station( // Usage of data class, to store data
    val id: Int,
    val name: String,
    val stands: StandsCount) {
    data class StandsCount(val free: Int, val taken: Int, val total: Int)
}