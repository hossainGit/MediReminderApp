package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowCompat


class MainActivity : AppCompatActivity() {

    private lateinit var btnDashboard: ImageButton
    private lateinit var btnAdd: ImageButton
    private lateinit var btnCabinet: ImageButton
    private lateinit var btnSettings: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnDashboard = findViewById<ImageButton>(R.id.button)
        btnAdd = findViewById<ImageButton>(R.id.button2)
        btnCabinet = findViewById<ImageButton>(R.id.button3)
        btnSettings = findViewById<ImageButton>(R.id.button4)

        // Default fragment
        replaceFragment(Dashboard())
        btnDashboard.setColorFilter(ContextCompat.getColor(this, R.color.blue))

        btnDashboard.setOnClickListener {
            resetButtonColors()
            btnDashboard.setColorFilter(ContextCompat.getColor(this, R.color.blue))
            replaceFragment(Dashboard())
        }

        btnAdd.setOnClickListener {
            resetButtonColors()
            btnAdd.setColorFilter(ContextCompat.getColor(this, R.color.blue))
            replaceFragment(mediTime())
        }

        btnCabinet.setOnClickListener {
            resetButtonColors()
            btnCabinet.setColorFilter(ContextCompat.getColor(this, R.color.blue))
            replaceFragment(cabinet())
        }

        btnSettings.setOnClickListener {
            resetButtonColors()
            btnSettings.setColorFilter(ContextCompat.getColor(this, R.color.blue))
            replaceFragment(Settings())
        }

    }

    private fun resetButtonColors() {
        val defaultColor = ContextCompat.getColor(this, R.color.ocean_blue)
        btnDashboard.setColorFilter(defaultColor)
        btnAdd.setColorFilter(defaultColor)
        btnCabinet.setColorFilter(defaultColor)
        btnSettings.setColorFilter(defaultColor)
    }


    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragmentContainer, fragment)
        ft.commit()
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}
