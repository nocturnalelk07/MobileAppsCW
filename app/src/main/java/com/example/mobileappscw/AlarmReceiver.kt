package com.example.mobileappscw

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.example.mobileappscw.database.SqliteDatabase
import com.koushikdutta.ion.Ion
import org.json.JSONObject
import java.time.LocalDateTime

class AlarmReceiver: BroadcastReceiver() {
    private lateinit var mySharedPreferences : SharedPreferences
    private lateinit var message : String

    override fun onReceive(context: Context?, intent: Intent?) {
        message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return

        if (context != null) {
            //we want to call the alarm again when it finishes so that it repeats
            val repeatTime = getNewTime(context)
            val alarmItem = AlarmItem(repeatTime, message)
            AndroidAlarmScheduler(context).schedule(alarmItem)

            val db = SqliteDatabase(context)
            //db.dropQuestionTables()
            db.newCurrentQuestionsTable()
            //TODO finish database call
            //call api, make array of questions, pass it into db, helper adds them to db
            //using arrayDequeue so i can pop questions as they are entered
            callAPI(context, db)

            //TODO send a call to the alarm system to go off here with a notification

        }
    }

    private fun getNewTime(context: Context) : LocalDateTime {
        val currentTime = LocalDateTime.now()

        //get the stored time for hour and minute in case it has changed, assume it hasn't by default
        mySharedPreferences = context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        val setHour = mySharedPreferences.getInt("alarm_Hour", currentTime.hour)
        val setMin = mySharedPreferences.getInt("alarm_min", currentTime.minute)

        //construct a local date time from the user prefs, plus one to go off next day
        val newTime = LocalDateTime.of(currentTime.year, currentTime.month,
            currentTime.dayOfMonth.plus(1), setHour, setMin)

        return newTime
    }

    private fun callAPI(context: Context, db : SqliteDatabase) {
        //this is where we are getting our questions from the api call and popping them on an array dequeue
        //construct the query with user prefs
        val query = constructQuery(context, db)
        //use query to call api then call function to add to db
        Ion.with(context)
            .load(query)
            .asString()
            .setCallback{ex, result ->addQuestionsToDb(result, context, db)}
    }

    private fun addQuestionsToDb(result : String, context: Context, db: SqliteDatabase) {

        //process the string of json into questions and send them to the database

        val resultProcessing = JSONObject(result)
        val resultArray = resultProcessing.getJSONArray("results")
        val arrayDeque : ArrayDeque<Question> = ArrayDeque(resultArray.length())
        //iterate over a range since JSONArray doesn't seem to have an iterator for loops
        for (i in 0..<resultArray.length()) {
            val questionJSON = resultArray.getJSONObject(i)
            val type = questionJSON.getString("type")
            val difficulty = questionJSON.getString("difficulty")
            val category = questionJSON.getString("category")
            val question = questionJSON.getString("question")
            val correctAnswer = questionJSON.getString("correct_answer")


            val incorrectAnswersJSON = questionJSON.getJSONArray("incorrect_answers")
            val incorrectAnswers = arrayOf("", "", "")

            for (count in 0..<incorrectAnswersJSON.length()) {
                incorrectAnswers[count] =
                    incorrectAnswersJSON.getString(count)
            }
            //now we have a question, we can put it in our ArrayDequeue
            val questionObject = Question(question, correctAnswer, incorrectAnswers, false, false, type, difficulty, category)
            arrayDeque.add(questionObject)
        }
        db.addQuestions(arrayDeque)
    }

    private fun constructQuery(context: Context, db: SqliteDatabase) : String {
//https://opentdb.com/api.php?amount=12&category=19&difficulty=medium&type=multiple
        val email = message
        //returns a userPrefs object we can use to get the user prefs
        val userPrefs = db.findUserPrefs(email)
        val count = userPrefs!!.count
        val category = userPrefs.category
        val difficulty = userPrefs.difficulty
        val type = userPrefs.type

        //here we have to convert the preferences into id's according to the api documentation
        val categoryArray = context.resources.getStringArray(R.array.categories)
        var categoryPosition = 0
        for (i in categoryArray.indices) {
            if (categoryArray[i] == category) {
                categoryPosition = i + 9
            }
        }
        var categoryString = categoryPosition.toString()
        if (categoryPosition == 0) {
            categoryString = ""
        }
        var difficultyString = difficulty
        if (difficultyString == "Any Difficulty") {
            difficultyString = ""
        }
        var typeString = type
        if (typeString == "Any Type") {
            typeString = ""
        }

        //now we format the string
        val url= String.format(context.getString(R.string.trivia_API_base_url), count,
            categoryString, difficultyString, typeString)
        Log.i("test306", url)

        //url for testing
        return "https://opentdb.com/api.php?amount=4&category=11&difficulty=&type="
    }
}