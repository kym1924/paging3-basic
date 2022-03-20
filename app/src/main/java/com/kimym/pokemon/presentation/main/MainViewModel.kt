package com.kimym.pokemon.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.kimym.pokemon.data.repository.PokemonRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    repository: PokemonRepositoryImpl
) : ViewModel() {
    val pokemonList = repository.getPokemonList().cachedIn(viewModelScope)
}
