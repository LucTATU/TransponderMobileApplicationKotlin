package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_session_du.*

class SessionDuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_du)


        textViewIDSessionRecup.text = intent.getStringExtra("ID")
    }
}
