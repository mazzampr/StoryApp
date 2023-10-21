package com.mazzampr.storyapps.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mazzampr.storyapps.data.StoryRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {

    fun login(email: String, password: String) = repository.login(email, password)

    fun saveSession(token: String) {
        viewModelScope.launch {
            repository.saveSession(token)
        }
    }
}