package com.kimym.pokemon.data.entity

import com.google.gson.annotations.SerializedName

data class PokemonList(
    @SerializedName("results")
    val results: List<PokemonEntity>
)

data class PokemonEntity(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String,
) {
    fun id(): Int {
        return url.dropLast(1).split("/").last().toInt()
    }
}
