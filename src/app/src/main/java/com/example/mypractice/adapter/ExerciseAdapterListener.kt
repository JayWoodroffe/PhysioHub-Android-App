package com.example.mypractice.adapter

interface ExerciseAdapterListener {
    fun onItemSelected(position: Int)
    fun onItemLongClick(position: Int)
    fun onAllItemsSelected()
    fun onItemDeselected()

    fun onEditClick(position:Int)
}