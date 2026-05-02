package com.university.newsapp.ui

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.university.newsapp.R
import com.university.newsapp.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class UserProfileActivity : AppCompatActivity() {

    private var userId: Int = -1

    private lateinit var profileProgressBar: ProgressBar
    private lateinit var profileContent: LinearLayout
    private lateinit var profileErrorText: TextView
    private lateinit var profileRetryButton: Button

    private lateinit var avatarCircle: TextView
    private lateinit var userName: TextView
    private lateinit var userUsername: TextView
    private lateinit var userEmail: TextView
    private lateinit var userPhone: TextView
    private lateinit var userWebsite: TextView
    private lateinit var userCompany: TextView
    private lateinit var userCatchphrase: TextView

    private lateinit var postsProgressBar: ProgressBar
    private lateinit var postsContainer: LinearLayout
    private lateinit var postsErrorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        userId = intent.getIntExtra("USER_ID", -1)

        profileProgressBar = findViewById(R.id.profileProgressBar)
        profileContent = findViewById(R.id.profileContent)
        profileErrorText = findViewById(R.id.profileErrorText)
        profileRetryButton = findViewById(R.id.profileRetryButton)

        avatarCircle = findViewById(R.id.avatarCircle)
        userName = findViewById(R.id.userName)
        userUsername = findViewById(R.id.userUsername)
        userEmail = findViewById(R.id.userEmail)
        userPhone = findViewById(R.id.userPhone)
        userWebsite = findViewById(R.id.userWebsite)
        userCompany = findViewById(R.id.userCompany)
        userCatchphrase = findViewById(R.id.userCatchphrase)

        postsProgressBar = findViewById(R.id.postsProgressBar)
        postsContainer = findViewById(R.id.postsContainer)
        postsErrorText = findViewById(R.id.postsErrorText)

        profileRetryButton.setOnClickListener { loadUser() }

        if (userId != -1) {
            loadUser()
            loadUserPosts()
        }
    }

    private fun loadUser() {
        profileProgressBar.visibility = View.VISIBLE
        profileContent.visibility = View.GONE
        profileErrorText.visibility = View.GONE
        profileRetryButton.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val user = RetrofitClient.instance.getUserById(userId)
                profileProgressBar.visibility = View.GONE
                profileContent.visibility = View.VISIBLE

                avatarCircle.text = getInitials(user.name)
                val drawable = GradientDrawable()
                drawable.shape = GradientDrawable.OVAL
                drawable.setColor(getAvatarColor(user.name))
                avatarCircle.background = drawable

                userName.text = user.name
                userUsername.text = "@${user.username}"
                userEmail.text = user.email
                userPhone.text = user.phone
                userWebsite.text = user.website
                userCompany.text = user.company.name
                userCatchphrase.text = "\"${user.company.catchPhrase}\""
            } catch (e: HttpException) {
                showProfileError("Server error: ${e.code()}")
            } catch (e: IOException) {
                showProfileError("Network error. Check your connection.")
            } catch (e: Exception) {
                showProfileError("Something went wrong: ${e.message}")
            }
        }
    }

    private fun loadUserPosts() {
        postsProgressBar.visibility = View.VISIBLE
        postsContainer.visibility = View.GONE
        postsErrorText.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val posts = RetrofitClient.instance.getPostsByUser(userId)
                postsProgressBar.visibility = View.GONE
                postsContainer.visibility = View.VISIBLE
                postsContainer.removeAllViews()
                val inflater = LayoutInflater.from(this@UserProfileActivity)
                for (post in posts) {
                    val v = inflater.inflate(R.layout.item_mini_post, postsContainer, false)
                    v.findViewById<TextView>(R.id.miniPostTitle).text = post.title
                    v.setOnClickListener {
                        val intent = Intent(this@UserProfileActivity, PostDetailActivity::class.java)
                        intent.putExtra("POST_ID", post.id)
                        intent.putExtra("USER_ID", post.userId)
                        startActivity(intent)
                    }
                    postsContainer.addView(v)
                }
            } catch (e: HttpException) {
                postsProgressBar.visibility = View.GONE
                postsErrorText.visibility = View.VISIBLE
                postsErrorText.text = "Server error: ${e.code()}"
            } catch (e: IOException) {
                postsProgressBar.visibility = View.GONE
                postsErrorText.visibility = View.VISIBLE
                postsErrorText.text = "Network error. Check your connection."
            } catch (e: Exception) {
                postsProgressBar.visibility = View.GONE
                postsErrorText.visibility = View.VISIBLE
                postsErrorText.text = "Something went wrong: ${e.message}"
            }
        }
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return if (parts.size >= 2) "${parts[0][0]}${parts[1][0]}".uppercase()
        else if (parts.isNotEmpty() && parts[0].isNotEmpty()) "${parts[0][0]}".uppercase()
        else "?"
    }

    private fun getAvatarColor(name: String): Int {
        val colors = intArrayOf(
            0xFFE91E63.toInt(),
            0xFF9C27B0.toInt(),
            0xFF3F51B5.toInt(),
            0xFF2196F3.toInt(),
            0xFF009688.toInt(),
            0xFF4CAF50.toInt(),
            0xFFFF5722.toInt(),
            0xFF795548.toInt()
        )
        return colors[name.length % colors.size]
    }

    private fun showProfileError(message: String) {
        profileProgressBar.visibility = View.GONE
        profileErrorText.visibility = View.VISIBLE
        profileErrorText.text = message
        profileRetryButton.visibility = View.VISIBLE
    }
}
