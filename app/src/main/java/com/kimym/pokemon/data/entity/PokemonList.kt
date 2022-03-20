package com.kimym.pokemon.data.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class PokemonList(
    @SerializedName("results")
    val results: List<PokemonEntity>
)

@Parcelize
data class PokemonEntity(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String,
) : Parcelable {
    fun id(): Int {
        return url.dropLast(1).split("/").last().toInt()
    }
}
