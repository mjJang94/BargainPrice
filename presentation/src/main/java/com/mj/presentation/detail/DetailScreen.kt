@file:OptIn(ExperimentalGlideComposeApi::class)

package com.mj.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.mj.core.base.SIDE_EFFECTS_KEY
import com.mj.core.common.compose.ImmutableGlideImage
import com.mj.core.theme.BargainPriceTheme
import com.mj.core.theme.Typography
import com.mj.core.theme.white
import com.mj.core.toPriceFormat
import com.mj.domain.model.Shopping
import com.mj.presentation.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    state: DetailContract.State,
    effectFlow: Flow<DetailContract.Effect>?,
    onEventSend: (event: DetailContract.Event) -> Unit,
    onNavigationRequested: (effect: DetailContract.Effect.Navigation) -> Unit,
) {

    val shoppingInfo by state.shoppingInfo.collectAsStateWithLifecycle()

    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is DetailContract.Effect.Navigation.ToMain -> onNavigationRequested(effect)
            }
        }?.collect()
    }

    Column(modifier = modifier) {
        DetailContent(
            shoppingInfo = shoppingInfo
        )
    }
}

@Composable
private fun DetailContent(
    shoppingInfo: Shopping?,
) {
    if (shoppingInfo == null) {
        LoadFail()
    } else {
        FavoriteProduct(info = shoppingInfo)
    }
}

@Composable
private fun LoadFail() {
    Text(text = stringResource(id = R.string.detail_shopping_info_load_failure))
}

@Composable
private fun FavoriteProduct(info: Shopping) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        //이미지
        ImmutableGlideImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            model = info.image,
        )

        //상품명
        Text(
            text = info.title,
            style = Typography.titleLarge,
            maxLines = 2,
        )

        //몰
        Text(
            modifier = Modifier.padding(bottom = 10.dp),
            text = info.mallName,
            style = Typography.titleMedium,
        )

        //최고가
        PriceLabel(
            isHighest = true,
            label = stringResource(id = R.string.highest_price),
            price = info.highestPrice,
        )

        //최저가
        PriceLabel(
            isHighest = false,
            label = stringResource(id = R.string.lowest_price),
            price = info.lowestPrice,
        )
    }
}

@Composable
private fun PriceLabel(
    isHighest: Boolean,
    label: String,
    price: String,
) {
    if (price.isNotBlank()) {
        Row {
            if (!isHighest) {
                Text(
                    text = label,
                    style = Typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = price.toPriceFormat()?.let { it + stringResource(id = R.string.price_won) } ?: stringResource(id = R.string.unknown_price),
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = when (isHighest) {
                    true -> Color.Red
                    else -> Color.Blue
                }
            )
        }
    }
}

@Composable
@Preview
private fun DetailScreenPreview() {
    BargainPriceTheme {
        DetailScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(white),
            state = DetailContract.State(
                shoppingInfo = MutableStateFlow(null)
            ),
            effectFlow = null,
            onEventSend = {},
            onNavigationRequested = {},
        )
    }
}