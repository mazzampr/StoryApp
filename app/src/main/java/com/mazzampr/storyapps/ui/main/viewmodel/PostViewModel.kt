package com.mazzampr.storyapps.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mazzampr.storyapps.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostViewModel(private val repository: StoryRepository): ViewModel() {

    fun getSession(): LiveData<String> {
        return repository.getSession().asLiveData()
    }
    fun uploadStory(token: String, image: MultipartBody.Part, desc: RequestBody) = repository.uploadStory(token, image, desc)
}