package com.university.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.university.newsapp.R
import com.university.newsapp.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class PostDetailActivity : AppCompatActivity() {

    private var postId: Int = -1
    private var userId: Int = -1

    private lateinit var postProgressBar: ProgressBar
    private lateinit var postContent: LinearLayout
    private lateinit var postErrorText: TextView
    private lateinit var postRetryButton: Button
    private lateinit var postTitle: TextView
    private lateinit var postBody: TextView

    private lateinit var authorProgressBar: ProgressBar
    private lateinit var authorCard: CardView
    private lateinit var authorErrorText: TextView
    private lateinit var authorName: TextView
    private lateinit var authorEmail: TextView
    private lateinit var authorCompany: TextView

    private lateinit var commentsProgressBar: ProgressBar
    private lateinit var commentsContainer: LinearLayout
    private lateinit var commentsErrorText: TextView
    private lateinit var commentsRetryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        postId = intent.getIntExtra("POST_ID", -1)
        userId = intent.getIntExtra("USER_ID", -1)

        postProgressBar = findViewById(R.id.postProgressBar)
        postContent = findViewById(R.id.postContent)
        postErrorText = findViewById(R.id.postErrorText)
        postRetryButton = findViewById(R.id.postRetryButton)
        postTitle = findViewById(R.id.postTitle)
        postBody = findViewById(R.id.postBody)

        authorProgressBar = findViewById(R.id.authorProgressBar)
        authorCard = findViewById(R.id.authorCard)
        authorErrorText = findViewById(R.id.authorErrorText)
        authorName = findViewById(R.id.authorName)
        authorEmail = findViewById(R.id.authorEmail)
        authorCompany = findViewById(R.id.authorCompany)

        commentsProgressBar = findViewById(R.id.commentsProgressBar)
        commentsContainer = findViewById(R.id.commentsContainer)
        commentsErrorText = findViewById(R.id.commentsErrorText)
        commentsRetryButton = findViewById(R.id.commentsRetryButton)

        postRetryButton.setOnClickListener { loadPost() }
        commentsRetryButton.setOnClickListener { loadComments() }

        if (postId != -1) {
            loadPost()
            loadComments()
        }
        if (userId != -1) {
            loadAuthor()
        }
    }

    private fun loadPost() {
        postProgressBar.visibility = View.VISIBLE
        postContent.visibility = View.GONE
        postErrorText.visibility = View.GONE
        postRetryButton.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val post = RetrofitClient.instance.getPostById(postId)
                postProgressBar.visibility = View.GONE
                postContent.visibility = View.VISIBLE
                postTitle.text = post.title
                postBody.text = post.body
            } catch (e: HttpException) {
                showPostError("Server error: ${e.code()}")
            } catch (e: IOException) {
                showPostError("Network error. Check your connection.")
            } catch (e: Exception) {
                showPostError("Something went wrong: ${e.message}")
            }
        }
    }

    private fun loadAuthor() {
        authorProgressBar.visibility = View.VISIBLE
        authorCard.visibility = View.GONE
        authorErrorText.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val user = RetrofitClient.instance.getUserById(userId)
                authorProgressBar.visibility = View.GONE
                authorCard.visibility = View.VISIBLE
                authorName.text = user.name
                authorEmail.text = user.email
                authorCompany.text = user.company.name
                authorCard.setOnClickListener {
                    val intent = Intent(this@PostDetailActivity, UserProfileActivity::class.java)
                    intent.putExtra("USER_ID", user.id)
                    startActivity(intent)
                }
            } catch (e: HttpException) {
                authorProgressBar.visibility = View.GONE
                authorErrorText.visibility = View.VISIBLE
                authorErrorText.text = "Server error: ${e.code()}"
            } catch (e: IOException) {
                authorProgressBar.visibility = View.GONE
                authorErrorText.visibility = View.VISIBLE
                authorErrorText.text = "Network error. Check your connection."
            } catch (e: Exception) {
                authorProgressBar.visibility = View.GONE
                authorErrorText.visibility = View.VISIBLE
                authorErrorText.text = "Something went wrong: ${e.message}"
            }
        }
    }

    private fun loadComments() {
        commentsProgressBar.visibility = View.VISIBLE
        commentsContainer.visibility = View.GONE
        commentsErrorText.visibility = View.GONE
        commentsRetryButton.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val comments = RetrofitClient.instance.getCommentsByPost(postId)
                commentsProgressBar.visibility = View.GONE
                commentsContainer.visibility = View.VISIBLE
                commentsContainer.removeAllViews()
                val inflater = LayoutInflater.from(this@PostDetailActivity)
                for (comment in comments) {
                    val v = inflater.inflate(R.layout.item_comment, commentsContainer, false)
                    v.findViewById<TextView>(R.id.commentName).text = comment.name
                    v.findViewById<TextView>(R.id.commentEmail).text = comment.email
                    v.findViewById<TextView>(R.id.commentBody).text = comment.body
                    commentsContainer.addView(v)
                }
            } catch (e: HttpException) {
                showCommentsError("Server error: ${e.code()}")
            } catch (e: IOException) {
                showCommentsError("Network error. Check your connection.")
            } catch (e: Exception) {
                showCommentsError("Something went wrong: ${e.message}")
            }
        }
    }

    private fun showPostError(message: String) {
        postProgressBar.visibility = View.GONE
        postErrorText.visibility = View.VISIBLE
        postErrorText.text = message
        postRetryButton.visibility = View.VISIBLE
    }

    private fun showCommentsError(message: String) {
        commentsProgressBar.visibility = View.GONE
        commentsErrorText.visibility = View.VISIBLE
        commentsErrorText.text = message
        commentsRetryButton.visibility = View.VISIBLE
    }
}
