package com.example.a023_stories

class User {
    lateinit var name:String
    lateinit var email:String
    lateinit var imageURL:String
    lateinit var uid:String
    constructor(name:String,email: String,imageURL:String,uid:String){
        this.name = name
        this.email = email
        this.imageURL=imageURL
        this.uid=uid
    }

    constructor()
}