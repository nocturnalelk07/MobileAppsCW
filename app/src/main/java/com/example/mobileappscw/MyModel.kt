package com.example.mobileappscw

class MyModel {
    var modelName: String? = null
    private var modelImage: Int = 0

    fun getNames(): String {
        return modelName.toString()
    }

    fun setNames(name: String) {
        modelName = name
    }

    fun getImages(): Int {
        return modelImage
    }

    fun setImages(image: Int) {
        modelImage = image
    }


}