package com.example.mypractice.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mypractice.R
import com.example.mypractice.model.ExerciseModel

class ExerciseAdapter (private val context: Context, private val exercises: List<ExerciseModel>) : BaseAdapter() {
    override fun getCount():Int{
        return exercises.size
    }

    override fun getItem(position: Int): Any{
        return exercises[position]
    }

    override fun getItemId(position:Int):Long{
        return exercises[position].id.toLong()
    }

    //returning a view that displays the data for the position in the data set
    override fun getView(position:Int, convertView: View?, parent: ViewGroup?): View{
        var itemView = convertView
        val holder: ExerciseViewHolder

        //if the view is not recycled, inflate layout
        if (itemView == null)
        {
            itemView = LayoutInflater.from(context).inflate(R.layout.exercise_card, parent, false)
            holder = ExerciseViewHolder()
            holder.tvTitle = itemView.findViewById(R.id.tvTitle)
            holder.tvDescription = itemView.findViewById(R.id.tvDescription)
            holder.tvSetsReps = itemView.findViewById(R.id.tvSetsReps)
            itemView.tag = holder

        }
        else{
            holder = itemView.tag as ExerciseViewHolder
        }

        //get data for exercise in this pos
        val exercise = exercises[position]

        //bind data to views
        holder.tvTitle.text= exercise.name
        holder.tvDescription.text = exercise.description
        holder.tvSetsReps.text = "${exercise.sets} sets x ${exercise.reps} reps"
        return itemView!!
    }


}