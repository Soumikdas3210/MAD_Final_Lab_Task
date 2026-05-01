package com.university.studentcoursemanager

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class CourseAdapter(

    private var list: ArrayList<Course>,
    private val onDelete: (Course) -> Unit

) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private var filteredList = ArrayList<Course>()

    init {
        filteredList = list
    }

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_course,
                parent,
                false
            )

        return CourseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = filteredList[position]

        holder.itemView.findViewById<TextView>(R.id.tvCourseName).text = course.courseName
        holder.itemView.findViewById<TextView>(R.id.tvCourseCode).text = "Code: ${course.courseCode}"
        holder.itemView.findViewById<TextView>(R.id.tvInstructor).text = "Instructor: ${course.instructor}"
        holder.itemView.findViewById<TextView>(R.id.tvCredits).text = "Credits: ${course.creditHours}"
        holder.itemView.findViewById<TextView>(R.id.tvSchedule).text = course.schedule

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, CourseDetailActivity::class.java)
            intent.putExtra("course", course)

            holder.itemView.context.startActivity(intent)
        }

        // Edit
        holder.itemView.findViewById<ImageButton>(R.id.btnEdit).setOnClickListener {
            val intent = Intent(holder.itemView.context, EditCourseActivity::class.java)
            intent.putExtra("course", course)
            holder.itemView.context.startActivity(intent)
        }

        // Delete
        holder.itemView.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener {
            onDelete(course)
        }
    }

    // Search Filter

    fun filter(query: String) {
        filteredList = ArrayList()

        if(query.isEmpty()){
            filteredList.addAll(list)

        } else {

            val searchText = query.lowercase(Locale.getDefault())
            for(course in list){

                if(course.courseName.lowercase(Locale.getDefault()).contains(searchText) ||
                    course.courseCode.lowercase(
                        Locale.getDefault()
                    ).contains(searchText)
                ){
                    filteredList.add(course)
                }
            }
        }

        notifyDataSetChanged()
    }

    fun updateList(newList: ArrayList<Course>){

        list = newList
        filteredList = newList

        notifyDataSetChanged()
    }
}