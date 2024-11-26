package com.example.mobileappscw

class friend {
    var username: String? = null
    private var userPFP: Int = 0

    fun getNames(): String {
        return username.toString()
    }

    fun setNames(name: String) {
        username = name
    }

    fun getImages(): Int {
        return userPFP
    }

    fun setImages(image: Int) {
        userPFP = image
    }


}