package com.mj.data.repo.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mj.data.repo.local.dao.RecordPriceDao
import com.mj.data.repo.local.dao.ShoppingDao
import com.mj.data.repo.local.entity.RecordPriceEntity
import com.mj.data.repo.local.entity.ShoppingEntity


@Database(
    entities = [
        ShoppingEntity::class,
        RecordPriceEntity::class,
    ],
    version = 1,
    exportSchema = false,
    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingDao(): ShoppingDao
    abstract fun recordPriceDao(): RecordPriceDao
}