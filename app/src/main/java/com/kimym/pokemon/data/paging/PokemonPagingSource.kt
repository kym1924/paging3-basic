package com.kimym.pokemon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kimym.pokemon.data.api.PokemonService
import com.kimym.pokemon.data.entity.PokemonEntity
import com.kimym.pokemon.data.repository.PokemonRepositoryImpl

class PokemonPagingSource(
    private val service: PokemonService,
) : PagingSource<Int, PokemonEntity>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonEntity> {
        return try {
            val offset = params.key ?: 0
            val response = service.getPokemonList(offset).results
            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = if (response.isEmpty() || offset == 870) null else offset + PokemonRepositoryImpl.LIMIT
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PokemonEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(PokemonRepositoryImpl.LIMIT)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(PokemonRepositoryImpl.LIMIT)
        }
    }
}
