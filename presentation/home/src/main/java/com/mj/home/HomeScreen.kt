@file:OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)

package com.mj.home

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.mj.core.base.SIDE_EFFECTS_KEY
import com.mj.core.common.HtmlText
import com.mj.core.common.ImmutableGlideImage
import com.mj.core.common.appendCategoryData
import com.mj.core.theme.BargainPriceTheme
import com.mj.core.theme.Typography
import com.mj.core.theme.green_100
import com.mj.core.theme.green_200
import com.mj.core.theme.green_300
import com.mj.core.theme.green_500
import com.mj.core.theme.green_700
import com.mj.core.theme.white
import com.mj.core.toPriceFormat
import com.mj.home.HomeViewModel.ShoppingItem
import com.mj.home.model.Pages
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeContract.State,
    effectFlow: Flow<HomeContract.Effect>?,
    onEventSent: (event: HomeContract.Event) -> Unit,
    onNavigationRequested: (effect: HomeContract.Effect.Navigation) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val pagerState = rememberPagerState(
        pageCount = { Pages.entries.size }
    )
    val shoppingItems = state.shoppingItems.collectAsLazyPagingItems()
    val favoriteItem by state.favoriteShoppingItems.collectAsStateWithLifecycle()
    val priceAlarmActivated by state.priceAlarmActivated.collectAsState()

    val emptyQueryMsg = stringResource(R.string.empty_query)

    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is HomeContract.Effect.EmptyQuery -> Toast.makeText(context, emptyQueryMsg, Toast.LENGTH_SHORT).show()
                is HomeContract.Effect.Navigation.ToDetail -> onNavigationRequested(effect)
            }
        }?.collect()
    }

    Column(modifier = modifier) {
        HomeContent(
            focusManager = focusManager,
            pagerState = pagerState,
            priceAlarmActivated = priceAlarmActivated,
            shoppingItems = shoppingItems,
            favoriteItems = remember(favoriteItem) { favoriteItem.toImmutableList() },
            onPriceAlarmActive = { onEventSent(HomeContract.Event.AlarmActive(it)) },
            onQueryChanged = { onEventSent(HomeContract.Event.QueryChange(it)) },
            onSearchClick = { onEventSent(HomeContract.Event.SearchClick) },
            onAddFavoriteClick = { onEventSent(HomeContract.Event.AddFavorite(it)) },
            onDeleteFavoriteClick = { onEventSent(HomeContract.Event.DeleteFavorite(it)) },
            onItemClick = { onEventSent(HomeContract.Event.ItemClick(it)) },
            onPageChanged = { index ->
                coroutineScope.launch {
                    pagerState.scrollToPage(index)
                }
            },
            onRetryButtonClick = { onEventSent(HomeContract.Event.Retry) },
        )
    }
}

@Composable
private fun HomeContent(
    focusManager: FocusManager,
    pagerState: PagerState,
    priceAlarmActivated: Boolean,
    shoppingItems: LazyPagingItems<ShoppingItem>,
    favoriteItems: ImmutableList<ShoppingItem>,
    onPriceAlarmActive: (Boolean) -> Unit,
    onQueryChanged: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAddFavoriteClick: (ShoppingItem) -> Unit,
    onDeleteFavoriteClick: (String) -> Unit,
    onItemClick: (ShoppingItem) -> Unit,
    onPageChanged: (Int) -> Unit,
    onRetryButtonClick: () -> Unit,
) {

    TabRow(selectedTabIndex = pagerState.currentPage) {
        Pages.entries.forEachIndexed { index, title ->
            Tab(
                modifier = Modifier.background(white),
                text = { Text(text = stringResource(id = title.resId)) },
                selected = pagerState.currentPage == index,
                onClick = { onPageChanged(index) },
            )
        }
    }

    HorizontalPager(
        state = pagerState,
        beyondBoundsPageCount = pagerState.pageCount,
    ) { index ->
        when (Pages.entries[index]) {
            Pages.SEARCH -> {
                ShoppingListPage(
                    focusManager = focusManager,
                    shoppingItems = shoppingItems,
                    onQueryChanged = onQueryChanged,
                    onSearchClick = onSearchClick,
                    onItemClick = onItemClick,
                    onAddFavoriteClick = onAddFavoriteClick,
                    onDeleteFavoriteClick = onDeleteFavoriteClick,
                    onRetryButtonClick = onRetryButtonClick,
                )
            }

            Pages.FAVORITE -> {
                FavoriteListPage(
                    priceAlarmActivated = priceAlarmActivated,
                    favoriteItem = favoriteItems,
                    onPriceAlarmActive = onPriceAlarmActive,
                    onDeleteFavoriteClick = onDeleteFavoriteClick,
                    onItemClick = onItemClick,
                )
            }
        }
    }
}

