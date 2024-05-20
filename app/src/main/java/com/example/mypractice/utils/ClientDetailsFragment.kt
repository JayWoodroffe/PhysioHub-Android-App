package com.example.mypractice.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mypractice.R
import com.example.mypractice.databinding.FragmentClientDetailsBinding
import com.example.mypractice.model.ClientModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ClientDetailsFragment : Fragment() {
    private lateinit var binding: FragmentClientDetailsBinding
    private lateinit var client: ClientModel
    fun setClientDetails(c: ClientModel)
    {
        client = c

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClientDetailsBinding.inflate(inflater, container, false)

        client.dob?.let{
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
            val age = it.calculateAge()
            binding.tvDobData.text = formattedDate
            binding.tvAgeData.text = age.toString()
        }?:run{
            binding.tvDobData.text = "N/A"
        }


        binding.tvNumberData.text = client.number
        binding.tvEmailData.text =  client.email
        
        return binding.root
    }

    private fun Date.calculateAge(): Int {
        val currentDate = Calendar.getInstance().time
        val dobCalendar = Calendar.getInstance().apply { time = this@calculateAge }

        var age = Calendar.getInstance().get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)

        if (Calendar.getInstance().get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }
}