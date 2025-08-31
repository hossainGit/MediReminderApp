package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class cabinet : Fragment() {

    private lateinit var container: LinearLayout
//    private lateinit var btnUpdate: Button
//    private lateinit var btnDlt: Button

    override fun onCreateView(inflater: LayoutInflater, containerParent: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_cabinet, containerParent, false)
        container = v.findViewById(R.id.cabinetContainer)
//        btnUpdate = v.findViewById(R.id.button7)
//        btnDlt = v.findViewById(R.id.btnDeleteCabinet)
//
//        btnUpdate.setOnClickListener { loadCabinet() }

        return v
    }

    override fun onResume() {
        super.onResume()
        loadCabinet()
    }

    private fun loadCabinet() {
        container.removeAllViews()
        val set = PreferenceHelper.getAllCabinet(requireContext())
        val items = set.mapNotNull { PreferenceHelper.parseCabinet(it) }.sortedBy { it.third }

        if (items.isEmpty()) {
            val t = TextView(requireContext())
            t.text = "Cabinet empty"
            container.addView(t)
            return
        }

        val inflater = LayoutInflater.from(requireContext())
        for ((id, name, stock) in items) {
            val card = inflater.inflate(R.layout.item_cabinet, container, false)
            val tvName = card.findViewById<TextView>(R.id.tvCabName)
            val tvDetails = card.findViewById<TextView>(R.id.tvCabDetails)
            val tvAvail = card.findViewById<TextView>(R.id.tvIsAvailable)
            val tvStock = card.findViewById<TextView>(R.id.tvStock)
            val btnInc = card.findViewById<Button>(R.id.btnInc)
            val btnDec = card.findViewById<Button>(R.id.btnDec)
            val btnDlt = card.findViewById<Button>(R.id.btnDeleteCabinet)

            tvName.text = name
            tvDetails.text = "Inventory"
            tvStock.text = stock.toString()
            tvAvail.text = when {
                stock <= 0 -> "Empty"
                stock <= 3 -> "Running Low"
                else -> "Available"
            }

            btnInc.setOnClickListener {
                PreferenceHelper.incrementCabinet(requireContext(), id, +1)
                loadCabinet()
            }
            btnDec.setOnClickListener {
                PreferenceHelper.incrementCabinet(requireContext(), id, -1)
                loadCabinet()
            }

            btnDlt.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Remove")
                    .setMessage("Remove $name from cabinet? (Reminders will remain)")
                    .setPositiveButton("Yes") { _, _ ->
                        PreferenceHelper.removeCabinetEntry(requireContext(), id)
                        loadCabinet()
                    }
                    .setNegativeButton("No", null)
                    .show()
                true
            }

            container.addView(card)
        }
    }
}
