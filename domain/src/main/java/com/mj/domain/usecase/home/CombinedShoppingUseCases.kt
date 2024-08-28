package com.mj.domain.usecase.home

data class CombinedShoppingUseCases(
    val getShoppingData: GetShoppingDataUseCase,
    val getFavoriteShoppingData: GetFavoriteShoppingListUseCase,
    val insertFavoriteShoppingData: InsertFavoriteShoppingUseCase,
    val deleteFavoriteShoppingData: DeleteFavoriteShoppingUseCase,
    val getAlarmActiveUseCase: GetAlarmActiveUseCase,
    val setAlarmActiveUseCase: SetAlarmActiveUseCase,
    val getRecentQueriesUseCase: GetRecentQueriesUseCase,
    val setRecentQueriesUseCase: SetRecentQueriesUseCase,
    val getRefreshTimeUseCase: GetRefreshTimeUseCase,
)