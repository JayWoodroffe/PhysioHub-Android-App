package com.example.mypractice

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypractice.adapter.MessageAdapter
import com.example.mypractice.data.ChatDataAccess
import com.example.mypractice.data.DoctorDataHolder
import com.example.mypractice.databinding.FragmentChatBinding
import com.example.mypractice.model.MessageModel

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private lateinit var patientId: String
    private lateinit var doctorId:String
    private lateinit var chatId:String
    private lateinit var messageAdapter: MessageAdapter
    private val messages = mutableListOf<MessageModel>()

    fun setClientId(id: String) {
        patientId = id
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChatBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        //getting the id
        doctorId = DoctorDataHolder.getLoggedInDoctor()?.certId.toString()

        if (doctorId.isBlank() || patientId.isBlank()) {
            Log.e("ChatFragment", "Doctor ID or Patient ID is blank")
            return binding.root
        }
        chatId = ChatDataAccess.getChatId(doctorId, patientId)

        messageAdapter = MessageAdapter(messages)
        binding.recyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = messageAdapter
        }

        ChatDataAccess.listenForMessages(chatId){newMessages ->
            messages.clear()
            messages.addAll(newMessages)
            messageAdapter.notifyDataSetChanged()
            binding.recyclerViewMessages.scrollToPosition(messageAdapter.itemCount - 1)
        }

        // Send message on button click
        binding.buttonSend.setOnClickListener {
            val text = binding.editTextMessage.text.toString()

            if (text.isNotEmpty()) {
                ChatDataAccess.sendMessage(chatId, doctorId, text)
                binding.editTextMessage.text.clear()
            }
        }

        return binding.root
    }




}