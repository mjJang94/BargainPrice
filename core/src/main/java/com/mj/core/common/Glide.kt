@file:OptIn(ExperimentalGlideComposeApi::class)

package com.mj.core.common

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.GlideImage as GImage
import com.bumptech.glide.integration.compose.RequestBuilderTransform

@Stable
data class ImmutableGlideImage(val data: Any?)

@Composable
fun ImmutableGlideImage(
    modifier: Modifier = Modifier,
    model: Any?,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    loading: Placeholder? = null,
    failure: Placeholder? = null,
    requestBuilderTransform: RequestBuilderTransform<Drawable> = { it },
) {
    GlideImage(
        modifier = modifier,
        model = ImmutableGlideImage(model),
        contentDescription = contentDescription,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        loading = loading,
        failure = failure,
        requestBuilderTransform = requestBuilderTransform,
    )
}

@Composable
private fun GlideImage(
    modifier: Modifier = Modifier,
    model: ImmutableGlideImage?,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    loading: Placeholder? = null,
    failure: Placeholder? = null,
    requestBuilderTransform: RequestBuilderTransform<Drawable> = { it },
) {
    GlideImage(
        modifier = modifier,
        model = model?.data,
        contentDescription = contentDescription,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        loading = loading,
        failure = failure,
        requestBuilderTransform = requestBuilderTransform,
    )
}

@Composable
private fun GlideImage(
    modifier: Modifier = Modifier,
    model: Any?,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    loading: Placeholder? = null,
    failure: Placeholder? = null,
    requestBuilderTransform: RequestBuilderTransform<Drawable> = { it },
) {
    GImage(
        modifier = modifier,
        model = model,
        contentDescription = contentDescription,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        loading = loading,
        failure = failure,
        requestBuilderTransform = requestBuilderTransform,
    )
}