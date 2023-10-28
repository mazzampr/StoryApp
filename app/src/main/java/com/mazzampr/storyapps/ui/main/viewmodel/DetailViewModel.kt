package com.mazzampr.storyapps.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mazzampr.storyapps.data.StoryRepository

class DetailViewModel(private val repository: StoryRepository): ViewModel() {

    fun getSession(): LiveData<String> {
        return repository.getSession().asLiveData()
    }
    fun getDetailStory(token: String, id: String) = repository.getStoryDetail(token, id)
}