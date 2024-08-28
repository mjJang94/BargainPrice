package com.mj.domain.usecase.detail

data class CombinedDetailUseCases(
    val getRecordPriceUseCase: GetRecordPriceUseCase,
    val getFavoriteShoppingItemUseCase: GetFavoriteShoppingItemUseCase,
    val inertRecordPriceUseCase: InsertRecordPriceUseCase,
)