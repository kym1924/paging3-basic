package com.kimym.pokemon.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kimym.pokemon.data.api.PokemonService
import com.kimym.pokemon.data.entity.PokemonDetail
import com.kimym.pokemon.data.entity.PokemonEntity
import com.kimym.pokemon.data.paging.PokemonPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val service: PokemonService
) : PokemonRepository {
    override fun getPokemonList(): Flow<PagingData<PokemonEntity>> {
        return Pager(
            config = PagingConfig(pageSize = LIMIT, enablePlaceholders = false),
            pagingSourceFactory = { PokemonPagingSource(service) }
        ).flow
    }

    override fun getPokemonDetail(id: Int): Flow<PokemonDetail> {
        return flow {
            kotlin.runCatching {
                service.getPokemonDetail(id)
            }.onSuccess { pokemon ->
                emit(pokemon)
            }.onFailure {
                Log.d("getPokemonDetailException", it.toString())
            }
        }
    }

    companion object {
        const val LIMIT = 30
    }
}
