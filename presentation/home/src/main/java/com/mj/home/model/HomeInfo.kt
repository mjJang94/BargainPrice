package com.mj.home.model

import androidx.paging.PagingData
import com.mj.domain.model.Shopping

data class HomeInfo(

    val shopping: PagingData<Shopping>
)