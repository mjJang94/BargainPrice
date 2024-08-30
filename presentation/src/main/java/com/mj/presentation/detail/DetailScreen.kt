@file:OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)

package com.mj.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.mj.core.base.SIDE_EFFECTS_KEY
import com.mj.core.common.compose.Chart7
import com.mj.core.common.compose.ImmutableGlideImage
import com.mj.core.common.compose.Toolbar
import com.mj.core.theme.BargainPriceTheme
import com.mj.core.theme.Typography
import com.mj.core.theme.black
import com.mj.core.theme.gray_light
import com.mj.core.theme.green_500
import com.mj.core.theme.green_700
import com.mj.core.theme.white
import com.mj.core.toPriceFormat
import com.mj.domain.model.Shopping
import com.mj.presentation.R
import com.mj.presentation.detail.DetailContract.Effect
import com.mj.presentation.detail.DetailContract.Event
import com.mj.presentation.detail.DetailContract.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    state: State,
    effectFlow: Flow<Effect>?,
    onEventSend: (event: Event) -> Unit,
    onNavigationRequested: (effect: Effect) -> Unit,
) {

    val shoppingInfo by state.shoppingInfo.collectAsStateWithLifecycle()
    val recordPrices by state.recordPrices.collectAsStateWithLifecycle()
    val recordTimes by state.recordTimes.collectAsStateWithLifecycle()

    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            onNavigationRequested(effect)
        }?.collect()
    }

    Column(modifier = modifier) {
        Toolbar(
            modifier = Modifier.fillMaxWidth(),
            titleText = stringResource(id = R.string.detail_title),
            navigationImage = Icons.AutoMirrored.Filled.ArrowBack,
            navigationAction = { onEventSend(Event.Back) }
        )

        DetailContent(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            shoppingInfo = shoppingInfo,
            recordPrices = recordPrices,
            recordTimes = recordTimes,
            onClickMall = { link -> onEventSend(Event.MallClick(link)) }
        )
    }
}

@Composable
private fun DetailContent(
    modifier: Modifier,
    shoppingInfo: Shopping?,
    recordPrices: List<Long>,
    recordTimes: List<Long>,
    onClickMall: (String) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (shoppingInfo == null) {
            LoadFail()
        } else {
            ProductDetails(
                shoppingInfo = shoppingInfo,
                recordPrices = recordPrices,
                recordTimes = recordTimes,
                onClickMall = onClickMall,
            )
        }
    }
}

@Composable
private fun LoadFail() {
    Text(
        text = stringResource(id = R.string.detail_shopping_info_load_failure),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun ProductDetails(
    shoppingInfo: Shopping,
    recordPrices: List<Long>,
    recordTimes: List<Long>,
    onClickMall: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gray_light),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FavoriteProductPanel(
            info = shoppingInfo,
            onClickMall = onClickMall,
        )

        PriceChanges(
            records = recordPrices,
            times = recordTimes,
        )
    }
}

@Composable
private fun FavoriteProductPanel(
    info: Shopping,
    onClickMall: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(white)
            .padding(start = 10.dp, end = 10.dp, bottom = 15.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
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

        //판매처
        Text(
            modifier = Modifier.clickable { onClickMall(info.link) },
            text = info.mallName,
            color = green_500,
            textDecoration = TextDecoration.Underline,
            style = Typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(5.dp))

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

        //브랜드
        Text(
            text = "${stringResource(id = R.string.brand)} ${info.brand.ifEmpty { stringResource(id = R.string.unknown) }}",
            style = Typography.bodyMedium,
            color = black,
        )

        //제조사
        Text(
            text = "${stringResource(id = R.string.maker)} ${info.maker.ifEmpty { stringResource(id = R.string.unknown) }}",
            style = Typography.bodyMedium,
            color = black,
        )
    }
}

@Composable
private fun PriceChanges(
    records: List<Long>,
    times: List<Long>,
) {
    Column(
        modifier = Modifier
            .background(white)
            .padding(horizontal = 10.dp, vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Chart7(
            modifier = Modifier.wrapContentSize(),
            prices = records,
            times = times,
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
                text = price.toPriceFormat() ?: stringResource(id = R.string.unknown_price),
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = green_700
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
            state = State(
                shoppingInfo = MutableStateFlow(null),
                recordPrices = MutableStateFlow(emptyList()),
                recordTimes = MutableStateFlow(emptyList()),
            ),
            effectFlow = null,
            onEventSend = {},
            onNavigationRequested = {},
        )
    }
}