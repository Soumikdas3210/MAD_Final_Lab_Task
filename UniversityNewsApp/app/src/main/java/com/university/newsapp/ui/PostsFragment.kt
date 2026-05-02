package com.university.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.university.newsapp.R
import com.university.newsapp.model.Post
import com.university.newsapp.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class PostsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    private lateinit var adapter: PostAdapter

    private var allPosts = listOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_posts, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)
        retryButton = view.findViewById(R.id.retryButton)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        searchView = view.findViewById(R.id.searchView)

        adapter = PostAdapter { post ->
            val intent = Intent(requireContext(), PostDetailActivity::class.java)
            intent.putExtra("POST_ID", post.id)
            intent.putExtra("USER_ID", post.userId)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        swipeRefresh.setOnRefreshListener { loadPosts() }
        retryButton.setOnClickListener { loadPosts() }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterPosts(newText ?: "")
                return true
            }
        })

        loadPosts()
    }

    private fun loadPosts() {
        if (!swipeRefresh.isRefreshing) {
            progressBar.visibility = View.VISIBLE
            swipeRefresh.visibility = View.GONE
            errorText.visibility = View.GONE
            retryButton.visibility = View.GONE
        }

        lifecycleScope.launch {
            try {
                val posts = RetrofitClient.instance.getAllPosts()
                allPosts = posts
                progressBar.visibility = View.GONE
                swipeRefresh.visibility = View.VISIBLE
                swipeRefresh.isRefreshing = false
                errorText.visibility = View.GONE
                retryButton.visibility = View.GONE
                adapter.submitList(posts)
            } catch (e: HttpException) {
                swipeRefresh.isRefreshing = false
                showError("Server error: ${e.code()}")
            } catch (e: IOException) {
                swipeRefresh.isRefreshing = false
                showError("Network error. Check your connection.")
            } catch (e: Exception) {
                swipeRefresh.isRefreshing = false
                showError("Something went wrong: ${e.message}")
            }
        }
    }

    private fun filterPosts(query: String) {
        val filtered = if (query.isEmpty()) allPosts
        else allPosts.filter { it.title.contains(query, ignoreCase = true) }
        adapter.submitList(filtered)
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        swipeRefresh.visibility = View.GONE
        errorText.visibility = View.VISIBLE
        errorText.text = message
        retryButton.visibility = View.VISIBLE
    }
}
