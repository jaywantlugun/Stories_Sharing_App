package com.example.a023_stories

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDateTime

class HomeActivity : AppCompatActivity() {

    lateinit var txt_username:TextView
    lateinit var recyclerview:RecyclerView
    lateinit var btn_add_stories:FloatingActionButton

    lateinit var userid:String
    var username:String = ""
    var user_profile_image_url:String=""
    val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseReference: DatabaseReference = firebaseDatabase.reference.child("UsersList")

    var storiesList=ArrayList<Story>()
    lateinit var storiesAdapter: StoriesAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        txt_username = findViewById(R.id.txt_username)
        recyclerview = findViewById(R.id.recyclerview)
        btn_add_stories = findViewById(R.id.btn_add_stories)

        userid = intent.getStringExtra("userid").toString()
        fetchUserInfo()


        retrieveReceivedStoriesFromDatabase()
        recyclerview.layoutManager = GridLayoutManager(this,2)
        storiesAdapter = StoriesAdapter(this,storiesList)
        recyclerview.adapter = storiesAdapter


        btn_add_stories.setOnClickListener {

            val create_story_intent = Intent(this,CreateStory::class.java)
            create_story_intent.putExtra("userid",userid)
            create_story_intent.putExtra("username",username)
            create_story_intent.putExtra("user_profile_image_url",user_profile_image_url)
            startActivity(create_story_intent)

        }

    }

    private fun retrieveReceivedStoriesFromDatabase() {
        val receivedStoriesReference = databaseReference.child(userid).child("ReceivedStories")
        receivedStoriesReference.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                storiesList.clear()

                for(eachStoryData in snapshot.children)
                {
                    val data = eachStoryData.getValue(Story::class.java)
                    var remaining_time:Long = System.currentTimeMillis() - (data?.uploading_time ?: 0)
                    remaining_time = remaining_time/((60000).toLong())
                    //Toast.makeText(this@HomeActivity,remaining_time.toString(),Toast.LENGTH_LONG).show()
                    if (data != null && remaining_time<=((10.0).toLong())) {
                        storiesList.add(data)
                    }

                }
                storiesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"Unable to Fetch Data", Toast.LENGTH_LONG).show()
            }


        })
    }

    private fun fetchUserInfo() {
        val userReference = databaseReference.child(userid).child("UserInfo")
        userReference.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var userDetails = snapshot.getValue(User::class.java)
                user_profile_image_url = userDetails!!.imageURL
                username = userDetails.name
                txt_username.text = "Welcome $username"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,"Enter fetching user Data",Toast.LENGTH_LONG).show()
            }

        })
    }


    //display logout menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.home_menu,menu)
        return true
    }
    //function of logout menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.log_out){

            FirebaseAuth.getInstance().signOut()
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        return true
    }


}