package com.example.mobileappscw

class Question(var text : String, var answer : String, var incorrectAnswers : Array<String>, var answered: Boolean = false,
               var correct : Boolean = false, var type : String) {
}