@Composable
private fun SearchBox(
    fm: FocusManager,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
) {

    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = query,
                onValueChange = { insert ->
                    query = insert
                    onQueryChange(insert)
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search,
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        fm.clearFocus(true)
                        onSearchClick()
                    },
                ),
                textStyle = TextStyle.Default.copy(fontSize = Typography.bodyMedium.fontSize),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = white,
                    unfocusedContainerColor = white,
                    unfocusedTextColor = green_200,
                    focusedTextColor = green_500,
                ),
                label = {
                    Text(text = stringResource(id = R.string.query_label))
                },
            )
        }
    }
}

@Composable
private fun ShoppingListPage(
    focusManager: FocusManager,
    shoppingItems: LazyPagingItems<ShoppingItem>,
    onQueryChanged: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAddFavoriteClick: (ShoppingItem) -> Unit,
    onDeleteFavoriteClick: (String) -> Unit,
    onItemClick: (ShoppingItem) -> Unit,
    onRetryButtonClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBox(
            fm = focusManager,
            onQueryChange = onQueryChanged,
            onSearchClick = onSearchClick,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                shoppingItems.itemCount < 1 -> {
                    EmptyPage(
                        modifier = Modifier.fillMaxSize(),
                        label = stringResource(id = R.string.empty_query)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 5.dp),
                        contentPadding = PaddingValues(all = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        items(
                            count = shoppingItems.itemCount,
                            key = shoppingItems.itemKey { it.productId },
                        ) { index ->
                            val item = shoppingItems[index] ?: return@items
                            ShoppingItem(
                                item = item,
                                onAddFavoriteClick = onAddFavoriteClick,
                                onDeleteFavoriteClick = onDeleteFavoriteClick,
                                onItemClick = onItemClick,
                            )
                        }

                        shoppingItems.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item { PageLoader(modifier = Modifier.fillParentMaxSize()) }
                                }

                                loadState.refresh is LoadState.Error -> {
                                    val error = shoppingItems.loadState.refresh as LoadState.Error
                                    item {
                                        ErrorMessage(
                                            modifier = Modifier.fillParentMaxSize(),
                                            message = error.error.localizedMessage!!,
                                            onClickRetry = { onRetryButtonClick() })
                                    }
                                }

                                loadState.append is LoadState.Loading -> {
                                    item { LoadingPageItem(modifier = Modifier) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteListPage(
    priceAlarmActivated: Boolean,
    favoriteItem: ImmutableList<ShoppingItem>,
    onPriceAlarmActive: (Boolean) -> Unit,
    onDeleteFavoriteClick: (String) -> Unit,
    onItemClick: (ShoppingItem) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {

        if (favoriteItem.isNotEmpty()) {
            PriceAlarmSwitch(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                isChecked = priceAlarmActivated,
                onPriceAlarmActive = onPriceAlarmActive,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                favoriteItem.isEmpty() -> {
                    EmptyPage(
                        modifier = Modifier.fillMaxSize(),
                        label = stringResource(id = R.string.empty_favorite),
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 5.dp),
                        contentPadding = PaddingValues(all = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        items(
                            count = favoriteItem.size,
                            key = { index -> favoriteItem[index].productId }
                        ) { index ->
                            val item = favoriteItem[index]
                            ShoppingItem(
                                item = item,
                                onDeleteFavoriteClick = onDeleteFavoriteClick,
                                onItemClick = onItemClick,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShoppingItem(
    item: ShoppingItem,
    onAddFavoriteClick: (ShoppingItem) -> Unit = {},
    onDeleteFavoriteClick: (String) -> Unit = {},
    onItemClick: (ShoppingItem) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onItemClick(item) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(all = 5.dp)
        ) {
            ImmutableGlideImage(
                modifier = Modifier.fillMaxSize(),
                model = item.image,
            )

            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        when (item.isFavorite) {
                            true -> onDeleteFavoriteClick(item.productId)
                            else -> onAddFavoriteClick(item)
                        }
                    },
                painter = when (item.isFavorite) {
                    true -> painterResource(id = R.drawable.baseline_star_24)
                    else -> painterResource(id = R.drawable.baseline_star_border_24)
                },
                contentDescription = ""
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            HtmlText(
                htmlText = item.title,
                textStyle = Typography.titleMedium,
                maxLine = 2,
            )

            //최고가
            PriceLabel(
                isHighest = true,
                label = stringResource(id = R.string.highest_price),
                price = item.highestPrice,
            )

            //최저가
            PriceLabel(
                isHighest = false,
                label = stringResource(id = R.string.lowest_price),
                price = item.lowestPrice,
            )

            //어제의 최고가 혹은 최저가
            PrevShoppingItem(
                prevLowestPrice = item.prevLowestPrice,
                prevHighestPrice = item.prevHighestPrice,
                isRefreshFail = item.isRefreshFail,
            )

            //판매처
            Text(
                text = "${stringResource(id = R.string.mall_name)} ${item.mallName}",
                style = Typography.bodySmall,
                color = Color.Gray,
            )

            //브랜드
            Text(
                text = "${stringResource(id = R.string.brand)} ${item.brand.ifEmpty { stringResource(id = R.string.unknown) }}",
                style = Typography.bodySmall,
                color = Color.Gray,
            )

            //제조사
            Text(
                text = "${stringResource(id = R.string.maker)} ${item.maker.ifEmpty { stringResource(id = R.string.unknown) }}",
                style = Typography.bodySmall,
                color = Color.Gray,
            )

            //카테고리
            Text(
                text = "${stringResource(id = R.string.category)} ${appendCategoryData(item.category1, item.category2, item.category3, item.category4)}",
                style = Typography.bodySmall,
                color = Color.Gray,
            )
        }
    }
}

@Composable
private fun PrevShoppingItem(
    prevLowestPrice: String,
    prevHighestPrice: String,
    isRefreshFail: Boolean,
) {
    if (prevHighestPrice.isNotBlank() || prevLowestPrice.isNotBlank()) {
        PrevShoppingPrice(
            price = prevHighestPrice,
            label = stringResource(id = R.string.prev_highest_price),
        )

        PrevShoppingPrice(
            price = prevLowestPrice,
            label = stringResource(id = R.string.prev_lowest_price),
        )
    } else {
        if (isRefreshFail) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(id = R.string.refresh_fail),
                    color = white,
                )
            }
        }
    }
}

@Composable
private fun PrevShoppingPrice(
    price: String,
    label: String,
) {
    if (price.isNotBlank()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
            Text(
                text = label,
                style = Typography.bodyMedium,
            )
            Text(
                text = price.toPriceFormat()?.let { it + stringResource(id = R.string.price_won) } ?: stringResource(id = R.string.unknown_price),
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = green_700
            )
        }
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
private fun PriceAlarmSwitch(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onPriceAlarmActive: (Boolean) -> Unit,
) {
    var checked by remember { mutableStateOf(isChecked) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = "매일 가격 갱신 알람 받기(자정)")

        Switch(
            checked = checked,
            onCheckedChange = {
                checked = it
                onPriceAlarmActive(it)
            },
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = green_300,
                uncheckedTrackColor = white,
                uncheckedBorderColor = green_100,
                checkedThumbColor = white,
                checkedTrackColor = green_500,
                checkedBorderColor = green_100,
            ),
        )
    }
}

@Composable
private fun EmptyPage(
    modifier: Modifier = Modifier,
    label: String,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PageLoader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()

        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = stringResource(id = R.string.fetch_data_from_server),
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LoadingPageItem(modifier: Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}

@Composable
private fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier,
    onClickRetry: () -> Unit
) {
    Row(
        modifier = modifier.padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f),
            maxLines = 2
        )
        OutlinedButton(onClick = onClickRetry) {
            Text(text = stringResource(id = R.string.network_error_retry_button_text))
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    BargainPriceTheme {
        HomeScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(white),
            state = HomeContract.State(
                priceAlarmActivated = MutableStateFlow(true),
                shoppingItems = MutableStateFlow(PagingData.empty()),
                favoriteShoppingItems = MutableStateFlow(emptyList()),
            ),
            effectFlow = null,
            onEventSent = {},
            onNavigationRequested = {},
        )
    }
}

@Composable
@Preview
private fun PriceAlarmSwitchPreview() {
    BargainPriceTheme {
        PriceAlarmSwitch(
            modifier = Modifier
                .fillMaxWidth()
                .background(white),
            isChecked = false,
            onPriceAlarmActive = {}
        )
    }
}