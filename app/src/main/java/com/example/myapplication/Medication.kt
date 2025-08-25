package com.example.myapplication

data class Medication(
    val id: String,
    var name: String,
    var dosage: String,
    var times: List<String>, // Morning, Noon, Night
    var mealTiming: String   // Before or After Meal
)
