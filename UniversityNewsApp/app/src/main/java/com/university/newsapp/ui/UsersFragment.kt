package com.university.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.university.newsapp.R
import com.university.newsapp.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class UsersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button
    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_users, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)
        retryButton = view.findViewById(R.id.retryButton)

        adapter = UserAdapter { user ->
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            intent.putExtra("USER_ID", user.id)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        retryButton.setOnClickListener { loadUsers() }

        loadUsers()
    }

    private fun loadUsers() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        errorText.visibility = View.GONE
        retryButton.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val users = RetrofitClient.instance.getAllUsers()
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(users)
            } catch (e: HttpException) {
                showError("Server error: ${e.code()}")
            } catch (e: IOException) {
                showError("Network error. Check your connection.")
            } catch (e: Exception) {
                showError("Something went wrong: ${e.message}")
            }
        }
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        errorText.visibility = View.VISIBLE
        errorText.text = message
        retryButton.visibility = View.VISIBLE
    }
}
