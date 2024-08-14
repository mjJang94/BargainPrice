package com.mj.home.model

import androidx.paging.PagingData
import com.mj.domain.model.ShoppingData

data class HomeInfo(

    val shoppingData: PagingData<ShoppingData>
)