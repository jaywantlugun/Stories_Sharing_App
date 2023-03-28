package com.example.a023_stories

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    //declaring variables
    lateinit var btn_login_activity: Button
    lateinit var btn_signup_activity: Button
    lateinit var imageView: ImageView

    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //hiding the top action bar
        if(supportActionBar!=null){
            supportActionBar!!.hide()
        }

        //initialising the layout variables
        btn_signup_activity = findViewById(R.id.btn_signup_activity)
        btn_login_activity = findViewById(R.id.btn_login_activity)
        imageView = findViewById(R.id.imageView)

        //clicking login button
        btn_login_activity.setOnClickListener {

            val loginIntent = Intent(this,LoginActivity::class.java)
            //val pair = Pair(imageView,"main_image")
            val options: ActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this,android.util.Pair(imageView,"main_image"))
            startActivity(loginIntent,options.toBundle())
            // finish()
        }

        //clicking sign up button
        btn_signup_activity.setOnClickListener {

            val signupIntent = Intent(this,SignupActivity::class.java)
            startActivity(signupIntent)
            //finish()

        }



    }

    //Login using last user
    override fun onStart() {
        super.onStart()

        val user = firebaseAuth.currentUser
        if(user!=null){
            val userId=user.uid
            val homeIntent= Intent(this,HomeActivity::class.java)
            homeIntent.putExtra("userid",userId)
            startActivity(homeIntent)
            finish()
        }

    }

}