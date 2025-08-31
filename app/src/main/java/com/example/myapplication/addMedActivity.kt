package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class addMedActivity : AppCompatActivity() {

    private lateinit var medName: EditText
    private lateinit var medDosage: EditText
    private lateinit var medPillsPerDose: EditText
    private lateinit var medStock: EditText        // ?? fix
    private lateinit var cbMorning: CheckBox       // in @+id/timeCheckboxes
    private lateinit var cbNoon: CheckBox
    private lateinit var cbNight: CheckBox
    private lateinit var rbBefore: RadioButton    // khaoar age
    private lateinit var rbAfter: RadioButton     // khaoar pore
    private lateinit var btnAdd: Button
    private lateinit var btnDiscard: Button

    private var editingRaw: String? = null
    private var editingId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_med)

        medName = findViewById(R.id.medName)
        medDosage = findViewById(R.id.medDosage)
        medPillsPerDose = findViewById(R.id.medPillsPerDose)
        medStock = findViewById(R.id.medStock)


        cbMorning = findViewById(R.id.checkBox2)
        cbNoon = findViewById(R.id.checkBox3)
        cbNight = findViewById(R.id.checkBox4)

        rbBefore = findViewById(R.id.radioButton6)
        rbAfter = findViewById(R.id.radioButton7)

        btnAdd = findViewById(R.id.addMedButton)
        btnDiscard = findViewById(R.id.discardBtn)

        // edit mode need?
        editingRaw = intent.getStringExtra("med_raw")
        if (editingRaw != null) prefill(editingRaw!!)

        btnDiscard.setOnClickListener { finish() }

        btnAdd.setOnClickListener { onSave() }
    }

    private fun prefill(raw: String) {
        val med = PreferenceHelper.parseReminder(raw)
        if (med != null) {
            editingId = med.id
            medName.setText(med.name)
            medDosage.setText(med.dosage)
            medPillsPerDose.setText(med.pillsPerDose.toString())

            val times = med.timesCsv.split(",").map { it.trim() }
            cbMorning.isChecked = times.contains("Morning")
            cbNoon.isChecked = times.contains("Noon")
            cbNight.isChecked = times.contains("Night")

            rbBefore.isChecked = med.meal.contains("Before", ignoreCase = true)
            rbAfter.isChecked = med.meal.contains("After", ignoreCase = true)

            // find cabinet stock (if any)
            val cab = PreferenceHelper.getAllCabinet(this)
            val match = cab.firstOrNull { it.split("|").firstOrNull() == med.id }
            val stock = match?.split("|")?.getOrNull(2)?.toIntOrNull() ?: 0
            medStock.setText(stock.toString())

            btnAdd.text = "Update Medication"
        }
    }

    private fun onSave() {
        val name = medName.text.toString().trim()
        val dosage = medDosage.text.toString().trim()
        val pillsPerDose = medPillsPerDose.text.toString().toIntOrNull() ?: 1
        val stock = medStock.text.toString().toIntOrNull() ?: 0

        if (name.isEmpty() || dosage.isEmpty()) {
            Toast.makeText(this, "Please fill name and dosage", Toast.LENGTH_SHORT).show()
            return
        }
        val times = mutableListOf<String>()
        if (cbMorning.isChecked) times.add("Morning")
        if (cbNoon.isChecked) times.add("Noon")
        if (cbNight.isChecked) times.add("Night")
        if (times.isEmpty()) {
            Toast.makeText(this, "Select at least one time", Toast.LENGTH_SHORT).show()
            return
        }
        val timesCsv = times.joinToString(",")
        val meal = if (rbBefore.isChecked) "Before Meal" else "After Meal"
        val id = editingId ?: System.currentTimeMillis().toString()
        val med = Medication(id, name, dosage, pillsPerDose, timesCsv, meal, "active")
        val raw = PreferenceHelper.encodeReminder(med)

        AlertDialog.Builder(this)
            .setTitle("Confirm")
            .setMessage("Save medication?")
            .setPositiveButton("Yes") { _, _ ->
                PreferenceHelper.addOrUpdateReminder(this, raw, stock)
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
