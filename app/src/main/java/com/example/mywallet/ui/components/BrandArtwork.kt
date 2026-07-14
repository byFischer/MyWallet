package com.example.mywallet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent

@Composable
fun BrandArtwork(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    adaptToAspectRatio: Boolean = false
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl == null) {
            BrandFallback()
        } else {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                contentScale = contentScale,
                filterQuality = FilterQuality.High,
                modifier = Modifier.fillMaxSize(),
                loading = { BrandFallback() },
                error = { BrandFallback() },
                success = { state ->
                    val image = state.result.image
                    val aspectRatio = if (image.height > 0) {
                        image.width.toFloat() / image.height.toFloat()
                    } else {
                        1f
                    }
                    val shouldContain = adaptToAspectRatio &&
                        (aspectRatio > 1.25f || aspectRatio < 0.8f)

                    SubcomposeAsyncImageContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(if (shouldContain) 10.dp else 0.dp),
                        contentScale = if (shouldContain) {
                            ContentScale.Fit
                        } else {
                            contentScale
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun BrandFallback() {
    val markColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.72f)

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FallbackStroke(width = 22.dp, color = markColor)
        FallbackStroke(width = 16.dp, color = markColor)
        FallbackStroke(width = 10.dp, color = markColor)
    }
}

@Composable
private fun FallbackStroke(width: Dp, color: Color) {
    Box(
        modifier = Modifier
            .width(width)
            .height(3.dp)
            .background(color, RoundedCornerShape(50))
    )
}
