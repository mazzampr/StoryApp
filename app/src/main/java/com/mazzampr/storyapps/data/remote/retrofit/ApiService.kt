package com.mazzampr.storyapps.data.remote.retrofit


import com.mazzampr.storyapps.data.remote.response.ErrorResponse
import com.mazzampr.storyapps.data.remote.response.LoginResponse
import com.mazzampr.storyapps.data.remote.response.RegisterResponse
import com.mazzampr.storyapps.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("stories")
    fun getStories(@Header("Authorization") token: String,
                   @Query("location") location: Int = 1
   ): Call<StoryResponse>

    @GET("stories/{id}")
    fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<StoryResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<ErrorResponse>
}