package com.kimym.pokemon.presentation.main

import android.app.ActivityOptions
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.kimym.pokemon.R
import com.kimym.pokemon.data.entity.PokemonEntity
import com.kimym.pokemon.databinding.ActivityMainBinding
import com.kimym.pokemon.presentation.detail.DetailActivity
import com.kimym.pokemon.util.repeatOnLifecycle
import com.kimym.pokemon.util.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusBarColor(getColor(R.color.red))
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val pokemonAdapter =
            MainAdapter { view, pokemon, color -> startDetailActivity(view, pokemon, color) }

        with(binding.rvPokemon) {
            adapter = pokemonAdapter
            setHasFixedSize(true)
        }

        repeatOnLifecycle {
            viewModel.pokemonList.collectLatest {
                pokemonAdapter.submitData(it)
            }
        }
    }

    private fun startDetailActivity(view: View, pokemon: PokemonEntity, color: Int?) {
        val options =
            ActivityOptions.makeSceneTransitionAnimation(this, view, getString(R.string.transition))
        startActivity(DetailActivity.intentFor(this, pokemon, color), options.toBundle())
    }
}
