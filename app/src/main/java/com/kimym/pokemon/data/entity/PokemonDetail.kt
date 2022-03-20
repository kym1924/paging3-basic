package com.kimym.pokemon.data.entity

import com.google.gson.annotations.SerializedName

data class PokemonDetail(
    @SerializedName("height")
    val height: Int,
    @SerializedName("weight")
    val weight: Int,
    @SerializedName("stats")
    val stats: List<Stat>,
    @SerializedName("types")
    val types: List<Type>
) {
    fun maxStat(): Int {
        return 200
    }
}

data class Stat(
    @SerializedName("base_stat")
    val baseStat: Int,
    @SerializedName("stat")
    val stat: Name
)

data class Type(
    @SerializedName("type")
    val type: Name
)

data class Name(
    @SerializedName("name")
    val name: String
)
