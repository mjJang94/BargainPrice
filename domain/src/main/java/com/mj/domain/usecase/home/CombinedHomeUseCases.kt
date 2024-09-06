package com.mj.domain.usecase.home

data class CombinedHomeUseCases(
    val getFavoriteShoppingData: GetFavoriteShoppingListUseCase,
    val deleteFavoriteShoppingData: DeleteFavoriteShoppingUseCase,
    val getAlarmActive: GetAlarmActiveUseCase,
    val setAlarmActive: SetAlarmActiveUseCase,
    val getRefreshTime: GetRefreshTimeUseCase,
)