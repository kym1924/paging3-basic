package com.kimym.pokemon.presentation.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kimym.pokemon.R
import com.kimym.pokemon.data.entity.PokemonEntity
import com.kimym.pokemon.databinding.ActivityDetailBinding
import com.kimym.pokemon.util.intFromIntent
import com.kimym.pokemon.util.parcelableFromIntent
import com.kimym.pokemon.util.repeatOnLifecycle
import com.kimym.pokemon.util.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<DetailViewModel>()
    private val color by intFromIntent()
    private val pokemon by parcelableFromIntent<PokemonEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusBarColor(color)
        initPokemonDetail()
        initIntentExtra()
        initBackButtonClickListener()
    }

    override fun onBackPressed() {
        finishAfterTransition()
    }

    private fun initIntentExtra() {
        binding.color = color
        binding.pokemon = pokemon
        binding.executePendingBindings()
    }

    private fun initBackButtonClickListener() {
        binding.toolbarDetail.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initPokemonDetail() {
        repeatOnLifecycle {
            pokemon?.let { pokemon ->
                viewModel.getPokemonDetail(pokemon.id()).collect {
                    binding.pokemonDetail = it
                    binding.executePendingBindings()
                }
            }
        }
    }

    companion object {
        fun intentFor(context: Context, pokemon: PokemonEntity, color: Int?): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(context.getString(R.string.intent_pokemon), pokemon)
            intent.putExtra(context.getString(R.string.intent_color), color)
            return intent
        }
    }
}
