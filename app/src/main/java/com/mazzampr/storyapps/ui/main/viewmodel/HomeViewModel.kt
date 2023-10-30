package com.mazzampr.storyapps.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
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

//     fun getAllStories(token: String,) = repository.getAllStories(token)
//     val story: LiveData<PagingData<StoryResponse>> =
//          repository.getAllStories().cachedIn(viewModelScope)

     fun getStories(token: String) = repository.getAllStories(token).cachedIn(viewModelScope)
}