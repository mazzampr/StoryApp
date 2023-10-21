package com.mazzampr.storyapps.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mazzampr.storyapps.data.StoryRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: StoryRepository): ViewModel() {

     fun getSession(): LiveData<String> {
          return repository.getSession().asLiveData()
     }

     fun logout() {
          viewModelScope.launch {
               repository.logout()
          }
     }

     fun getAllStories() = repository.getAllStories()
}