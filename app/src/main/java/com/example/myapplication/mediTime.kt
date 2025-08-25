package com.example.myapplication

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray


class mediTime : Fragment() {

    private lateinit var morningContainer: LinearLayout
    private lateinit var noonContainer: LinearLayout
    private lateinit var nightContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_medi_time, container, false)

        morningContainer = view.findViewById(R.id.morningContainer)
        noonContainer = view.findViewById(R.id.noonContainer)
        nightContainer = view.findViewById(R.id.nightContainer)

        // Example: fetch from SharedPreferences
        val sharedPrefs = requireContext().getSharedPreferences("Meds", Context.MODE_PRIVATE)
        val medsJson = sharedPrefs.getString("med_list", "[]")
        val meds = JSONArray(medsJson)

        for (i in 0 until meds.length()) {
            val med = meds.getJSONObject(i)
            val name = med.getString("name")
            val meal = med.getString("meal")  // "Before Meal" / "After Meal"
            val time = med.getString("time")  // "morning"/"noon"/"night"

            addMedicineCard(inflater, name, meal, time)
        }

        return view
    }

    private fun addMedicineCard(inflater: LayoutInflater, name: String, meal: String, time: String) {
        val cardView = inflater.inflate(R.layout.item_medicine, null)

        val medName = cardView.findViewById<TextView>(R.id.medName)
        val medMeal = cardView.findViewById<TextView>(R.id.medMeal)
        val btnEdit = cardView.findViewById<Button>(R.id.btnEdit)
        val btnDelete = cardView.findViewById<Button>(R.id.btnDelete)

        medName.text = name
        medMeal.text = meal

        btnEdit.setOnClickListener {
            Toast.makeText(requireContext(), "Edit $name", Toast.LENGTH_SHORT).show()
            // TODO: Open edit activity
        }

        btnDelete.setOnClickListener {
            Toast.makeText(requireContext(), "Deleted $name", Toast.LENGTH_SHORT).show()
            // TODO: Remove from SharedPreferences
        }

        when (time.lowercase()) {
            "morning" -> morningContainer.addView(cardView)
            "noon" -> noonContainer.addView(cardView)
            "night" -> nightContainer.addView(cardView)
        }
    }
    }


