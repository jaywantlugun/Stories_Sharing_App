package com.example.a023_stories

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.security.Timestamp
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class CreateStory : AppCompatActivity(),SendUserInterface {

    lateinit var create_story_imageview:ImageView
    lateinit var btn_select_story_image:Button
    lateinit var create_story_recyclerview:RecyclerView
    lateinit var btn_send_story:FloatingActionButton

    val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseReference: DatabaseReference = firebaseDatabase.reference.child("UsersList")

    val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference: StorageReference = firebaseStorage.reference.child("UsersStoryImages")

    var userList=ArrayList<User>()
    lateinit var userAdapter: UserAdapter

    lateinit var userid:String
    lateinit var username:String
    lateinit var user_profile_image_url:String

    //for imageView
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var imageUri: Uri?=null
    lateinit var imageURL:String
    var image_uploaded:Int = 0

    var sendingList = ArrayList<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_story)

        create_story_imageview =findViewById(R.id.create_story_imageview)
        btn_select_story_image = findViewById(R.id.btn_select_story_image)
        create_story_recyclerview = findViewById(R.id.create_story_recyclerview)
        btn_send_story = findViewById(R.id.btn_send_story)

        userid = intent.getStringExtra("userid").toString()
        username = intent.getStringExtra("username").toString()
        user_profile_image_url = intent.getStringExtra("user_profile_image_url").toString()

        //register Activity for Result (otherwise it will not work )
        registerActivityForResult()

        //Adapter Part
        retrieveUsersFromDatabase()



        create_story_recyclerview.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(this, userList,this)
        create_story_recyclerview.adapter = userAdapter

        btn_select_story_image.setOnClickListener {
            chooseImage()
        }

        btn_send_story.setOnClickListener {

            if(image_uploaded==0){
                Toast.makeText(this,"Select Image",Toast.LENGTH_LONG).show()
            }
            else{
                upload_image()
            }

        }

    }

    //function to select image from gallery
    private fun chooseImage(){

        //permission is already granted
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        activityResultLauncher.launch(intent)

    }

    //If user is requested permission for the first time
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==123 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //permission is  granted for the 1st time
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }

    }

    //register activity for result
    private fun registerActivityForResult(){
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result->

                val resultCode = result.resultCode
                val imageData = result.data

                if(resultCode== RESULT_OK && imageData!=null){
                    imageUri = imageData.data

                    //using picasso to load image in imageview
                    imageUri?.let {
                        Picasso.get().load(it).into(create_story_imageview)
                        image_uploaded=1
                    }

//                    Another way to load image in ImageView
//                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//                    signup_image.setImageBitmap(bitmap)
                }


            })
    }

    private fun retrieveUsersFromDatabase() {
        databaseReference.addListenerForSingleValueEvent(object :
            ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                for(eachUserData in snapshot.children)
                {
                    val data = eachUserData.child("UserInfo").getValue(User::class.java)
                    if(data!=null && data.uid!=userid)
                    {
                        userList.add(data)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"Unable to Fetch Data", Toast.LENGTH_LONG).show()
            }


        })
    }

    private fun upload_image(){

        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("This is TITLE")
        mProgressDialog.show()


        val imageName = UUID.randomUUID().toString()
        val profileImageReference = storageReference.child("StoryImages").child(imageName)

        imageUri?.let { uri->

            profileImageReference.putFile(uri).addOnSuccessListener {

                //downloadable url
                profileImageReference.downloadUrl.addOnSuccessListener {url->

                    imageURL=url.toString()
                    sendStory(mProgressDialog)
                }


            }.addOnFailureListener{

                if(mProgressDialog.isShowing){
                    mProgressDialog.dismiss()
                }

                Toast.makeText(applicationContext,it.localizedMessage, Toast.LENGTH_LONG).show()
            }.addOnProgressListener { taskSnapshot->
                var percent:Float = (100*taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount).toFloat()
                mProgressDialog.setMessage("Sending Story : $percent%")

            }

        }


    }


    private fun sendStory(mProgressDialog: ProgressDialog) {
        val uploading_time = System.currentTimeMillis()
        val story = Story(user_profile_image_url,username,imageURL,uploading_time)

        val receiverReference = databaseReference
        for(receiver in sendingList){
            val receiverRef = receiverReference.child(receiver).child("ReceivedStories")
            receiverRef.push().setValue(story)
        }
        mProgressDialog.dismiss()

        sendingList.clear()
        val intent= Intent(this,HomeActivity::class.java)
        intent.putExtra("userid",userid)
        startActivity(intent)
        finish()
    }


    override fun getSendList(sendList: ArrayList<String>) {
        sendingList = sendList
    }
}