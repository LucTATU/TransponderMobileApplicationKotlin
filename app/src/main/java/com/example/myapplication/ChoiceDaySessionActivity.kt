package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_choice_day_session.*
import java.util.*

class ChoiceDaySessionActivity: AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_choice_day_session);

        OnClickDate()

        btn_confirm.setOnClickListener{
            val intent = Intent(this, SessionSelectionActivity::class.java)
            intent.putExtra("date", dateText.text)
            startActivity(intent)
        }

        btn_logout.setOnClickListener {
            Toast.makeText(applicationContext, "Déconnecté", Toast.LENGTH_SHORT).show()
            this.finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun OnClickDate(){
        val datePicker = findViewById<DatePicker>(R.id.date_Picker)
        val today = Calendar.getInstance()
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH))
        { view, year, month, day ->
            val month = month + 1
            val msg = "$day/$month/$year"
            dateText.text = msg
        }
    }
}