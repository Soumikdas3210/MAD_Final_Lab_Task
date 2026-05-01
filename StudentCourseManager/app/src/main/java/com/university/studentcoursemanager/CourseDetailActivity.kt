package com.university.studentcoursemanager

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CourseDetailActivity : AppCompatActivity() {

    private lateinit var tvCourseName: TextView
    private lateinit var tvCourseCode: TextView
    private lateinit var tvInstructor: TextView
    private lateinit var tvCredits: TextView
    private lateinit var tvSchedule: TextView
    private lateinit var tvRoom: TextView
    private lateinit var tvSemester: TextView

    private lateinit var fabEdit: FloatingActionButton

    private lateinit var course: Course

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_course_detail)
        supportActionBar?.title = "Course Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Receive data
        course = intent.getSerializableExtra("course") as Course

        // Views
        tvCourseName = findViewById(R.id.tvCourseName)
        tvCourseCode = findViewById(R.id.tvCourseCode)
        tvInstructor = findViewById(R.id.tvInstructor)
        tvCredits = findViewById(R.id.tvCredits)
        tvSchedule = findViewById(R.id.tvSchedule)
        tvRoom = findViewById(R.id.tvRoom)
        tvSemester = findViewById(R.id.tvSemester)
        fabEdit = findViewById(R.id.fabEdit)

        // Set data
        tvCourseName.text = course.courseName
        tvCourseCode.text = "Course Code: ${course.courseCode}"
        tvInstructor.text = "Instructor: ${course.instructor}"
        tvCredits.text = "Credit Hours: ${course.creditHours}"
        tvSchedule.text = "Schedule: ${course.schedule}"
        tvRoom.text = "Room: ${course.room}"
        tvSemester.text = "Semester: ${course.semester}"

        // Edit Button
        fabEdit.setOnClickListener {
            val intent = Intent(
                this,
                EditCourseActivity::class.java
            )

            intent.putExtra(
                "course",
                course
            )

            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}