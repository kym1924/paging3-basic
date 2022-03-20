package com.kimym.pokemon.binding

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

@BindingAdapter("setDetailImage")
fun ImageView.setDetailImage(url: String?) {
    url?.let {
        Glide.with(context)
            .load(url)
            .into(this)
    }
}

@BindingAdapter("setImage", "setIndexColor")
fun ImageView.setImage(url: String?, tvIndex: TextView) {
    url?.let {
        Glide.with(context)
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    val bitmap = resource?.toBitmap()
                    if (bitmap != null) {
                        Palette.from(bitmap).generate { palette ->
                            palette?.let {
                                val swatch = it.mutedSwatch
                                if (swatch != null) {
                                    tvIndex.backgroundTintList = ColorStateList.valueOf(swatch.rgb)
                                    tvIndex.setTextColor(swatch.titleTextColor)
                                }
                            }
                        }
                    }
                    return false
                }
            })
            .into(this)
    }
}
