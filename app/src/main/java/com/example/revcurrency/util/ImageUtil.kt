package com.example.revcurrency.util

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

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

    fun loadCircleImage(
        url: String,
        target: ImageView,
        @DrawableRes placeholderResId: Int = android.R.color.transparent
    ) {

        Glide.with(target.context)
            .load(url)
            .apply(
                RequestOptions
                    .circleCropTransform()
                    .placeholder(placeholderResId)
            )
            .into(target)
    }

    fun loadCircleImage(
        @DrawableRes drawableRes: Int?,
        target: ImageView,
        @DrawableRes placeholderResId: Int = android.R.color.transparent
    ) {

        Glide.with(target.context)
            .load(drawableRes)
            .apply(
                RequestOptions
                    .circleCropTransform()
                    .placeholder(placeholderResId)
            )
            .into(target)
    }
}