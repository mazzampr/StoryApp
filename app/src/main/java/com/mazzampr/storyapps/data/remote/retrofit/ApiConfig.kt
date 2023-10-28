package com.mazzampr.storyapps.data.remote.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class ApiConfig{
    companion object{
        fun retrofitInstance(): ApiService {
            val api = Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return api.create(ApiService::class.java)
        }
    }
}
