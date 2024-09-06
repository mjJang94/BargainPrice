@file:OptIn(ExperimentalGlideComposeApi::class)

package com.mj.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.mj.core.common.compose.ImmutableGlideImage
import com.mj.core.theme.Typography
import com.mj.core.theme.green_200
import com.mj.core.theme.green_50
import com.mj.core.theme.green_500
import com.mj.core.theme.white
import com.mj.presentation.R
import com.mj.presentation.base.SIDE_EFFECTS_KEY
import com.mj.presentation.search.SearchContract.Effect
import com.mj.presentation.search.SearchContract.Event
import com.mj.presentation.search.SearchContract.State
import com.mj.presentation.search.SearchViewModel.ShoppingItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    state: State,
    effectFlow: Flow<Effect>?,
    onEventSent: (event: Event) -> Unit,
    onNavigationRequested: (effect: Effect.Navigation) -> Unit,
) {

    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is Effect.Navigation.ToMain -> onNavigationRequested(effect)
            }
        }?.collect()
    }

    val focusManager = LocalFocusManager.current

    val query by state.query.collectAsStateWithLifecycle()
    val shoppingItems = state.shoppingItems.collectAsLazyPagingItems()
    val recentQueriesItems by state.recentQueries.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        ShoppingListPage(
            focusManager = focusManager,
            query = query,
            shoppingItems = shoppingItems,
            recentQueriesItems = remember(recentQueriesItems) { recentQueriesItems.toImmutableList() },
            onRecentQueryClick = { onEventSent(Event.RecentQueryClick(it)) },
            onQueryChanged = { onEventSent(Event.QueryChange(it)) },
            onSearchClick = { onEventSent(Event.SearchClick) },
            onDeleteFavoriteClick = { onEventSent(Event.DeleteFavorite(it)) },
            onAddFavoriteClick = { onEventSent(Event.AddFavorite(it)) },
            onRetryButtonClick = { onEventSent(Event.Retry) },
            onDeleteQuery = { onEventSent(Event.DeleteQuery(it)) },
        )
    }
}

@Composable
private fun ShoppingListPage(
    focusManager: FocusManager,
    query: String,
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
            query = query,
            onQueryChange = onQueryChanged,
            onSearchClick = onSearchClick,
        )

        RecentQueriesList(
            recentQueriesItems = recentQueriesItems,
            onRecentQueryClick = onRecentQueryClick,
            onDeleteQuery = onDeleteQuery
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
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
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
) {

    var q by remember { mutableStateOf("") }

    LaunchedEffect(query) {
        q = query
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
                value = q,
                onValueChange = { insert ->
                    q = insert
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

                loadState.append is LoadState.NotLoading -> {
                    if (shoppingItems.itemCount < 1) {
                        item {
                            EmptyPage(
                                modifier = Modifier.fillMaxSize(),
                                label = stringResource(id = R.string.empty_query_result)
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
    Row(modifier = Modifier
        .height(150.dp)
        .fillMaxWidth()
    ) {
        ImmutableGlideImage(
            modifier = Modifier.fillMaxSize(),
            model = item.image,
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