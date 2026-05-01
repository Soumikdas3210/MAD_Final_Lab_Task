package com.university.studentcoursemanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.view.Menu
import androidx.appcompat.widget.SearchView
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyLayout: LinearLayout
    private lateinit var fabAdd: FloatingActionButton

    private lateinit var courseList: ArrayList<Course>
    private lateinit var adapter: CourseAdapter

    private lateinit var databaseRef: DatabaseReference

    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Views
        recyclerView = findViewById(R.id.recyclerViewCourses)
        emptyLayout = findViewById(R.id.emptyLayout)
        fabAdd = findViewById(R.id.fabAddCourse)

        // Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("Courses")

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup list
        courseList = ArrayList()

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CourseAdapter(courseList) { course ->
            deleteCourse(course)
        }

        recyclerView.adapter = adapter

        // Add course button
        fabAdd.setOnClickListener {
            startActivity(
                Intent(this, AddCourseActivity::class.java)
            )
        }

        // Load data
        loadCourses()
    }

    private fun loadCourses() {

        databaseRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                courseList.clear()
                for (data in snapshot.children) {
                    val course = data.getValue(Course::class.java)
                    if (course != null) {
                        course.id = data.key.toString()
                        courseList.add(course)
                    }
                }

                adapter.updateList(courseList)

                if (courseList.isEmpty()) {
                    emptyLayout.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyLayout.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    error.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
            R.menu.menu_search,
            menu
        )

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Search course..."

        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter(newText ?: "")
                    return true
                }
            })

        return true
    }

    private fun deleteCourse(course: Course) {

        databaseRef.child(course.id).removeValue().addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Course deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}