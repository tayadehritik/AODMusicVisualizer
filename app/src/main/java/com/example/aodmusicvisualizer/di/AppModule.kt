package com.example.aodmusicvisualizer.di

import android.app.Application
import android.media.MediaPlayer
import com.example.aodmusicvisualizer.Metronome
import com.example.aodmusicvisualizer.R
import com.example.aodmusicvisualizer.data.api.SpotifyAPI
import com.example.aodmusicvisualizer.data.api.SpotifyAuthAPI
import com.example.aodmusicvisualizer.data.api.SpotifyLocal
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesSpotifyAPI(): SpotifyAPI {
        return Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyAPI::class.java)
    }
    @Provides
    @Singleton
    fun providesSpotifyAuthAPI(): SpotifyAuthAPI {
        return Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyAuthAPI::class.java)
    }

    @Provides
    @Singleton
    fun providesSpotifyLocal(app:Application,
                             spotifyAPI: SpotifyAPI,
                             metronome: Metronome,
                             spotifyAuthAPI: SpotifyAuthAPI): SpotifyLocal {
        return SpotifyLocal(app,spotifyAPI,metronome,spotifyAuthAPI)
    }

    @Provides
    @Singleton
    fun providesMetronome(app:Application): Metronome {
        return Metronome(app)
    }
}