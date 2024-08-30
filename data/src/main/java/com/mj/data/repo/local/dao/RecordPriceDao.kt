package com.mj.data.repo.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mj.data.repo.local.entity.RecordPriceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordPriceDao {

    @Query("SELECT * FROM RecordPriceEntity WHERE productId=:productId AND timeStamp BETWEEN :startTime AND :endTime")
    fun flow(
        productId: String,
        startTime: Long,
        endTime: Long,
    ): Flow<List<RecordPriceEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recordPriceEntity: RecordPriceEntity)

    @Query("DELETE FROM RecordPriceEntity WHERE productId=:productId")
    suspend fun deleteById(productId: String)
}