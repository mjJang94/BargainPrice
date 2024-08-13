package com.mj.app.bargainprice.di

import android.content.Context
import com.mj.core.alarm.AlarmHelper
import com.mj.core.notification.NotificationHelper
import com.mj.core.perm.PermissionHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideNotificationHelper(
        @ApplicationContext context: Context
    ): NotificationHelper = NotificationHelper(context)

    @Provides
    @Singleton
    fun providePermissionHelper(
        @ApplicationContext context: Context
    ): PermissionHelper = PermissionHelper(context)

    @Provides
    @Singleton
    fun provideAlarmHelper(
        @ApplicationContext context: Context,
        permissionHelper: PermissionHelper,
    ): AlarmHelper = AlarmHelper(context, permissionHelper)

    @Provides
    @Singleton
    fun provideCoreBridge(
        notiHelper: NotificationHelper,
        permHelper: PermissionHelper,
        alarmHelper: AlarmHelper,
    ): CoreBridge = CoreBridgeImpl(notiHelper, permHelper, alarmHelper)
}