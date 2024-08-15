@file:OptIn(ExperimentalFoundationApi::class)

package com.mj.home

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
import com.mj.core.base.SIDE_EFFECTS_KEY
import com.mj.core.common.HtmlText
import com.mj.core.common.Progress
import com.mj.core.theme.BargainPriceTheme
import com.mj.core.theme.Typography
import com.mj.core.theme.green_200
import com.mj.core.theme.green_500
import com.mj.core.theme.white
import com.mj.domain.model.ShoppingData
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
    val query = state.query.collectAsState()

    val emptyQueryMsg = stringResource(R.string.empty_query)
    val dataLoadedMsg = stringResource(R.string.home_screen_loaded_message)

    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is HomeContract.Effect.EmptyQuery -> Toast.makeText(context, emptyQueryMsg, Toast.LENGTH_SHORT).show()
                is HomeContract.Effect.DataLoaded -> Toast.makeText(context, dataLoadedMsg, Toast.LENGTH_SHORT).show()
                is HomeContract.Effect.Navigation.ToDetail -> onNavigationRequested(effect)
            }
        }?.collect()
    }

    Column(modifier = modifier) {
        HomeContent(
            focusManager = focusManager,
            pagerState = pagerState,
            isError = state.isError,
            isLoading = state.isLoading,
            provideQuery = { query.value },
            pagingItems = shoppingPagingItem,
            onQueryChanged = { onEventSent(HomeContract.Event.QueryChange(it)) },
            onSearchClick = { onEventSent(HomeContract.Event.SearchClick) },
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
    isLoading: Boolean,
    isError: Boolean,
    provideQuery: () -> String,
    pagingItems: LazyPagingItems<ShoppingData>,
    onQueryChanged: (String) -> Unit,
    onSearchClick: () -> Unit,
    onItemClick: (ShoppingData) -> Unit,
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
                    provideQuery = provideQuery,
                    isLoading = isLoading,
                    isError = isError,
                    shoppingPagingItem = pagingItems,
                    onQueryChanged = onQueryChanged,
                    onSearchClick = onSearchClick,
                    onItemClick = onItemClick,
                    onRetryButtonClick = onRetryButtonClick,
                )
            }

            Pages.SCRAP -> {
                Text(text = "asd")
            }
        }
    }
}

@Composable
private fun SearchBox(
    fm: FocusManager,
    provideQuery: () -> String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
) {
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
                value = provideQuery(),
                onValueChange = onQueryChange,
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
    provideQuery: () -> String,
    isLoading: Boolean,
    isError: Boolean,
    shoppingPagingItem: LazyPagingItems<ShoppingData>,
    onQueryChanged: (String) -> Unit,
    onSearchClick: () -> Unit,
    onItemClick: (ShoppingData) -> Unit,
    onRetryButtonClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {

        SearchBox(
            fm = focusManager,
            provideQuery = provideQuery,
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
                isLoading -> Progress()
                isError -> NetworkError(onRetryButtonClick = onRetryButtonClick)
            }

            if (shoppingPagingItem.itemCount < 1) {
                Text(
                    text = stringResource(id = R.string.home_screen_loaded_result_empty)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(all = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(shoppingPagingItem.itemCount) { index ->
                        val item = shoppingPagingItem[index] ?: return@items
                        NewsRow(
                            shoppingData = item,
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
                                        onClickRetry = { retry() })
                                }
                            }

                            loadState.append is LoadState.Loading -> {
                                item { LoadingNextPageItem(modifier = Modifier) }
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
    shoppingData: ShoppingData,
    onItemClick: (ShoppingData) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
//        TextButton(onClick = { onScrapClick(isAdd, shoppingData) }) {
//            Box(
//                modifier = Modifier
//                    .background(
//                        color = Color.Yellow,
//                        shape = RoundedCornerShape(10.dp)
//                    )
//                    .padding(10.dp)
//            ) {
//                Text(
//                    text = when (isAdd) {
//                        true -> stringResource(id = R.string.scrap)
//                        else -> stringResource(id = R.string.delete)
//                    },
//                    style = Typography.bodySmall,
//                    color = Color.Black,
//                )
//            }
//        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .wrapContentHeight()
                .clickable { onItemClick(shoppingData) }
        ) {
            HtmlText(
                htmlText = shoppingData.title,
                textStyle = Typography.titleLarge,
                maxLine = 2,
            )

            Spacer(modifier = Modifier.height(8.dp))

            HtmlText(
                htmlText = shoppingData.mallName,
                textStyle = Typography.bodyMedium,
                maxLine = 10,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = shoppingData.brand,
                style = Typography.bodySmall,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun PageLoader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.fetch_data_from_server),
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        CircularProgressIndicator(Modifier.padding(top = 10.dp))
    }
}

@Composable
fun LoadingNextPageItem(modifier: Modifier) {
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
                isLoading = false,
                isError = false,
            ),
            effectFlow = null,
            onEventSent = {},
            onNavigationRequested = {},
        )
    }
}

@Composable
@Preview
private fun NewsRowPreview() {
    BargainPriceTheme {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(white)
        ) {
            NewsRow(
                shoppingData = ShoppingData(
                    title = "제목입니다.",
                    link = "http://app.yonhapnews.co.kr/YNA/Basic/SNS/r.aspx?c=AKR20160926019000008&did=1195m",
                    image = "",
                    lowestPrice = "10000",
                    highestPrice = "10000",
                    mallName = "10000",
                    productId = "1",
                    productType = "1",
                    maker = "maker",
                    brand = "brand",
                    category1 = "category1",
                    category2 = "category2",
                    category3 = "category3",
                    category4 = "category4",
                ),
                onItemClick = {},
            )
        }
    }
}