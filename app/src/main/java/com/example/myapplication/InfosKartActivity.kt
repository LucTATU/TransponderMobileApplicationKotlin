package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_infos_kart.*

class InfosKartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infos_kart)

        textViewDateSessionChoisi.text = intent.getStringExtra("DATE")
        textViewHeureSessionChoisi.text = intent.getStringExtra("TIME")
        textViewIdKart.text = intent.getStringExtra("ID_K")

        queryDatasKart()
    }

    private fun queryDatasKart(){
        val idSession = intent.getStringExtra("ID_S")
        val idKart = textViewIdKart.text.toString()
        FirebaseDatabase.getInstance().reference
            .child("/karting/Karts/$idKart/Sessions/$idSession")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val p1 = p0.child("/Vitesses")
                    val p2 = p0.child("/Time")

                    var map1 = p1.value as Map<String,Any>
                    textViewMaxSpeed.text = map1["VitesseMax"].toString()
                    textViewMinSpeed.text = map1["VitesseMin"].toString()
                    textViewAvgSpeed.text = map1["VitesseMoy"].toString()

                    var map2 = p2.value as Map<String,Any>
                    textViewTotalTimes.text = map2["TotalTime"].toString()
                    textViewBestTimeLap.text = map2["BestTimeLap"].toString()
                    textViewWorstTimeLap.text = map2["WorstTimeLap"].toString()
                    textViewAvgTimeLap.text = map2["AvgTimeLap"].toString()

                }
            })
    }
}


