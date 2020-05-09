package com.example.myapplication

import android.util.Log
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

/*

    var dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.FRANCE)
    var timeFormat = SimpleDateFormat("HH:mm ", Locale.FRANCE)


    //fetchData()


private fun fetchData() { //recupere toutes les données
    val ref = FirebaseDatabase.getInstance().getReference("/sensor/dht")
    ref.addListenerForSingleValueEvent(object : ValueEventListener {

        override fun onDataChange(p0: DataSnapshot) {
            val adapter = GroupAdapter<ViewHolder>()

            p0.children.forEach {
                Log.d("NewMessages",it.toString())
                val data = it.getValue(DataTransponder::class.java)
                if(data != null){
                    adapter.add(SessionItem(data))
                }
            }

            //recycleView_sessions.adapter = adapter
        }
        override fun onCancelled(p0: DatabaseError) {

        }
    })
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
        return R.layout.sessionsrow
    }
}*/