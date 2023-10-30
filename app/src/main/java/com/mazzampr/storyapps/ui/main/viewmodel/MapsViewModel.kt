package com.mazzampr.storyapps.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mazzampr.storyapps.data.StoryRepository

class MapsViewModel(private val repository: StoryRepository): ViewModel() {

     fun getSession(): LiveData<String> {
          return repository.getSession().asLiveData()
     }

     fun getStoryFromDb() = repository.getStoryFromDb()
}