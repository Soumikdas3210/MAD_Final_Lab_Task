package com.university.newsapp.repository

import com.university.newsapp.model.Comment
import com.university.newsapp.model.Post
import com.university.newsapp.model.User
import com.university.newsapp.network.RetrofitClient

class PostRepository {
    private val api = RetrofitClient.instance

    suspend fun getAllPosts(): List<Post> = api.getAllPosts()
    suspend fun getPostById(id: Int): Post = api.getPostById(id)
    suspend fun getCommentsByPost(postId: Int): List<Comment> = api.getCommentsByPost(postId)
    suspend fun getAllUsers(): List<User> = api.getAllUsers()
    suspend fun getUserById(id: Int): User = api.getUserById(id)
    suspend fun getPostsByUser(userId: Int): List<Post> = api.getPostsByUser(userId)
}
