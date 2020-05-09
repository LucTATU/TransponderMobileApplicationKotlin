package com.example.myapplication

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sessionchoice.*
import java.util.*

class SessionChoiceActivity: AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sessionchoice);
        OnClickTime()
        OnClickDate()
        btn_confirm.setOnClickListener{
            val intent = Intent(this, SessionSelectionActivity::class.java)
            intent.putExtra("time",timeText.text)
            intent.putExtra("date",dateText.text)
            startActivity(intent)
        }
    }


    private fun OnClickTime() {
        val textView = findViewById<TextView>(R.id.timeText)
        val timePicker = findViewById<TimePicker>(R.id.time_Picker)
        timePicker.setOnTimeChangedListener { _, hour, minute -> var hour = hour
            var am_pm = ""
            // AM_PM decider logic
            when {hour == 0 -> { hour += 12
                am_pm = "AM"
            }
                hour == 12 -> am_pm = "PM"
                hour > 12 -> { hour -= 12
                    am_pm = "PM"
                }
                else -> am_pm = "AM"
            }
            if (textView != null) {
                val hour = if (hour < 10) "0" + hour else hour
                val min = if (minute < 10) "0" + minute else minute
                // display format of time
                val msg = "$hour : $min $am_pm"
                textView.text = msg
                //textView.visibility = ViewGroup.VISIBLE
            }
        }
    }

    private fun OnClickDate(){
        val datePicker = findViewById<DatePicker>(R.id.date_Picker)
        val today = Calendar.getInstance()
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)

        ) { view, year, month, day ->
            val month = month + 1
            val msg = "$day/$month/$year"
            dateText.text = msg
            //dateText.visibility = ViewGroup.VISIBLE
        }
    }
}