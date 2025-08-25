package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Medication data class

data class Medication(
    val name: String,
    val dosage: String,
    val times: List<String>, // e.g., ["morning", "noon", "night"]
    val mealInstruction: String // "before" or "after"
)

object MedicationUtils {
    private const val PREFS_NAME = "medication_prefs"
    private const val KEY_MEDICATIONS = "medications"
    private val gson = Gson()

    fun getMedications(context: Context): MutableList<Medication> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_MEDICATIONS, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Medication>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun saveMedications(context: Context, meds: List<Medication>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(meds)
        prefs.edit().putString(KEY_MEDICATIONS, json).apply()
    }

    fun addMedication(context: Context, med: Medication) {
        val meds = getMedications(context)
        meds.add(0, med) // add newest at the start
        saveMedications(context, meds)
    }
}

