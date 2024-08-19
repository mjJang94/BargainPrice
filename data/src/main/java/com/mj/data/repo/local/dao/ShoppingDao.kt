package com.mj.data.repo.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mj.data.repo.local.entity.ShoppingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shoppingentity")
    fun flow(): Flow<List<ShoppingEntity>>

    @Query("SELECT * FROM shoppingentity")
    suspend fun getAll(): List<ShoppingEntity>

    @Query("DELETE FROM shoppingentity WHERE productId=:productId")
    suspend fun deleteById(productId: String): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(newsEntity: ShoppingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(newsEntity: ShoppingEntity)
}