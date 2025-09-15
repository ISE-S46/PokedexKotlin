package com.example.pokedexandroid

data class Pokedex (
    val name: String,
    val type1: String,
    val type2: String,
    val total: Int,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val spAtk: Int,
    val spDef: Int,
    val speed: Int,
    val generation: Int,
    val isLegendary: Boolean
)