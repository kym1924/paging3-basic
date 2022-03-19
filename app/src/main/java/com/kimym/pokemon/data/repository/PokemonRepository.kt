package com.kimym.pokemon.data.repository

import androidx.paging.PagingData
import com.kimym.pokemon.data.entity.PokemonEntity
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPokemonList(): Flow<PagingData<PokemonEntity>>
}
