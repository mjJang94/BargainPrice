@file:OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)

package com.mj.presentation.home

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.mj.core.common.compose.ImmutableGlideImage
import com.mj.core.common.compose.appendCategoryData
import com.mj.core.theme.BargainPriceTheme
import com.mj.core.theme.Typography
import com.mj.core.theme.black
import com.mj.core.theme.blue
import com.mj.core.theme.gray
import com.mj.core.theme.green_100
import com.mj.core.theme.green_200
import com.mj.core.theme.green_300
import com.mj.core.theme.green_50
import com.mj.core.theme.green_500
import com.mj.core.theme.green_900
import com.mj.core.theme.red
import com.mj.core.theme.white
import com.mj.core.timeFormatDebugFull
import com.mj.core.toPriceFormat
import com.mj.presentation.R
import com.mj.presentation.home.HomeContract.Effect
import com.mj.presentation.home.HomeContract.Event
import com.mj.presentation.home.HomeContract.State
import com.mj.presentation.home.HomeViewModel.PriceState
import com.mj.presentation.home.HomeViewModel.ShoppingItem
import com.mj.presentation.home.model.Pages
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
    state: State,
    effectFlow: Flow<Effect>?,
    onEventSent: (event: Event) -> Unit,
    onNavigationRequested: (effect: Effect.Navigation) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val pagerState = rememberPagerState(
        pageCount = { Pages.entries.size }
    )

    val shoppingItems = state.shoppingItems.collectAsLazyPagingItems()

    val changedQuery by state.changedQuery.collectAsStateWithLifecycle()
    val favoriteItems by state.favoriteShoppingItems.collectAsStateWithLifecycle()
    val recentQueriesItems by state.recentQueries.collectAsStateWithLifecycle()
    val priceAlarmActivated by state.priceAlarmActivated.collectAsStateWithLifecycle()
    val currentRefreshTime by state.refreshTime.collectAsStateWithLifecycle()


    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is Effect.Navigation.ToDetail -> onNavigationRequested(effect)
            }
        }?.collect()
    }

    Column(modifier = modifier) {
        HomeContent(
            focusManager = focusManager,
            changedQuery = changedQuery,
            pagerState = pagerState,
            priceAlarmActivated = priceAlarmActivated,
            currentRefreshTime = currentRefreshTime,
            shoppingItems = shoppingItems,
            recentQueriesItems = remember(recentQueriesItems) { recentQueriesItems.toImmutableList() },
            favoriteItems = remember(favoriteItems) { favoriteItems.toImmutableList() },
            onRecentQueryClick = { onEventSent(Event.RecentQueryClick(it)) },
            onPriceAlarmActive = { onEventSent(Event.AlarmActive(it)) },
            onQueryChanged = { onEventSent(Event.QueryChange(it)) },
            onSearchClick = { onEventSent(Event.SearchClick) },
            onAddFavoriteClick = { onEventSent(Event.AddFavorite(it)) },
            onDeleteFavoriteClick = { onEventSent(Event.DeleteFavorite(it)) },
            onItemClick = { onEventSent(Event.ItemClick(it)) },
            onPageChanged = { index ->
                coroutineScope.launch {
                    pagerState.scrollToPage(index)
                }
            },
            onRetryButtonClick = { onEventSent(Event.Retry) },
            onDeleteQuery = { onEventSent(Event.DeleteQuery(it)) }
        )
    }
}

