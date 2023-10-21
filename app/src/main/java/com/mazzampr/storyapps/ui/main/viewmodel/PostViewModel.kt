package com.mazzampr.storyapps.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import com.mazzampr.storyapps.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostViewModel(private val repository: StoryRepository): ViewModel() {

    fun uploadStory(image: MultipartBody.Part, desc: RequestBody) = repository.uploadStory(image, desc)
}