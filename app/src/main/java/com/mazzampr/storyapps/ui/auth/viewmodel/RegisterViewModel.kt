package com.mazzampr.storyapps.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.mazzampr.storyapps.data.StoryRepository

class RegisterViewModel(private val repository: StoryRepository): ViewModel() {

    fun register(name: String, email: String, password : String) = repository.register(name, email, password)

}