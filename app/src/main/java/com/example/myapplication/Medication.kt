package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences

data class Medication(
    val id: String,
    val name: String,
    val dosage: String,
    val pillsPerDose: Int,
    val timesCsv: String,
    val meal: String,
    val status: String // "active", "taken", "missed"
)

object PreferenceHelper {
    private const val PREFS_NAME = "myData"
    private const val KEY_MED_LIST = "med_list"        // Set<String> of reminders
    private const val KEY_CABINET = "cabinet_list"     // Set<String> of cabinet items

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Encoding formats (pipe separated)
    // Reminder: id|name|dosage|pillsPerDose|timesCsv|meal|status
    // Cabinet:  id|name|stock

    fun encodeReminder(m: Medication): String {
        val name = m.name.replace("|", " ")
        val dosage = m.dosage.replace("|", " ")
        val times = m.timesCsv.replace("|", " ")
        val meal = m.meal.replace("|", " ")
        return "${m.id}|$name|$dosage|${m.pillsPerDose}|$times|$meal|${m.status}"
    }

    fun parseReminder(raw: String): Medication? {
        val p = raw.split("|")
        if (p.size < 7) return null
        val id = p[0]
        val name = p[1]
        val dosage = p[2]
        val pills = p[3].toIntOrNull() ?: 1
        val times = p[4]
        val meal = p[5]
        val status = p[6]
        return Medication(id, name, dosage, pills, times, meal, status)
    }

    fun encodeCabinet(id: String, name: String, stock: Int): String {
        val n = name.replace("|", " ")
        return "$id|$n|$stock"
    }

    fun parseCabinet(raw: String): Triple<String, String, Int>? {
        val p = raw.split("|")
        if (p.size < 3) return null
        val id = p[0]
        val name = p[1]
        val stock = p[2].toIntOrNull() ?: 0
        return Triple(id, name, stock)
    }

    fun getAllReminders(ctx: Context): MutableSet<String> {
        val set = prefs(ctx).getStringSet(KEY_MED_LIST, null)
        return set?.toMutableSet() ?: mutableSetOf()
    }

    fun getAllCabinet(ctx: Context): MutableSet<String> {
        val set = prefs(ctx).getStringSet(KEY_CABINET, null)
        return set?.toMutableSet() ?: mutableSetOf()
    }

    fun saveReminders(ctx: Context, set: Set<String>) {
        val editor = prefs(ctx).edit()
        editor.putStringSet(KEY_MED_LIST, set)
        editor.apply()
    }

    fun saveCabinet(ctx: Context, set: Set<String>) {
        val editor = prefs(ctx).edit()
        editor.putStringSet(KEY_CABINET, set)
        editor.apply()
    }

    // Add or update reminder. If cabinet entry missing, create with cabinetStockIfNew.
    fun addOrUpdateReminder(ctx: Context, reminderRaw: String, cabinetStockIfNew: Int) {
        val reminders = getAllReminders(ctx)
        val parts = reminderRaw.split("|")
        val id = parts.getOrNull(0) ?: return
        val name = parts.getOrNull(1) ?: ""

        // remove old same id if exists
        val toRem = reminders.filter { it.split("|").firstOrNull() == id }
        toRem.forEach { reminders.remove(it) }
        reminders.add(reminderRaw)
        saveReminders(ctx, reminders)

        // ensure cabinet has entry for this id (if not present add with given stock)
        val cabinet = getAllCabinet(ctx)
        val cabMatch = cabinet.firstOrNull { it.split("|").firstOrNull() == id }
        if (cabMatch == null) {
            cabinet.add(encodeCabinet(id, name, cabinetStockIfNew))
            saveCabinet(ctx, cabinet)
        } else {
            // ensure name is synced while preserving stock
            val partsCab = cabMatch.split("|")
            val stock = partsCab.getOrNull(2)?.toIntOrNull() ?: 0
            cabinet.remove(cabMatch)
            cabinet.add(encodeCabinet(id, name, stock))
            saveCabinet(ctx, cabinet)
        }
    }

    fun deleteReminderById(ctx: Context, id: String) {
        val reminders = getAllReminders(ctx)
        val toRem = reminders.filter { it.split("|").firstOrNull() == id }
        toRem.forEach { reminders.remove(it) }
        saveReminders(ctx, reminders)
        // DO NOT delete cabinet here (keeps stock independent of reminder)
    }

    fun updateReminderStatus(ctx: Context, id: String, newStatus: String) {
        val reminders = getAllReminders(ctx)
        val updated = mutableSetOf<String>()
        for (r in reminders) {
            val parts = r.split("|")
            val rid = parts.getOrNull(0) ?: ""
            if (rid == id && parts.size >= 7) {
                val newRaw = parts.subList(0, 6).joinToString("|") + "|$newStatus"
                updated.add(newRaw)
            } else {
                updated.add(r)
            }
        }
        saveReminders(ctx, updated)
    }

    // When medicine is taken: set status and decrement cabinet by pillsPerDose
    fun markTaken(ctx: Context, id: String) {
        updateReminderStatus(ctx, id, "taken")
        val reminders = getAllReminders(ctx)
        val r = reminders.firstOrNull { it.split("|").firstOrNull() == id } ?: return
        val parts = r.split("|")
        val pillsPerDose = parts.getOrNull(3)?.toIntOrNull() ?: 1

        val cabinet = getAllCabinet(ctx)
        val cabMatch = cabinet.firstOrNull { it.split("|").firstOrNull() == id }
        if (cabMatch != null) {
            val cp = cabMatch.split("|")
            val stock = cp.getOrNull(2)?.toIntOrNull() ?: 0
            val newStock = (stock - pillsPerDose).coerceAtLeast(0)
            cabinet.remove(cabMatch)
            cabinet.add(encodeCabinet(id, cp.getOrNull(1) ?: "", newStock))
            saveCabinet(ctx, cabinet)
        }
    }

    fun markMissed(ctx: Context, id: String) {
        updateReminderStatus(ctx, id, "missed")
    }

    fun incrementCabinet(ctx: Context, id: String, delta: Int) {
        val cabinet = getAllCabinet(ctx)
        val match = cabinet.firstOrNull { it.split("|").firstOrNull() == id } ?: return
        val parts = match.split("|")
        val name = parts.getOrNull(1) ?: ""
        val stock = parts.getOrNull(2)?.toIntOrNull() ?: 0
        val newStock = (stock + delta).coerceAtLeast(0)
        cabinet.remove(match)
        cabinet.add(encodeCabinet(id, name, newStock))
        saveCabinet(ctx, cabinet)
    }

    // Optional: remove cabinet entry
    fun removeCabinetEntry(ctx: Context, id: String) {
        val cabinet = getAllCabinet(ctx)
        val match = cabinet.firstOrNull { it.split("|").firstOrNull() == id } ?: return
        cabinet.remove(match)
        saveCabinet(ctx, cabinet)
    }
}
