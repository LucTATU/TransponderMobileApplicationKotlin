package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login);

        btn_log_login.setOnClickListener{
            performLogin()

        }
    }

    private fun performLogin(){
        val email = email_editText_login.text.toString()
        val password = password_editText_login.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill all the informations", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                val intent = Intent(this, ChoiceDaySessionActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to connect: ${it.message}", Toast.LENGTH_LONG).show()

            }
    }
}