package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_sessionchoice.*
import kotlinx.android.synthetic.main.sessionsrow.view.*
import java.text.SimpleDateFormat
import java.util.*

class SessionChoiceActivity: AppCompatActivity() {

//TODO verifyUserIsLoggedIn Kotlin Messenger 04

    var dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.FRANCE)
    var timeFormat = SimpleDateFormat("HH:mm ", Locale.FRANCE)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sessionchoice);


        btn_showDialogDate.setOnClickListener {
            val chosenDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val date = dateFormat.format(selectedDate.time)
                    btn_showDialogDate.text = dateFormat.format(selectedDate.time)
                },
                chosenDate.get(Calendar.YEAR),
                chosenDate.get(Calendar.MONTH),
                chosenDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }


        btn_showDialogTime.setOnClickListener {
            val chosenTime = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    val selectedTime = Calendar.getInstance()
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)
                    btn_showDialogTime.text = timeFormat.format(selectedTime.time)
                },
                chosenTime.get(Calendar.HOUR_OF_DAY), chosenTime.get(Calendar.MINUTE), true
            )
            timePicker.show()
        }

        fetchData()
    }

    private fun fetchData() { //recupere toutes les données
        val ref = FirebaseDatabase.getInstance().getReference("/sensor/dht")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d("NewMessages",it.toString())
                    val data = it.getValue(DataTransponder::class.java)
                    if(data != null){
                        adapter.add(SessionItem(data))
                    }
                }

                recycleView_sessions.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
class DataTransponder(val x: Int, val y: Int, val z: Int) {
    constructor() : this(0, 0 ,0 )

}
class SessionItem(val dataTransponder: DataTransponder): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textViewX.text = dataTransponder.x.toString()
        viewHolder.itemView.textViewY.text = dataTransponder.y.toString()
        viewHolder.itemView.textViewZ.text = dataTransponder.z.toString()
    }

    override fun getLayout(): Int {
        return R.layout.sessionsrow //comprends pas
    }
}