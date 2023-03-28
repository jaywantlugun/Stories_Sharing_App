package com.example.a023_stories

import java.time.LocalDateTime

class Story{
    lateinit var sender_profle_image:String
    lateinit var sender_name:String
    lateinit var sender_story_image:String
    var uploading_time:Long=0
    constructor(sender_profle_image:String,sender_name:String,sender_story_image:String,uploading_time:Long){
        this.sender_profle_image = sender_profle_image
        this.sender_name = sender_name
        this.sender_story_image = sender_story_image
        this.uploading_time = uploading_time
    }

    constructor()
}