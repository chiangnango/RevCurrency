package com.example.revcurrency.util

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

object ImageUtil {

    fun load(
        @DrawableRes drawableResId: Int?,
        target: ImageView,
        @DrawableRes placeholderResId: Int = android.R.color.transparent
    ) {

        Glide.with(target.context)
            .load(drawableResId)
            .placeholder(placeholderResId)
            .into(target)
    }
}