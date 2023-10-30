package com.mazzampr.storyapps.ui.auth.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MediatorLiveData
import com.mazzampr.storyapps.DataDummy
import com.mazzampr.storyapps.data.Result
import com.mazzampr.storyapps.data.StoryRepository
import com.mazzampr.storyapps.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUpViewModel() {
        loginViewModel = LoginViewModel(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Login Should Not Null and Return Success Messages`() = runTest {
        val dummyLogin = DataDummy.generateDummyLoginResponse()
        val response = MediatorLiveData<Result<String>>()
        response.value = Result.Success(dummyLogin.loginResult!!.token!!)


        val email = "abc@gmail.com"
        val password = "password"

        Mockito.`when`(repository.login(email, password)).thenReturn(response)

        val actualResponse = loginViewModel.login(email, password).getOrAwaitValue()
        Mockito.verify(repository).login(email, password)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
    }
}