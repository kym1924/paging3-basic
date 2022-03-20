package com.kimym.pokemon.util

import android.app.Activity
import android.os.Parcelable
import kotlin.properties.ReadOnlyProperty

fun intFromIntent(default: Int = -1): ReadOnlyProperty<Activity, Int> {
    return ReadOnlyProperty { thisRef, property ->
        thisRef.intent.extras?.getInt(property.name) ?: default
    }
}

fun <P : Parcelable> parcelableFromIntent(default: P? = null): ReadOnlyProperty<Activity, P?> {
    return ReadOnlyProperty { thisRef, property ->
        thisRef.intent.extras?.getParcelable(property.name) ?: default
    }
}
