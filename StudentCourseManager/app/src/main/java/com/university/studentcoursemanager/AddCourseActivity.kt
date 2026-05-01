package com.university.studentcoursemanager

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class AddCourseActivity : AppCompatActivity() {

    private lateinit var etCourseName: EditText
    private lateinit var etCourseCode: EditText
    private lateinit var etInstructor: EditText
    private lateinit var etSchedule: EditText
    private lateinit var etRoom: EditText

    private lateinit var spinnerCredits: Spinner
    private lateinit var spinnerSemester: Spinner

    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_course)

        supportActionBar?.title = "Add Course"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize views

        etCourseName = findViewById(R.id.etCourseName)
        etCourseCode = findViewById(R.id.etCourseCode)
        etInstructor = findViewById(R.id.etInstructor)
        etSchedule = findViewById(R.id.etSchedule)
        etRoom = findViewById(R.id.etRoom)

        spinnerCredits = findViewById(R.id.spinnerCredits)
        spinnerSemester = findViewById(R.id.spinnerSemester)

        btnSave = findViewById(R.id.btnSaveCourse)
        btnCancel = findViewById(R.id.btnCancel)

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

        // Save Button

        btnSave.setOnClickListener {

            saveCourse()
        }

        // Cancel Button

        btnCancel.setOnClickListener {

            finish()
        }
    }

    private fun saveCourse() {

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

        val databaseRef = FirebaseDatabase.getInstance().getReference("Courses")

        val courseId = databaseRef.push().key!!

        val course = Course(

            courseId,
            courseName,
            courseCode,
            instructor,
            credits,
            schedule,
            room,
            semester
        )

        databaseRef.child(courseId).setValue(course).addOnSuccessListener {

                progressBar.visibility = View.GONE

                Toast.makeText(
                    this,
                    "Course added successfully",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }.addOnFailureListener {

                progressBar.visibility = View.GONE

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