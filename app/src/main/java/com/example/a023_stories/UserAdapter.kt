package com.example.a023_stories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class UserAdapter(
    val context: CreateStory,
    val userList: ArrayList<User>,
    var sendUserInterface:SendUserInterface
): RecyclerView.Adapter<UserAdapter.UserViewHolder>(){

    var sendList = ArrayList<String>()

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val user_name: TextView = itemView.findViewById(R.id.user_name)
        val user_image: ImageView = itemView.findViewById(R.id.user_image)
        val user_checkbox:CheckBox = itemView.findViewById(R.id.user_checkbox)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_row,parent,false)
        return UserViewHolder(view)

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val UserData = userList[position]
        val name:String = UserData.name
        val uid = UserData.uid

        //load profile image
        val imageURL = UserData.imageURL
        Picasso.get().load(imageURL).into(holder.user_image,object : Callback {
            override fun onSuccess() {
                //image loaded successfully
                holder.user_name.text = name
            }

            override fun onError(e: Exception?) {
                Toast.makeText(context,e?.localizedMessage, Toast.LENGTH_LONG).show()
            }

        })

        holder.user_checkbox.setOnClickListener {

            if(holder.user_checkbox.isChecked){
                sendList.add(uid)
            }
            else{
                sendList.remove(uid)
            }
            sendUserInterface.getSendList(sendList)
        }


    }

    override fun getItemCount(): Int {
        return userList.size
    }




}
