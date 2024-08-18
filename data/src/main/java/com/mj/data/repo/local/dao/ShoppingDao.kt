package com.mj.data.repo.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mj.data.repo.local.entity.ShoppingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shoppingentity")
    fun flow(): Flow<List<ShoppingEntity>>

    @Query("DELETE FROM shoppingentity WHERE productId=:productId")
    suspend fun deleteById(productId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(newsEntity: ShoppingEntity)
}