package com.kimym.pokemon.data.api

import com.kimym.pokemon.data.entity.PokemonDetail
import com.kimym.pokemon.data.entity.PokemonList
import com.kimym.pokemon.data.repository.PokemonRepositoryImpl
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = PokemonRepositoryImpl.LIMIT
    ): PokemonList

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(
        @Path("id") id: Int
    ): PokemonDetail
}
