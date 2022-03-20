package com.kimym.pokemon.binding

import android.content.res.ColorStateList
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kimym.pokemon.R

@BindingAdapter("setTypeColor")
fun TextView.setTypeColor(type: String?) {
    type?.let {
        val color = context.getColor(getTypeColor(it))
        backgroundTintList = ColorStateList.valueOf(color)
    }
}

@BindingAdapter("setProgressColor")
fun ProgressBar.setProgressColor(type: String?) {
    type?.let {
        val color = context.getColor(getTypeColor(it))
        progressTintList = ColorStateList.valueOf(color)
    }
}

private fun getTypeColor(type: String): Int {
    return when (type) {
        "normal" -> R.color.color_normal
        "fire" -> R.color.color_fire
        "water" -> R.color.color_water
        "grass" -> R.color.color_grass
        "electric" -> R.color.color_electric
        "ice" -> R.color.color_ice
        "fighting" -> R.color.color_fighting
        "poison" -> R.color.color_poison
        "ground" -> R.color.color_ground
        "flying" -> R.color.color_flying
        "psychic" -> R.color.color_psychic
        "bug" -> R.color.color_bug
        "rock" -> R.color.color_rock
        "ghost" -> R.color.color_ghost
        "dragon" -> R.color.color_dragon
        "dark" -> R.color.color_dark
        "steel" -> R.color.color_steel
        "fairy" -> R.color.color_fairy
        "???" -> R.color.color_mystery
        else -> R.color.black
    }
}
