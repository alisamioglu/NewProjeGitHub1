package com.samioglu.newc

// PostManager.kt
class PostManager {
    private val posts = mutableListOf<Postt>() // Postt olarak değiştirildi

    fun addPost(content: String) {
        val post = Postt(content) // Postt olarak değiştirildi
        posts.add(post)
    }

    fun getAllPosts(): List<Postt> { // Postt olarak değiştirildi
        return posts.toList()
    }
}


