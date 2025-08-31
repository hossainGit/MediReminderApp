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
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Dashboard : Fragment() {

    private lateinit var medListContainer: LinearLayout
    private lateinit var pillCabinetContainer: LinearLayout
    private lateinit var fab: FloatingActionButton


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?):
            View? {

        val v = inflater.inflate(R.layout.fragment_dashboard, container, false)
        medListContainer = v.findViewById(R.id.medListContainer)
        pillCabinetContainer = v.findViewById(R.id.pillCabinetContainer)

        fab = v.findViewById(R.id.fBtn)

        fab.setOnClickListener {
            val i = Intent(requireActivity(), addMedActivity::class.java)
            startActivity(i)
        }

        return v
    }

    override fun onResume() {
        super.onResume()
        loadUpcoming()
        loadCabinetPreview()
    }

    private fun loadUpcoming() {
        medListContainer.removeAllViews()
        val set = PreferenceHelper.getAllReminders(requireContext())
        val meds = set.mapNotNull { PreferenceHelper.parseReminder(it) }
            .filter { it.status == "active" }
            .sortedBy { it.id.toLongOrNull() ?: 0L }

        if (meds.isEmpty()) {
            val t = TextView(requireContext())
            t.text = "No upcoming medicines"
            medListContainer.addView(t)
            return
        }

        val inflater = LayoutInflater.from(requireContext())
        for (m in meds) {
            val card = inflater.inflate(R.layout.item_dash_med, medListContainer, false)
            val tvName = card.findViewById<TextView>(R.id.tvDashMedName)
            val tvDetails = card.findViewById<TextView>(R.id.tvDashMedDetails)
            val btnTaken = card.findViewById<Button>(R.id.btnDashTaken)
            val btnMissed = card.findViewById<Button>(R.id.btnDashMissed)

            tvName.text = m.name
            tvDetails.text = "${m.timesCsv} • ${m.meal} • ${m.pillsPerDose} tab • ${m.dosage}"

            btnTaken.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Taken")
                    .setMessage("Mark ${m.name} as taken?")
                    .setPositiveButton("Yes") { _, _ ->
                        PreferenceHelper.markTaken(requireContext(), m.id)
                        loadUpcoming()
                        loadCabinetPreview()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

            btnMissed.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Missed")
                    .setMessage("Mark ${m.name} as missed?")
                    .setPositiveButton("Yes") { _, _ ->
                        PreferenceHelper.markMissed(requireContext(), m.id)
                        loadUpcoming()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

            medListContainer.addView(card)
        }
    }

    private fun loadCabinetPreview() {
        pillCabinetContainer.removeAllViews()
        val set = PreferenceHelper.getAllCabinet(requireContext())
        val items = set.mapNotNull { PreferenceHelper.parseCabinet(it) }.sortedBy { it.third }

        if (items.isEmpty()) {
            val t = TextView(requireContext())
            t.text = "No cabinet items"
            pillCabinetContainer.addView(t)
            return
        }

        val inflater = LayoutInflater.from(requireContext())
        for ((id, name, stock) in items) {
            val card = inflater.inflate(R.layout.item_dash_cab, pillCabinetContainer, false)
            val tvName = card.findViewById<TextView>(R.id.tvDashCabName)
            val tvStock = card.findViewById<TextView>(R.id.tvDashCabStock)
            tvName.text = name
            tvStock.text = stock.toString()

//


            pillCabinetContainer.addView(card)
        }
    }
}
