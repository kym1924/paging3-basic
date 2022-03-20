package com.kimym.pokemon.presentation.detail

import androidx.lifecycle.ViewModel
import com.kimym.pokemon.data.entity.PokemonDetail
import com.kimym.pokemon.data.repository.PokemonRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: PokemonRepositoryImpl
) : ViewModel() {
    fun getPokemonDetail(id: Int): Flow<PokemonDetail> {
        return repository.getPokemonDetail(id)
    }
}
