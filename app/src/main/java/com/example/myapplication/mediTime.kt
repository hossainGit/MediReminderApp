package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class mediTime : Fragment() {

    private lateinit var morningContainer: LinearLayout
    private lateinit var noonContainer: LinearLayout
    private lateinit var nightContainer: LinearLayout
    private lateinit var btnAdd: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_medi_time, container, false)
        morningContainer = v.findViewById(R.id.morningContainer)
        noonContainer = v.findViewById(R.id.noonContainer)
        nightContainer = v.findViewById(R.id.nightContainer)
        btnAdd = v.findViewById(R.id.button6)

        btnAdd.setOnClickListener {
            val i = Intent(requireActivity(), addMedActivity::class.java)
            startActivity(i)
        }

        return v
    }

    override fun onResume() {
        super.onResume()
        loadSchedule()
    }

    private fun loadSchedule() {
        morningContainer.removeAllViews()
        noonContainer.removeAllViews()
        nightContainer.removeAllViews()

        val set = PreferenceHelper.getAllReminders(requireContext())
        val meds = set.mapNotNull { PreferenceHelper.parseReminder(it) }
            .sortedBy { it.id.toLongOrNull() ?: 0L }

        if (meds.isEmpty()) {
            val t = TextView(requireContext())
            t.text = "No reminders found"
            morningContainer.addView(t)
            return
        }

        val inflater = LayoutInflater.from(requireContext())
        for (m in meds) {
            val times = m.timesCsv.split(",").map { it.trim() }
            for (tLabel in times) {

                val card = inflater.inflate(R.layout.item_medicine, null, false)
                val tvName = card.findViewById<TextView>(R.id.tvMedName)
                val tvDetails = card.findViewById<TextView>(R.id.tvMedDetails)
                val tvStatus = card.findViewById<TextView>(R.id.tvMedStatus)
                val btnEdit = card.findViewById<Button>(R.id.btnMedEdit)
                val btnDelete = card.findViewById<Button>(R.id.btnMedDelete)

                tvName.text = m.name
                tvDetails.text = "$tLabel • ${m.meal} • ${m.pillsPerDose} tab • ${m.dosage}"
                tvStatus.text = m.status.capitalize()

                btnEdit.setOnClickListener {
                    val raw = PreferenceHelper.getAllReminders(requireContext())
                        .firstOrNull { it.split("|").firstOrNull() == m.id }
                    if (raw != null) {
                        val i = Intent(requireActivity(), addMedActivity::class.java)
                        i.putExtra("med_raw", raw)
                        startActivity(i)
                    }
                }

                btnDelete.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete")
                        .setMessage("Delete ${m.name} reminder?")
                        .setPositiveButton("Yes") { _, _ ->

                            PreferenceHelper.deleteReminderById(requireContext(), m.id)
                            loadSchedule()
                        }
                        .setNegativeButton("No", null)
                        .show()
                }

                when (tLabel.toLowerCase()) {
                    "morning" -> morningContainer.addView(card)
                    "noon" -> noonContainer.addView(card)
                    "night" -> nightContainer.addView(card)
                    else -> noonContainer.addView(card)
                }
            }
        }

        if (morningContainer.childCount == 0) {

            val t = TextView(requireContext())
            t.text = "No morning medicines"

            morningContainer.addView(t)

        }
        if (noonContainer.childCount == 0) {
            val t = TextView(requireContext())
            t.text = "Noon: none"
            noonContainer.addView(t)
        }
        if (nightContainer.childCount == 0) {
            val t = TextView(requireContext())
            t.text = "No night medicines"
            nightContainer.addView(t)
        }
    }
}
