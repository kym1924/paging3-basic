package com.kimym.pokemon.di

import com.kimym.pokemon.data.api.PokemonService
import com.kimym.pokemon.data.repository.PokemonRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @Provides
    @ViewModelScoped
    fun providePokemonRepository(pokemonService: PokemonService): PokemonRepositoryImpl {
        return PokemonRepositoryImpl(pokemonService)
    }
}
