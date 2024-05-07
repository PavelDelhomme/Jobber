package com.delhomme.jobber

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EntretienAddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entretien_add)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ajouter Entretien"

        val cancelButton = findViewById<Button>(R.id.btnCancel)
        cancelButton.setOnClickListener {
            finish()
        }
    }
}
