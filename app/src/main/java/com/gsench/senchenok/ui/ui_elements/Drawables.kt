package com.gsench.senchenok.ui.ui_elements

import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

fun getShimmerDrawable() = ShimmerDrawable().apply {
    setShimmer(
        Shimmer
            .AlphaHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
            .setDuration(1800) // how long the shimmering animation takes to do one full sweep
            .setBaseAlpha(0.7f) //the alpha of the underlying children
            .setHighlightAlpha(0.6f) // the shimmer alpha amount
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()
    )
}