package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_session_du.*
import kotlinx.android.synthetic.main.classementrow.view.*

class SessionDuActivity : AppCompatActivity() {
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_du)

        textViewIDSessionRecup.text = intent.getStringExtra("ID")
        textViewDateSessionChoisi.text = intent.getStringExtra("DATE")
        textViewHeureSessionChoisi.text = intent.getStringExtra("TIME")

        //recycleView_sessions.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))



        recyclerView_classement.adapter = adapter

        //adapter.add(KartItemID(Classement(2,8)))

        querySingleDataKart()

    }

    private fun querySingleDataKart() {
        FirebaseDatabase.getInstance().reference
            .child("/karting/SESSIONS")
            .orderByChild("IDSESSION").equalTo(textViewIDSessionRecup.text.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val p1 = p0.child("CLASSEMENT")
                    Log.d("trouve",p1.toString())
                    p1.children.forEach {// ???????????????
                        Log.d("trouve","1.5")
                        Log.d("trouve",it.toString())
                        val data = it.getValue(Classement::class.java)
                        if (data != null) {
                            adapter.add(KartItemID(data))
                            Log.d("trouve","2")
                        }
                    }
                }
            })
    }
}

class Classement(val IDKART: Int, val POSITION: Int) {
    constructor() : this(0, 0)
}

class KartItemID(val classement: Classement): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        Log.d("trouve","3")
        viewHolder.itemView.textViewIdKart.text = classement.IDKART.toString()
        viewHolder.itemView.btn_positionClassement.text = classement.POSITION.toString()
    }

    override fun getLayout(): Int {
        return R.layout.classementrow
    }
}