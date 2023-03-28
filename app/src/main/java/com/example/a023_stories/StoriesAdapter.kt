package com.example.a023_stories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class StoriesAdapter(val context: HomeActivity,val storiesList: ArrayList<Story>): RecyclerView.Adapter<StoriesAdapter.StoryViewHolder>(){
    class StoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val sender_image: ImageView = itemView.findViewById(R.id.stories_user_image)
        val sender_name: TextView = itemView.findViewById(R.id.stories_user_name)
        val sender_story_image: ImageView = itemView.findViewById(R.id.stories_image)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_stories,parent,false)
        return StoryViewHolder(view)

    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {

        val StoryData = storiesList[position]
        val sender_image = StoryData.sender_profle_image
        val sender_name = StoryData.sender_name
        val sender_story_image = StoryData.sender_story_image


        //load profile image
        Picasso.get().load(sender_image).into(holder.sender_image,object : Callback {
            override fun onSuccess() {
                //profile image loaded successfully
                //loading story image
                holder.sender_name.text = sender_name
                Picasso.get().load(sender_story_image).into(holder.sender_story_image,object : Callback {
                    override fun onSuccess() {
                        //image loaded successfully

                    }

                    override fun onError(e: Exception?) {


                    }

                })
            }

            override fun onError(e: Exception?) {

                //Toast.makeText(context,e?.localizedMessage, Toast.LENGTH_LONG).show()
            }

        })


    }

    override fun getItemCount(): Int {
        return storiesList.size
    }


}