package com.mj.domain.usecase.search

data class CombinedSearchUseCases(
    val getShoppingData: GetShoppingDataUseCase,
    val getFavoriteData: GetFavoriteListUseCase,
    val getRecentQueries: GetRecentQueriesUseCase,
    val setRecentQueries: SetRecentQueriesUseCase,
    val deleteFavoriteData: DeleteFavoriteUseCase,
    val insertFavoriteData: InsertFavoriteUseCase,
)