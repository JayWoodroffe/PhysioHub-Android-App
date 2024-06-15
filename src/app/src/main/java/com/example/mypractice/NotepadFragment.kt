package com.example.mypractice

import NoteSharedPreferencesHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

class NotepadFragment : Fragment() {
    private lateinit var noteEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var sharedPreferencesHelper: NoteSharedPreferencesHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notepad, container, false)

        noteEditText = view.findViewById(R.id.editTextNote)
        saveButton = view.findViewById(R.id.buttonSave)
        sharedPreferencesHelper = NoteSharedPreferencesHelper(requireContext())

        saveButton.setOnClickListener {
            val note = noteEditText.text.toString()
            sharedPreferencesHelper.saveNote(note)
        }

        // Load saved note on fragment creation
        val savedNote = sharedPreferencesHelper.getNote()
        noteEditText.setText(savedNote)

        return view
    }
}