package com.mazzampr.storyapps.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class UserPreferenceTest{
    private lateinit var userPreference: UserPreference
    private val dataStore: DataStore<Preferences> = Mockito.mock(DataStore::class.java) as DataStore<Preferences>
    private val tokenKey = stringPreferencesKey("token")

    @Before
    fun setUp() {
        userPreference = UserPreference.getInstance(dataStore)
    }

    @Test
    fun testSaveSession() = runBlocking {
        val expectedToken = "token-dummy"

        userPreference.saveSession(expectedToken)

        val savedToken = userPreference.getSession().first()

        assertEquals(expectedToken, savedToken)
    }

    @Test
    fun testLogout() = runBlocking {
        val token = "existingToken"
        val preferencesFlow: Flow<Preferences> = flow {
            val preferences = dataStore.data.first()
            val newPreferences = preferences.toMutablePreferences().apply {
                set(tokenKey, token)
            }
            emit(newPreferences)
        }
        Mockito.`when`(dataStore.data).thenReturn(preferencesFlow)
        // Perform logout
        userPreference.logout()

        val tokenAfterLogout = userPreference.getSession().first()

        assertEquals("", tokenAfterLogout)
    }
}