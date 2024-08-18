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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.mj.core.theme.green_200
import com.mj.core.theme.green_500
import com.mj.core.theme.white
import com.mj.core.toPriceFormat
import com.mj.home.HomeViewModel.ShoppingItem
import com.mj.home.model.Pages
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

    val shoppingPagingItem = state.shoppingInfo.collectAsLazyPagingItems()

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
            pagingItems = shoppingPagingItem,
            onQueryChanged = { onEventSent(HomeContract.Event.QueryChange(it)) },
            onSearchClick = { onEventSent(HomeContract.Event.SearchClick) },
            onFavoriteClick = { onEventSent(HomeContract.Event.AddFavorite(it)) },
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
    pagingItems: LazyPagingItems<ShoppingItem>,
    onQueryChanged: (String) -> Unit,
    onSearchClick: () -> Unit,
    onFavoriteClick: (ShoppingItem) -> Unit,
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
                    shoppingPagingItem = pagingItems,
                    onQueryChanged = onQueryChanged,
                    onSearchClick = onSearchClick,
                    onItemClick = onItemClick,
                    onFavoriteClick = onFavoriteClick,
                    onRetryButtonClick = onRetryButtonClick,
                )
            }

            Pages.FAVORITE -> {
                Text(text = "asd")
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
    shoppingPagingItem: LazyPagingItems<ShoppingItem>,
    onQueryChanged: (String) -> Unit,
    onSearchClick: () -> Unit,
    onFavoriteClick: (ShoppingItem) -> Unit,
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
                shoppingPagingItem.itemCount < 1 -> EmptyPage(modifier = Modifier.fillMaxSize())
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 5.dp),
                        contentPadding = PaddingValues(all = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        items(
                            count = shoppingPagingItem.itemCount,
                            key = shoppingPagingItem.itemKey { it.productId },
                        ) { index ->
                            val item = shoppingPagingItem[index] ?: return@items
                            NewsRow(
                                item = item,
                                onFavoriteClick = onFavoriteClick,
                                onItemClick = onItemClick,
                            )
                        }

                        shoppingPagingItem.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item { PageLoader(modifier = Modifier.fillParentMaxSize()) }
                                }

                                loadState.refresh is LoadState.Error -> {
                                    val error = shoppingPagingItem.loadState.refresh as LoadState.Error
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
private fun NewsRow(
    item: ShoppingItem,
    onFavoriteClick: (ShoppingItem) -> Unit,
    onItemClick: (ShoppingItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onItemClick(item) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier
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
                    .clickable { onFavoriteClick(item) },
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
                price = item.highestPrice,
            )

            //최저가
            PriceLabel(
                isHighest = false,
                price = item.lowestPrice,
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
fun PriceLabel(
    isHighest: Boolean,
    price: String,
) {
    if (price.isNotBlank()) {
        Row {
            if (!isHighest) {
                Text(
                    text = stringResource(id = R.string.lowest_price),
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
fun EmptyPage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.empty_query),
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PageLoader(modifier: Modifier = Modifier) {
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
fun LoadingPageItem(modifier: Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}

@Composable
fun ErrorMessage(
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
fun NetworkError(
    modifier: Modifier = Modifier,
    onRetryButtonClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.network_error_title),
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(R.string.network_error_description),
            style = Typography.bodySmall,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
        )

        Button(onClick = { onRetryButtonClick() }) {
            Text(text = stringResource(R.string.network_error_retry_button_text).uppercase())
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
                query = MutableStateFlow(""),
                shoppingInfo = MutableStateFlow(PagingData.empty()),
            ),
            effectFlow = null,
            onEventSent = {},
            onNavigationRequested = {},
        )
    }
}