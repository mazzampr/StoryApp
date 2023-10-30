package com.mazzampr.storyapps

import com.mazzampr.storyapps.data.remote.response.ListStoryItem
import com.mazzampr.storyapps.data.remote.response.LoginResponse
import com.mazzampr.storyapps.data.remote.response.LoginResult

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = "id_$i",
                name = "Name $i",
                description = "Description $i",
                photoUrl = "https://core.api.efishery.com/image/p/q100;/https://efishery.com/images/misi-global-hifi-1.png",
                createdAt = "2021-01-22T22:22:22Z",
                lat = i.toDouble() * 5,
                lon = i.toDouble() * 10
            )
            items.add(story)
        }
        return items
    }


    fun generateDummyLoginResponse(): LoginResponse {
        return LoginResponse(
            error = false,
            message = "success",
            loginResult = LoginResult(
                userId = "user-198239-khi",
                name = "Sule andre",
                token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLVVEVzhnc1R2aHl2V3VBUjIiLCJpYXQiOjE2OTc0Mzc2Mjl9.5Aw7hLrdzJ7NHKU8e6UKjNnw2XlRwLq_21C5pof1TBQ"
            )
        )
    }
}