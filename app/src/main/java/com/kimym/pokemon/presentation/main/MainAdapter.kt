package com.kimym.pokemon.presentation.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kimym.pokemon.data.entity.PokemonEntity
import com.kimym.pokemon.databinding.ItemPokemonBinding

class MainAdapter(private val move: (view: View, pokemon: PokemonEntity, color: Int?) -> Unit) :
    PagingDataAdapter<PokemonEntity, MainAdapter.PokemonViewHolder>(pokemonDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPokemonBinding.inflate(inflater)
        return PokemonViewHolder(binding, move)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    class PokemonViewHolder(
        private val binding: ItemPokemonBinding,
        private val move: (view: View, pokemon: PokemonEntity, color: Int?) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pokemonEntity: PokemonEntity) {
            with(binding) {
                pokemon = pokemonEntity
                executePendingBindings()
                root.setOnClickListener {
                    move(imgPokemon, pokemonEntity, tvPokemonIndex.backgroundTintList?.defaultColor)
                }
            }
        }
    }

    companion object {
        private val pokemonDiffUtil = object : DiffUtil.ItemCallback<PokemonEntity>() {
            override fun areItemsTheSame(oldItem: PokemonEntity, newItem: PokemonEntity) =
                oldItem.id() == newItem.id()

            override fun areContentsTheSame(oldItem: PokemonEntity, newItem: PokemonEntity) =
                oldItem == newItem
        }
    }
}
