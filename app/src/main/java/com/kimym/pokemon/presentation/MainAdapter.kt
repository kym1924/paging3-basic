package com.kimym.pokemon.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kimym.pokemon.data.entity.PokemonEntity
import com.kimym.pokemon.databinding.ItemPokemonBinding

class MainAdapter :
    PagingDataAdapter<PokemonEntity, MainAdapter.PokemonViewHolder>(pokemonDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPokemonBinding.inflate(inflater)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    class PokemonViewHolder(private val binding: ItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pokemon: PokemonEntity) {
            binding.pokemon = pokemon
            binding.executePendingBindings()
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
