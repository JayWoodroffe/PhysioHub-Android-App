package com.example.mypractice

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mypractice.data.ClientDataAccess
import com.example.mypractice.data.DoctorDataHolder
import com.example.mypractice.databinding.ActivityInvitationCodeBinding
import java.util.Calendar

class InvitationCode : AppCompatActivity() {

    private lateinit var binding: ActivityInvitationCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvitationCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        var code = displayNewCode()

        binding.btnCopy.setOnClickListener {
            copyToClipboard(code)
            addCodeToDb(code)
        }

        binding.btnNewCode.setOnClickListener {
            code = displayNewCode()
        }

    }

    private fun displayNewCode():String {
        val code = generateCode()
        binding.tvCode.text = code
        return code
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

    fun copyToClipboard(code:String)
    {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Generated Code", code)
        clipboard.setPrimaryClip(clip)
    }

}