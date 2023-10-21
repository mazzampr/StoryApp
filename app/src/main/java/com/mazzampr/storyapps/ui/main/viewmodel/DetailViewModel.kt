package com.mazzampr.storyapps.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import com.mazzampr.storyapps.data.StoryRepository

class DetailViewModel(private val repository: StoryRepository): ViewModel() {

    fun getDetailStory(id: String) = repository.getStoryDetail(id)
}