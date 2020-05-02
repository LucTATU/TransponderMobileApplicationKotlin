package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.ImageDecoder.createSource
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register);

        btn_save_register.setOnClickListener{
            performRegister()
        }

        btn_selectPicture.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    var selectedPhotoUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){


            //getBitmap
            selectedPhotoUri = data.data // location where the picture is stored on the device
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPhotoUri) //get access of the images selected
            selectPhoto_imageView_register.setImageBitmap(bitmap) //to show the image as the picture profile


            //decodeBitmap
            /*val image = data.data
            val source = ImageDecoder.createSource(this.contentResolver, image!!)
            val bitmap = ImageDecoder.decodeBitmap(source)
            selectPhoto_imageView_register.setImageBitmap(bitmap)*/


            btn_selectPicture.alpha = 0f //the btn is set on the background

            //to put the image in the button
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //btn_selectPicture.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister(){
        val pseudo = pseudo_editText_register.text.toString()
        val email = email_editText_register.text.toString()
        val password = password_editText_register.text.toString()
        val password2 = password2_editText_register.text.toString()

        if(pseudo.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()){
            Toast.makeText(this, "Please fill all the informations", Toast.LENGTH_SHORT).show()
            return
        }
        if(password != password2){
            Toast.makeText(this, "Passwords need to be the same", Toast.LENGTH_SHORT).show()
            return
        }
        //Firebase Authentication to create a user with email and pwd
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                Toast.makeText(this, "Successfully user created", Toast.LENGTH_LONG).show()
                uploadImageFirebaseStorage()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImageFirebaseStorage(){
        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString() //to give a random name
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{
                //do some logging here
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: "" //check if uid is known, if not => "" as default value
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, pseudo_editText_register.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("registerActivity" , "finally we saved the user to the database")
            }
    }
}

class User(val uid: String, val pseudo: String, val profileImageUrl: String)