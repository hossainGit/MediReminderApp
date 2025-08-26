package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import androidx.fragment.app.Fragment

class Settings : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etEmail: EditText
    private lateinit var swRem: Switch

    private val PREFS = "myData"
    private val KEY_NAME = "profile_name"
    private val KEY_AGE = "profile_age"
    private val KEY_EMAIL = "profile_email"
    private val KEY_REM = "reminder_enabled"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_settings, container, false)
        etName = v.findViewById(R.id.editTextText)
        etAge = v.findViewById(R.id.editTextText2)
        etEmail = v.findViewById(R.id.editTextTextEmailAddress)
        swRem = v.findViewById(R.id.switch1)
        return v
    }

    override fun onResume() {
        super.onResume()
        readSettings()
    }

    override fun onPause() {
        super.onPause()
        saveSettings()
    }

    private fun readSettings() {
        val p = requireActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        etName.setText(p.getString(KEY_NAME, ""))
        etAge.setText(p.getString(KEY_AGE, ""))
        etEmail.setText(p.getString(KEY_EMAIL, ""))
        swRem.isChecked = p.getBoolean(KEY_REM, true)
    }

    private fun saveSettings() {
        val p = requireActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val e = p.edit()
        e.putString(KEY_NAME, etName.text.toString().trim())
        e.putString(KEY_AGE, etAge.text.toString().trim())
        e.putString(KEY_EMAIL, etEmail.text.toString().trim())
        e.putBoolean(KEY_REM, swRem.isChecked)
        e.apply()
    }
}
