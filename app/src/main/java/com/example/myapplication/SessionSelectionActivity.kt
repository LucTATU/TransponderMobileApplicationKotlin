package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_session_selection.*
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.sessionsrow.*
import kotlinx.android.synthetic.main.sessionsrow.view.*
import kotlinx.android.synthetic.main.sessionsrow.view.btn_sessionChosed

class SessionSelectionActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_selection)

        recycleView_sessions.adapter = adapter
        recycleView_sessions.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

        timeTextRecup.text = intent.getStringExtra("time")
        dateTextRecup.text = intent.getStringExtra("date")

        //adapter.add(SessionItemID(IDSession("test","TEST",4)))
        querySingleDataDate()

        // set item click listener on the adapter
        adapter.setOnItemClickListener { item, view ->
            val id = item as SessionItemID
            val intent = Intent(this, SessionDuActivity::class.java)
            intent.putExtra("ID", id.idSession.IDSESSION.toString())
            intent.putExtra("DATE", id.idSession.DATESESSION.toString())
            intent.putExtra("TIME", id.idSession.HEURESESSION.toString())
            startActivity(intent)
        }
    }

    private fun querySingleDataDate() {
        FirebaseDatabase.getInstance().reference
            .child("/karting/SESSIONS")
            .orderByChild("DATESESSION").equalTo(dateTextRecup.text.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        Log.d("NewMessages", it.toString())
                        val data = it.getValue(IDSession::class.java)
                        if (data != null) {
                            adapter.add(SessionItemID(data))
                        }
                    }
                }
            })
    }

}
class IDSession(val DATESESSION: String, val HEURESESSION: String, val IDSESSION: Int) {
    constructor() : this("", "", 0)
}

class SessionItemID(val idSession: IDSession): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textViewDateSession.text = idSession.DATESESSION
        viewHolder.itemView.textViewHeureSession.text = idSession.HEURESESSION
        viewHolder.itemView.textViewIDSession.text = idSession.IDSESSION.toString()
    }

    override fun getLayout(): Int {
        return R.layout.sessionsrow
    }
}

