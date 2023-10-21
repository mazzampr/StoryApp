package com.mazzampr.storyapps.di

import android.content.Context
import com.mazzampr.storyapps.data.StoryRepository
import com.mazzampr.storyapps.data.pref.UserPreference
import com.mazzampr.storyapps.data.pref.dataStore
import com.mazzampr.storyapps.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val userToken = runBlocking { pref.getSession().first().toString() }
        val apiService = ApiConfig.retrofitInstance(userToken)
        return StoryRepository.getInstance(apiService, pref)
    }
}