package com.mazzampr.storyapps.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.gson.Gson
import com.mazzampr.storyapps.data.pref.UserPreference
import com.mazzampr.storyapps.data.remote.response.ErrorResponse
import com.mazzampr.storyapps.data.remote.response.ListStoryItem
import com.mazzampr.storyapps.data.remote.response.LoginResponse
import com.mazzampr.storyapps.data.remote.response.RegisterResponse
import com.mazzampr.storyapps.data.remote.response.StoryResponse
import com.mazzampr.storyapps.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
)
{
    private val resultLogin = MediatorLiveData<Result<String>>()
    private val allStoriesResult = MediatorLiveData<Result<List<ListStoryItem>>>()
    private val detailStoryResult = MediatorLiveData<Result<ListStoryItem>>()
    private val registerResult = MediatorLiveData<Result<RegisterResponse>>()
    private val uploadResult = MediatorLiveData<Result<ErrorResponse>>()

    fun uploadStory(image: MultipartBody.Part, desc: RequestBody) : LiveData<Result<ErrorResponse>> {
        uploadResult.value = Result.Loading
        apiService.uploadStory(image, desc).enqueue(object : Callback<ErrorResponse>{
            override fun onResponse(call: Call<ErrorResponse>, response: Response<ErrorResponse>) {
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            uploadResult.value = Result.Success(responseBody)
                        }
                    }
                    else {
                        throw HttpException(response)
                    }

                } catch (e: HttpException) {
                    val jsonInString = e.response()?.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    val errorMessage = errorBody.message
                    resultLogin.value = Result.Error(errorMessage!!)
                }
            }

            override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
                uploadResult.value = Result.Error(t.message.toString())
            }

        })
        return uploadResult
    }

    fun login(email: String, password: String): LiveData<Result<String>> {
        resultLogin.value = Result.Loading
        apiService.login(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                try {
                    if (response.isSuccessful) {

                        val responseBody = response.body()
                        if (responseBody != null) {
                            resultLogin.value = Result.Success(responseBody.loginResult?.token!!)
                        }

                    }
                    else {
                        throw HttpException(response)
                    }
                } catch (e: HttpException) {
                    val jsonInString = e.response()?.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    val errorMessage = errorBody.message
                    resultLogin.value = Result.Error(errorMessage!!)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                resultLogin.value = Result.Error(t.message.toString())
            }


        })

        return resultLogin
    }
    fun register(name: String, email: String, password : String): LiveData<Result<RegisterResponse>> {
        registerResult.value = Result.Loading
        apiService.register(name, email, password).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>,
            ) {
                try {
                    if(response.isSuccessful) {
                        val responseBody = response.body()!!
                        registerResult.value = Result.Success(responseBody)
                    } else throw HttpException(response)
                } catch (e: HttpException) {
                    val jsonInString = e.response()?.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    val errorMessage = errorBody.message
                    registerResult.value = Result.Error(errorMessage!!)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                registerResult.value = Result.Error(t.message.toString())
            }

        })
        return registerResult
    }

    suspend fun saveSession(token: String) {
        userPreference.saveSession(token)
    }

    fun getSession(): Flow<String> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

     fun getAllStories(): LiveData<Result<List<ListStoryItem>>> {
        allStoriesResult.value = Result.Loading
        apiService.getStories().enqueue(object: Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                try {
                    if (response.isSuccessful) {
                        val story = response.body()?.listStory as List<ListStoryItem>
                        allStoriesResult.value  = Result.Success(story)
                        Log.d("Response", story.toString())
                    } else {
                        throw HttpException(response)
                    }
                } catch (e: HttpException) {
                    val jsonInString = e.response()?.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, StoryResponse::class.java)
                    val errorMessage = errorBody.message
                    allStoriesResult.value = Result.Error(errorMessage.toString())
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                allStoriesResult.value = Result.Error(t.message.toString())
            }

        })
        return allStoriesResult
    }

    fun getStoryDetail(id: String): LiveData<Result<ListStoryItem>> {
        detailStoryResult.value = Result.Loading
        apiService.getDetailStory(id).enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                try {
                    if (response.isSuccessful) {
                        val storyDetail = response.body()?.story as ListStoryItem
                        detailStoryResult.value  = Result.Success(storyDetail)
                        Log.d("Response", storyDetail.toString())
                    } else {
                        throw HttpException(response)
                    }
                } catch (e: HttpException) {
                    val jsonInString = e.response()?.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, StoryResponse::class.java)
                    val errorMessage = errorBody.message
                    detailStoryResult.value = Result.Error(errorMessage.toString())
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                detailStoryResult.value = Result.Error(t.message.toString())
            }


        })
        return detailStoryResult
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
    }
}