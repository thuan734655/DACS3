package com.example.dacs3.di

import android.app.Application
import com.example.dacs3.util.DeviceUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {
    
    @Provides
    @Singleton
    fun provideDeviceUtils(application: Application): DeviceUtils {
        return DeviceUtils(application)
    }
} 