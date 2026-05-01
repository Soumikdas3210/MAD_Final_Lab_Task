package com.university.studentcoursemanager

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class EditCourseActivity : AppCompatActivity() {
    private lateinit var etCourseName: EditText
    private lateinit var etCourseCode: EditText
    private lateinit var etInstructor: EditText
    private lateinit var etSchedule: EditText
    private lateinit var etRoom: EditText

    private lateinit var spinnerCredits: Spinner
    private lateinit var spinnerSemester: Spinner

    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    private lateinit var progressBar: ProgressBar

    private lateinit var course: Course

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_course)

        supportActionBar?.title = "Edit Course"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Receive data
        course = intent.getSerializableExtra("course") as Course

        // Views
        etCourseName = findViewById(R.id.etCourseName)
        etCourseCode = findViewById(R.id.etCourseCode)
        etInstructor = findViewById(R.id.etInstructor)
        etSchedule = findViewById(R.id.etSchedule)
        etRoom = findViewById(R.id.etRoom)
        spinnerCredits = findViewById(R.id.spinnerCredits)
        spinnerSemester = findViewById(R.id.spinnerSemester)
        btnUpdate = findViewById(R.id.btnUpdateCourse)
        btnDelete = findViewById(R.id.btnDeleteCourse)
        progressBar = findViewById(R.id.progressBar)

        // Spinner Data
        val credits = arrayOf(
            "1",
            "2",
            "3",
            "4"
        )

        spinnerCredits.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            credits
        )

        val semesters = arrayOf(
            "Spring 2025",
            "Summer 2025",
            "Fall 2025"
        )

        spinnerSemester.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            semesters
        )

        // Prefill Data

        etCourseName.setText(course.courseName)
        etCourseCode.setText(course.courseCode)
        etInstructor.setText(course.instructor)
        etSchedule.setText(course.schedule)
        etRoom.setText(course.room)

        spinnerCredits.setSelection(
            credits.indexOf(course.creditHours)
        )

        spinnerSemester.setSelection(
            semesters.indexOf(course.semester)
        )

        // Update
        btnUpdate.setOnClickListener {

            updateCourse()
        }

        // Delete
        btnDelete.setOnClickListener {

            showDeleteDialog()
        }
    }

    private fun updateCourse() {

        val courseName = etCourseName.text.toString().trim()
        val courseCode = etCourseCode.text.toString().trim()
        val instructor = etInstructor.text.toString().trim()
        val schedule = etSchedule.text.toString().trim()
        val room = etRoom.text.toString().trim()
        val credits = spinnerCredits.selectedItem.toString()
        val semester = spinnerSemester.selectedItem.toString()

        // Validation
        if(courseName.isEmpty()){
            etCourseName.error = "Required"
            return
        }

        if(courseCode.isEmpty()){
            etCourseCode.error = "Required"
            return
        }

        if(instructor.isEmpty()){
            etInstructor.error = "Required"
            return
        }

        progressBar.visibility = View.VISIBLE

        val updatedCourse = Course(
            course.id,
            courseName,
            courseCode,
            instructor,
            credits,
            schedule,
            room,
            semester
        )

        FirebaseDatabase.getInstance()
            .getReference("Courses")
            .child(course.id)
            .setValue(updatedCourse)

            .addOnSuccessListener {

                progressBar.visibility = View.GONE

                Toast.makeText(
                    this,
                    "Course updated successfully",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }

            .addOnFailureListener {

                progressBar.visibility = View.GONE

                Toast.makeText(
                    this,
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun showDeleteDialog() {

        AlertDialog.Builder(this)
            .setTitle("Delete Course")
            .setMessage(
                "Are you sure you want to delete this course?"
            )

            .setPositiveButton("Yes") { _, _ ->
                deleteCourse()
            }

            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteCourse() {

        FirebaseDatabase.getInstance()
            .getReference("Courses")
            .child(course.id)
            .removeValue()

            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Course deleted successfully",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }

            .addOnFailureListener {
                Toast.makeText(
                    this,
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}