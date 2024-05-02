package com.example.mypractice.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.mypractice.R
import com.example.mypractice.model.ExerciseModel

class ExerciseAdapter (private val context: Context,
                       private val exercises: List<ExerciseModel>,
                       private val listener: ExerciseAdapterListener ) : BaseAdapter() {
    private var isSelectionMode = false
    private val selectedItems = mutableSetOf<Int>()
    //used to notify the fragment of item click events

    private class ExerciseViewHolder {
        lateinit var tvTitle: TextView
        lateinit var tvDescription: TextView
        lateinit var tvSetsReps: TextView
        lateinit var cbSelect: CheckBox
        lateinit var coloredRectangle: TextView
    }
    override fun getCount():Int{
        return exercises.size
    }

    override fun getItem(position: Int): Any{
        return exercises[position]
    }

    override fun getItemId(position:Int): Long {
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
            holder.coloredRectangle=itemView.findViewById(R.id.coloredRectangle)
            holder.tvTitle = itemView.findViewById(R.id.tvTitle)
            holder.tvDescription = itemView.findViewById(R.id.tvDescription)
            holder.tvSetsReps = itemView.findViewById(R.id.tvSetsReps)
            holder.cbSelect = itemView.findViewById(R.id.cbSelect)
            itemView.tag = holder

        }
        else{
            holder = itemView.tag as ExerciseViewHolder
        }

        //get data for exercise in this pos
        val exercise = exercises[position]

        //bind data to views
        //TODO decide on colors
        if(exercise.retired==true){
        holder.coloredRectangle.setBackgroundResource(R.color.friday)}
        else{holder.coloredRectangle.setBackgroundResource(R.color.saturday)}
        holder.tvTitle.text= exercise.name
        holder.tvDescription.text = exercise.description.take(40) + if (exercise.description.length > 40) "..." else ""
        holder.tvSetsReps.text = "${exercise.sets} sets x ${exercise.reps} reps"

        //setting visibility of selection checkbox
        holder.cbSelect.visibility = if (isSelectionMode)View.VISIBLE else View.GONE
        holder.cbSelect.isChecked = isItemSelected(position)


        itemView?.setOnClickListener{
            if(isSelectionMode)
            {
                listener.onItemSelected(position)
            }
            else{ //TODO if an item is clicked to get more details
            }
        }

        itemView?.setOnLongClickListener{
            if (!isSelectionMode) {
                Log.d("Exercises", "long hold registered")
                listener.onItemLongClick(position)
            }
            true
        }


        return itemView!!
    }

    //to toggle between selection mode and normal
    fun toggleSelectionMode(){
        isSelectionMode= !isSelectionMode
        Log.d("Selection", "adapter reached")
        notifyDataSetChanged()
    }
    fun setSelectionMode(mode: Boolean)
    {
        isSelectionMode= mode
        notifyDataSetChanged()
    }

    fun getSelectedExercises():  List<Int> {
        return selectedItems.map { exercises[it].id }
    }
    fun selectAll()
    {
        selectedItems.clear()
        for(i in exercises.indices)
        {
            selectedItems.add(i)
        }
        notifyDataSetChanged()
    }
    fun deselectAll()
    {
        selectedItems.clear()

        notifyDataSetChanged()
    }
     fun toggleSelection(position: Int) {
         if (selectedItems.contains(position)) {
             selectedItems.remove(position)
             if (selectedItems.size<exercises.size) {
                 listener.onItemDeselected()
             }
         } else {
             selectedItems.add(position)
             if (selectedItems.size == exercises.size) {
                 listener.onAllItemsSelected()
             }
         }
        notifyDataSetChanged()
    }

    fun isSelectionModeEnabled():Boolean{
        return isSelectionMode
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun isItemSelected(position: Int): Boolean {
        return selectedItems.contains(position)
    }


}