package com.kimym.pokemon.util

import android.app.Activity
import androidx.annotation.ColorInt

fun Activity.setStatusBarColor(@ColorInt color: Int) {
    window.statusBarColor = color
}
