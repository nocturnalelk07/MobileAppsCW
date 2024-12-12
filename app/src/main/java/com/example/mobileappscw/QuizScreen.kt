package com.example.mobileappscw


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappscw.database.SqliteDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlin.random.Random

class QuizScreen : AppCompatActivity() {

    private lateinit var difficultyText : TextView
    private lateinit var countText : TextView
    private lateinit var typeText : TextView
    private lateinit var categoryText : TextView
    private lateinit var db : SqliteDatabase
    private lateinit var questionArray : ArrayDeque<Question>
    private lateinit var userPreferences: UserPreferences
    private lateinit var currentQuestion: Question
    private lateinit var spinner: Spinner

    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_screen)

        currentUser = auth.currentUser
        db = SqliteDatabase(this)
        userPreferences = db.findUserPrefs(currentUser!!.email.toString())!!
        difficultyText = findViewById(R.id.prefDifficulty)
        val difficultyPref = userPreferences.difficulty
        countText = findViewById(R.id.prefCount)
        val countPref = userPreferences.count
        typeText = findViewById(R.id.prefType)
        val typePref = userPreferences.type
        categoryText = findViewById(R.id.prefCategory)
        val categoryPref = userPreferences.category

        difficultyText.text = String.format(getString(R.string.question_difficulty), difficultyPref)
        countText.text = String.format(getString(R.string.count_pref), countPref)
        typeText.text = String.format(getString(R.string.question_type), typePref)
        categoryText.text = String.format(getString(R.string.question_categories), categoryPref)

        questionArray = db.getCurrentQuestions()
        showQuestion()

        val submitAnswerButton = findViewById<Button>(R.id.submitButton)
        submitAnswerButton.setOnClickListener{_ -> answerQuestion()}

        lateinit var navBarIntent: Intent
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_bar_quiz)
        bottomNavigationView.setOnItemReselectedListener() {
            when (it.itemId) {
                R.id.homeButton -> navBarIntent = Intent(this, HomeScreen::class.java)
                R.id.alarmButton -> navBarIntent = Intent(this, AlarmScreen::class.java)
                R.id.profileButton -> navBarIntent = Intent(this, AccountScreen::class.java)
                R.id.settingsButton -> navBarIntent = Intent(this, SettingsScreen::class.java)
                R.id.quizButton -> navBarIntent = Intent(this, QuizScreen::class.java)
            }
            startActivity(navBarIntent)
        }
    }

    private fun showQuestion() {
        //get the question we will show
        val question = questionArray.removeFirstOrNull()
        val qDifficultyText = findViewById<TextView>(R.id.questionDifficulty)
        val qCategoryText = findViewById<TextView>(R.id.questionCategory)
        val qTypeText = findViewById<TextView>(R.id.questionType)
        val qCountText = findViewById<TextView>(R.id.questionCount)
        val qText = findViewById<TextView>(R.id.questionText)
        var questionsAnswered = 1

        val answerArray = arrayOf("", "", "", "")
        val arrayDeque = ArrayDeque<String>(3)

        //this is where in the array the correct question will go
        val randomInt = Random.nextInt(0,3)

        if (question != null) {
            currentQuestion = question
            if (!currentQuestion.answered) {
                arrayDeque.add(currentQuestion.incorrectAnswers[0])
                arrayDeque.add(currentQuestion.incorrectAnswers[1])
                arrayDeque.add(currentQuestion.incorrectAnswers[2])

                qDifficultyText.text = String.format(
                    getString(R.string.this_question_difficulty),
                    currentQuestion.difficulty
                )
                qCategoryText.text = String.format(
                    getString(R.string.this_question_category),
                    currentQuestion.category
                )
                qTypeText.text = String.format(
                    getString(R.string.this_question_type),
                    currentQuestion.type
                )
                qCountText.text = String.format(getString(R.string.question_count),
                    questionsAnswered.toString(), userPreferences.count)
                qText.text = currentQuestion.text

                //now we add the answers to one array to pass in to spinner
                spinner = findViewById(R.id.answerSpinner)
                Log.i("test306", randomInt.toString())
                for ((index, answer) in answerArray.withIndex()) {
                    if (index == randomInt) {
                        answerArray[index] = currentQuestion.answer
                    } else {
                        answerArray[index] = arrayDeque.removeFirst()
                    }
                }
                //now we use an array adapter to give the spinner the answer array
                val arrayAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
                    this,
                    android.R.layout.simple_spinner_item,
                    answerArray
                )

                arrayAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )
                spinner.adapter = arrayAdapter
            } else {
                questionsAnswered++
            }
        } else {
            qDifficultyText.text = ""
            qCategoryText.text = ""
            qTypeText.text = ""
            qCountText.text = ""
            qText.text = getString(R.string.all_questions_complete)
        }
    }

    private fun answerQuestion() {
        //do all the stuff
        val selectedAnswer = spinner.selectedItem
        val correctAnswer = currentQuestion.answer
        var wasCorrect = false
        if (selectedAnswer == correctAnswer) {
            wasCorrect = true
        }
        //send answer to the db to update, the database will update the first question that hasn't been answered
        db.answerAQuestion(wasCorrect, currentQuestion)

        //tell the user they got it right or wrong
        if (wasCorrect) {
            findViewById<TextView>(R.id.currentQuestion).text = getString(R.string.correct_answer)
        } else {
            findViewById<TextView>(R.id.currentQuestion).text = String.format(getString(R.string.wrong_answer),
                correctAnswer)
        }

        //show the next question
        showQuestion()
    }
}