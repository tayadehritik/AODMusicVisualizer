package com.example.aodmusicvisualizer.di

import android.app.Application
import android.media.MediaPlayer
import com.example.aodmusicvisualizer.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMediaPlayer(app:Application): MediaPlayer {
        return MediaPlayer.create(app, R.raw.tick)
    }
}