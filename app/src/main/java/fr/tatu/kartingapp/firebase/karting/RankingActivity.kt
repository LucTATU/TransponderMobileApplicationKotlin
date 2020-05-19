package fr.tatu.kartingapp.firebase.karting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_ranking.*
import kotlinx.android.synthetic.main.ranking_row.view.*

class SessionDuActivity : AppCompatActivity() {
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        textViewIDSessionRecup.text = intent.getStringExtra("ID_S")
        textViewDateSessionChoisi.text = intent.getStringExtra("DATE")
        textViewHeureSessionChoisi.text = intent.getStringExtra("TIME")

        recyclerView_classement.adapter = adapter

        //adapter.add(KartItemID(Ranking(2,8)))

        querySingleDataKart()

        // set item click listener on the adapter
        adapter.setOnItemClickListener { item, view ->
            val id = item as KartItemID
            val intent = Intent(this, DataKartActivity::class.java)
            intent.putExtra("ID_K", id.ranking.IDKART.toString())
            intent.putExtra("ID_S", textViewIDSessionRecup.text.toString())
            intent.putExtra("DATE", textViewDateSessionChoisi.text.toString())
            intent.putExtra("TIME", textViewHeureSessionChoisi.text.toString())
            intent.putExtra("POSITION", id.ranking.POSITION.toString())
            startActivity(intent)
        }

    }

    private fun querySingleDataKart() {
        val dialog = ProgressDialogUtil.setProgressDialog(this, "Loading...")
        dialog.show()
        val sessionId = textViewIDSessionRecup.text.toString()
        FirebaseDatabase.getInstance().reference
            .child("/karting/SESSIONS/$sessionId")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    dialog.dismiss()
                    val p1 = p0.child("/CLASSEMENT")
                    p1.children.forEach {
                        Log.d("trouve",it.toString())
                        val data = it.getValue(Ranking::class.java)

                        if (data != null) {
                            adapter.add(KartItemID(data))
                        }
                    }
                }
            })
    }
}

class Ranking(val IDKART: Int, val POSITION: Int) {
    constructor() : this(0,0)
}

class KartItemID(val ranking: Ranking): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textViewIdKart.text = ranking.IDKART.toString()
        viewHolder.itemView.btn_positionClassement.text = ranking.POSITION.toString()
    }

    override fun getLayout(): Int {
        return R.layout.ranking_row
    }
}