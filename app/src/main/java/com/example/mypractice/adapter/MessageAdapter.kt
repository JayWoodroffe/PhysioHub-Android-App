package com.example.mypractice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mypractice.R
import com.example.mypractice.databinding.MessageCardBinding
import com.example.mypractice.model.MessageModel
class MessageAdapter(private val messages: List<MessageModel>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = MessageCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    class MessageViewHolder(private val binding: MessageCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageModel) {
            binding.textViewMessage.text = message.text
            // Optionally handle image messages
            // Load image using your preferred image loading library (e.g., Glide, Picasso)
        }
    }
}