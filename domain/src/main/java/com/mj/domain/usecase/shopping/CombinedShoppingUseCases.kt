package com.mj.domain.usecase.shopping

data class CombinedShoppingUseCases(
    val getShoppingData: GetShoppingDataUseCase,
    val getFavoriteShoppingData: GetFavoriteShoppingUseCase,
    val insertFavoriteShoppingData: InsertFavoriteShoppingUseCase,
    val deleteFavoriteShoppingData: DeleteFavoriteShoppingUseCase,
    val getAlarmActiveUseCase: GetAlarmActiveUseCase,
    val setAlarmActiveUseCase: SetAlarmActiveUseCase,
)