@Composable
private fun HomeContent(
    focusManager: FocusManager,
    changedQuery: String,
    pagerState: PagerState,
    priceAlarmActivated: Boolean,
    currentRefreshTime: Long,
    shoppingItems: LazyPagingItems<ShoppingItem>,
    recentQueriesItems: ImmutableList<String>,
    favoriteItems: ImmutableList<ShoppingItem>,
    onRecentQueryClick: (String) -> Unit,
    onPriceAlarmActive: (Boolean) -> Unit,
    onQueryChanged: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAddFavoriteClick: (ShoppingItem) -> Unit,
    onDeleteFavoriteClick: (String) -> Unit,
    onItemClick: (String) -> Unit,
    onPageChanged: (Int) -> Unit,
    onRetryButtonClick: () -> Unit,
    onDeleteQuery: (String) -> Unit,
) {

    Column {
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            state = pagerState,
            userScrollEnabled = false,
        ) { index ->
            when (Pages.entries[index]) {
                Pages.SEARCH -> {
                    ShoppingListPage(
                        focusManager = focusManager,
                        changedQuery = changedQuery,
                        shoppingItems = shoppingItems,
                        recentQueriesItems = recentQueriesItems,
                        onRecentQueryClick = onRecentQueryClick,
                        onQueryChanged = onQueryChanged,
                        onSearchClick = onSearchClick,
                        onAddFavoriteClick = onAddFavoriteClick,
                        onDeleteFavoriteClick = onDeleteFavoriteClick,
                        onRetryButtonClick = onRetryButtonClick,
                        onDeleteQuery = onDeleteQuery,
                    )
                }

                Pages.FAVORITE -> {
                    FavoriteListPage(
                        priceAlarmActivated = priceAlarmActivated,
                        currentRefreshTime = currentRefreshTime,
                        favoriteItem = favoriteItems,
                        onPriceAlarmActive = onPriceAlarmActive,
                        onDeleteFavoriteClick = onDeleteFavoriteClick,
                        onItemClick = onItemClick,
                    )
                }
            }
        }

        TabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = pagerState.currentPage,
            indicator = {},
            divider = {},
        ) {
            Pages.entries.forEachIndexed { index, title ->
                Tab(
                    modifier = Modifier.background(green_500),
                    text = {
                        Text(
                            text = stringResource(id = title.resId),
                            style = Typography.titleLarge,
                            color = when (pagerState.currentPage == index) {
                                true -> green_50
                                else -> green_900
                            }
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = { onPageChanged(index) },
                )
            }
        }
    }
}

@Composable
private fun ShoppingListPage(
    focusManager: FocusManager,
    changedQuery: String,
    shoppingItems: LazyPagingItems<ShoppingItem>,
    recentQueriesItems: ImmutableList<String>,
    onRecentQueryClick: (String) -> Unit,
    onQueryChanged: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAddFavoriteClick: (ShoppingItem) -> Unit,
    onDeleteFavoriteClick: (String) -> Unit,
    onRetryButtonClick: () -> Unit,
    onDeleteQuery: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBox(
            fm = focusManager,
            changedQuery = changedQuery,
            onQueryChange = onQueryChanged,
            onSearchClick = onSearchClick,
        )

        RecentQueriesList(
            recentQueriesItems = recentQueriesItems,
            onRecentQueryClick = onRecentQueryClick,
            onDeleteQuery = onDeleteQuery
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            ShoppingList(
                shoppingItems = shoppingItems,
                onAddFavoriteClick = onAddFavoriteClick,
                onDeleteFavoriteClick = onDeleteFavoriteClick,
                onRetryButtonClick = onRetryButtonClick,
            )
        }
    }
}

@Composable
private fun SearchBox(
    fm: FocusManager,
    changedQuery: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
) {

    var query by remember { mutableStateOf("") }

    LaunchedEffect(changedQuery) {
        query = changedQuery
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 5.dp, start = 10.dp, end = 10.dp),
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
private fun RecentQueriesList(
    recentQueriesItems: ImmutableList<String>,
    onRecentQueryClick: (String) -> Unit,
    onDeleteQuery: (String) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(
            count = recentQueriesItems.size,
            key = { index -> recentQueriesItems[index] }
        ) { index ->
            val query = recentQueriesItems[index]

            Box(
                modifier = Modifier.wrapContentSize(),
                contentAlignment = Alignment.TopEnd,
            ) {
                TextButton(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonColors(
                        contentColor = green_500,
                        containerColor = green_50,
                        disabledContentColor = green_500,
                        disabledContainerColor = green_50,
                    ),
                    onClick = { onRecentQueryClick(query) },
                ) {
                    Text(
                        text = query,
                        style = Typography.bodyMedium,
                    )
                }

                Image(
                    modifier = Modifier.clickable { onDeleteQuery(query) },
                    painter = painterResource(id = R.drawable.baseline_cancel_24),
                    contentDescription = "",
                )
            }
        }
    }
}

