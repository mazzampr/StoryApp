package com.mazzampr.storyapps.di

import android.content.Context
import com.mazzampr.storyapps.data.StoryRepository
import com.mazzampr.storyapps.data.pref.UserPreference
import com.mazzampr.storyapps.data.pref.dataStore
import com.mazzampr.storyapps.data.remote.retrofit.ApiConfig
import com.mazzampr.storyapps.data.room.StoryDatabase

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val storyDatabase = StoryDatabase.getDatabase(context)
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.retrofitInstance()
        return StoryRepository.getInstance(storyDatabase,apiService, pref)
    }
}