package fr.tatu.kartingapp.firebase.karting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_data_kart.*

class DataKartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_kart)

        textViewDateSessionChoisi.text = intent.getStringExtra("DATE")
        textViewHeureSessionChoisi.text = intent.getStringExtra("TIME")
        textViewIdKart.text = intent.getStringExtra("ID_K")
        btn_positionClassement.text = intent.getStringExtra("POSITION")

        queryDataKart()
    }

    private fun queryDataKart(){
        val dialog = ProgressDialogUtil.setProgressDialog(this, "Loading...")
        dialog.show()
        val idSession = intent.getStringExtra("ID_S")
        val idKart = textViewIdKart.text.toString()
        FirebaseDatabase.getInstance().reference
            .child("/karting/Karts/$idKart/Sessions/$idSession")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    dialog.dismiss()
                    val p1 = p0.child("/Vitesses")
                    val p2 = p0.child("/Time")

                    val map1 = p1.value as Map<*, *>
                    textViewMaxSpeed.text = map1["VitesseMax"].toString()
                    textViewMinSpeed.text = map1["VitesseMin"].toString()
                    textViewAvgSpeed.text = map1["VitesseMoy"].toString()

                    val map2 = p2.value as Map<*, *>
                    textViewTotalTimes.text = map2["TotalTime"].toString()
                    textViewBestTimeLap.text = map2["BestTimeLap"].toString()
                    textViewWorstTimeLap.text = map2["WorstTimeLap"].toString()
                    textViewAvgTimeLap.text = map2["AvgTimeLap"].toString()
                }
            })
    }
}