@Composable
private fun ShoppingList(
    shoppingItems: LazyPagingItems<ShoppingItem>,
    onAddFavoriteClick: (ShoppingItem) -> Unit,
    onDeleteFavoriteClick: (String) -> Unit,
    onRetryButtonClick: () -> Unit,
) {
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
            ShoppingListRow(
                item = item,
                onAddFavoriteClick = onAddFavoriteClick,
                onDeleteFavoriteClick = onDeleteFavoriteClick,
                onItemClick = {
                    when (item.isFavorite) {
                        true -> onDeleteFavoriteClick(item.productId)
                        else -> onAddFavoriteClick(item)
                    }
                }
            )
        }

        shoppingItems.apply {
//            when {
//                loadState.refresh is LoadState.Loading -> {
//                    item { PageLoader(modifier = Modifier.fillParentMaxSize()) }
//                }
//
//                loadState.refresh is LoadState.Error -> {
//                    val error = shoppingItems.loadState.refresh as LoadState.Error
//                    item {
//                        ErrorMessage(
//                            modifier = Modifier.fillParentMaxSize(),
//                            message = error.error.localizedMessage!!,
//                            onClickRetry = { onRetryButtonClick() })
//                    }
//                }
//
//                loadState.append is LoadState.Loading -> {
//                    item { LoadingPageItem(modifier = Modifier) }
//                }

//                loadState.append is LoadState.NotLoading -> {
//                    if (shoppingItems.itemCount < 1) {
//                        item {
//                            EmptyPage(
//                                modifier = Modifier.fillMaxSize(),
//                                label = stringResource(id = R.string.empty_query_result)
//                            )
//                        }
//                    }
//                }
            }
//        }
    }
}

@Composable
private fun FavoriteListPage(
    priceAlarmActivated: Boolean,
    currentRefreshTime: Long,
    favoriteItem: ImmutableList<ShoppingItem>,
    onPriceAlarmActive: (Boolean) -> Unit,
    onDeleteFavoriteClick: (String) -> Unit,
    onItemClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {

        if (favoriteItem.isNotEmpty()) {
            PriceAlarmSwitch(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp),
                isChecked = priceAlarmActivated,
                currentRefreshTime = currentRefreshTime,
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
                            ShoppingListRow(
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
private fun ShoppingListRow(
    item: ShoppingItem,
    onAddFavoriteClick: (ShoppingItem) -> Unit = {},
    onDeleteFavoriteClick: (String) -> Unit = {},
    onItemClick: (String) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onItemClick(item.productId) },
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
            Text(
                text = item.title,
                style = Typography.titleMedium,
                maxLines = 2,
            )

            //최저가
            LowestPriceLabel(
                price = item.lowestPrice,
                priceState = item.priceState,
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
private fun LowestPriceLabel(
    price: String,
    priceState: PriceState?,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        PriceLabel(
            label = stringResource(id = R.string.lowest_price),
            price = price,
        )

        val (color, difference, icon) = when (priceState) {
            is PriceState.Increase -> Triple(red, priceState.difference, painterResource(id = R.drawable.baseline_arrow_drop_up_24))
            is PriceState.Decrease -> Triple(blue, priceState.difference, painterResource(id = R.drawable.baseline_arrow_drop_down_24))
            else -> return
        }

        //차이값이 기준치에 도달하지 못할 경우 걸러짐
        if (!difference.isNullOrEmpty()) {
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .background(color, RoundedCornerShape(4.dp))
                    .padding(horizontal = 2.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Image(painter = icon, contentDescription = "")
                Text(
                    text = "$difference",
                    style = Typography.bodyMedium,
                    color = white,
                )
            }
        }
    }
}

@Composable
private fun PriceLabel(
    label: String,
    price: String,
) {
    if (price.isNotBlank()) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                text = label,
                style = Typography.bodyMedium,
            )

            Text(
                text = price.toPriceFormat() ?: stringResource(id = R.string.unknown_price),
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Blue
            )
        }
    }
}

@Composable
private fun PriceAlarmSwitch(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    currentRefreshTime: Long,
    onPriceAlarmActive: (Boolean) -> Unit,
) {
    var checked by remember { mutableStateOf(isChecked) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.refresh_price_label),
                style = Typography.titleMedium,
                color = black
            )

            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = stringResource(id = R.string.refresh_price_desc),
                style = Typography.bodySmall,
                color = gray,
            )

            if (currentRefreshTime > 0L) {
                Text(
                    text = "${stringResource(id = R.string.refresh_last_time)} ${currentRefreshTime.timeFormatDebugFull()}",
                    style = Typography.bodySmall,
                    color = green_500,
                )
            }
        }

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
            state = State(
                changedQuery = MutableStateFlow(""),
                priceAlarmActivated = MutableStateFlow(true),
                shoppingItems = MutableStateFlow(PagingData.empty()),
                recentQueries = MutableStateFlow(emptyList()),
                favoriteShoppingItems = MutableStateFlow(emptyList()),
                refreshTime = MutableStateFlow(0L),
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
            currentRefreshTime = System.currentTimeMillis(),
            onPriceAlarmActive = {}
        )
    }
}