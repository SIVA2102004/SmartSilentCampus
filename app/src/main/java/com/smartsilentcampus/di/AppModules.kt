package com.smartsilentcampus.di

import android.content.Context
import com.smartsilentcampus.data.local.dao.*
import com.smartsilentcampus.data.local.database.AppPreferencesDataStore
import com.smartsilentcampus.data.local.database.SmartSilentDatabase
import com.smartsilentcampus.data.repository.*
import com.smartsilentcampus.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SmartSilentDatabase {
        return SmartSilentDatabase.buildDatabase(context)
    }

    @Provides
    fun provideLocationDao(database: SmartSilentDatabase): LocationDao = database.locationDao()

    @Provides
    fun provideSoundProfileDao(database: SmartSilentDatabase): SoundProfileDao = database.soundProfileDao()

    @Provides
    fun providePreviousSoundStateDao(database: SmartSilentDatabase): PreviousSoundStateDao =
        database.previousSoundStateDao()

    @Provides
    fun provideAutomationHistoryDao(database: SmartSilentDatabase): AutomationHistoryDao =
        database.automationHistoryDao()

    @Provides
    fun provideActiveZoneDao(database: SmartSilentDatabase): ActiveZoneDao = database.activeZoneDao()

    @Provides
    @Singleton
    fun provideAppPreferencesDataStore(@ApplicationContext context: Context): AppPreferencesDataStore =
        AppPreferencesDataStore(context)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    @Singleton
    abstract fun bindSoundProfileRepository(
        impl: SoundProfileRepositoryImpl
    ): SoundProfileRepository

    @Binds
    @Singleton
    abstract fun bindSoundStateRepository(
        impl: SoundStateRepositoryImpl
    ): SoundStateRepository

    @Binds
    @Singleton
    abstract fun bindActiveZoneRepository(
        impl: ActiveZoneRepositoryImpl
    ): ActiveZoneRepository

    @Binds
    @Singleton
    abstract fun bindAutomationHistoryRepository(
        impl: AutomationHistoryRepositoryImpl
    ): AutomationHistoryRepository
}
