package com.example.mypractice

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import com.example.mypractice.data.ClientDataAccess
import com.example.mypractice.data.DoctorDataHolder
import com.example.mypractice.databinding.ActivityInvitationCodeBinding
import java.util.Calendar

class InvitationCode : AppCompatActivity() {

    private lateinit var binding: ActivityInvitationCodeBinding
    private lateinit var email:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvitationCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )



        binding.btnNewCode.setOnClickListener {
            if (checkEmail())
                formatEmail();
            else
                Toast.makeText(this, "Incorrect email format", Toast.LENGTH_SHORT).show()
        }

        binding.imBack.setOnClickListener {
            startActivity(Intent(this, Clients::class.java))
        }

    }

    private fun checkEmail(): Boolean
    {
        email = binding.etClientEmail.text.toString();
        email = email.trim();
        if (email.isNullOrEmpty()) return false
        val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    private fun formatEmail()
    {
        val doctorsName = DoctorDataHolder.getLoggedInDoctor()?.name;
        val invitationCode = generateCode()
        val subject = "PhysioLink Invitation"

        val body = "Your physiotherapist, Dr. $doctorsName, has invited you to download and register for the application PhysioLink." +
                "\n\n\n" +
                "Invitation code: $invitationCode" +
                "\n\n\nEnter this code into the application to register and access important exercise " +
                "information from your doctor." +
                "\n\n\n" +
                "Welcome to the team,\n\nThe PhysioLink Team"

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body))
        intent.setPackage("com.google.android.gm") // Specify package to open Gmail app

        try {
            addCodeToDb(invitationCode)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Gmail app is not installed, handle the error or prompt the user to install it
            Toast.makeText(this, "Gmail app is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    fun generateCode():String{
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val codeLength = 5
        val random = java.util.Random()
        val sb = StringBuilder(codeLength)
        for (i in 0 until codeLength) {
            sb.append(characters[random.nextInt(characters.length)])
        }
        return sb.toString()
    }

    fun addCodeToDb(code:String)
    {

        DoctorDataHolder.getLoggedInDoctor()?.certId?.let {
            ClientDataAccess.addInviteCode(code,
                it
            )
        }
    }

}