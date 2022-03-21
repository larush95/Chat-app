package com.example.chatapp.model

import java.util.*

class Message {
    var text: String? = null
    var name: String? = null
    var uid: String? = null

    //TODO check if profileImages can be supported
    //var profileImage: String? = null
    var timeSent: String? = null

    // Empty constructor needed for serialization
    constructor()

    constructor(text: String?, name: String?, profileImage: String?, timeSent: String?) {
        this.text = text
        this.name = name
        //this.profileImage = profileImage
        this.timeSent = timeSent
        this.uid = UUID.randomUUID().toString()
    }
}

