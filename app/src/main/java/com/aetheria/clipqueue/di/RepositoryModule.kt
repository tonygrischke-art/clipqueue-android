package com.aetheria.clipqueue.di

import android.content.Context
import com.aetheria.clipqueue.data.ClipQueueDatabase
import com.aetheria.clipqueue.data.ClipQueueRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideClipQueueDatabase(@ApplicationContext context: Context): ClipQueueDatabase {
        return ClipQueueDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideClipQueueRepository(database: ClipQueueDatabase): ClipQueueRepository {
        return ClipQueueRepository(database.context)
    }
}