package com.example.mobileappscw.database

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.mobileappscw.Question
import com.example.mobileappscw.UserPreferences
import com.google.firebase.auth.FirebaseAuth

class SqliteDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser

    override fun onCreate(db: SQLiteDatabase) {
        //sets default preferences for new users
        val createPrefsTable =
            "CREATE	TABLE $TABLE_USER_PREFERENCES($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COLUMN_USER_EMAIL TEXT ," +
                    " $COLUMN_PREFS_DIFFICULTY TEXT DEFAULT 'easy', $COLUMN_PREFS_COUNT TEXT DEFAULT '10', " +
                    "$COLUMN_PREFS_TYPE TEXT DEFAULT 'multiple choice', $COLUMN_PREFS_CATEGORY TEXT DEFAULT 'any category')"
        db.execSQL(createPrefsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_PREFERENCES")
        onCreate(db)
    }

    //should be called when user is signed in after creating account
    fun addPreferences(difficulty : String, count : String, type : String, category : String) {
        val values = ContentValues()
        currentUser = auth.currentUser
        val email = currentUser?.email
        values.put(COLUMN_USER_EMAIL, email)
        values.put(COLUMN_PREFS_DIFFICULTY, difficulty)
        values.put(COLUMN_PREFS_COUNT, count)
        values.put(COLUMN_PREFS_TYPE, type)
        values.put(COLUMN_PREFS_CATEGORY, category)
        val db = this.writableDatabase
        db.insert(TABLE_USER_PREFERENCES, null, values)
    }

    //user deletes their own info from the table and firebase
    fun deleteUser() {
        currentUser = auth.currentUser
        val email = currentUser?.email
        val db = this.writableDatabase
        db.delete(TABLE_USER_PREFERENCES,"$COLUMN_USER_EMAIL = ?", arrayOf(email))
        auth.currentUser?.delete()
    }

    companion object {
        private const val DATABASE_VERSION = 5
        private const val DATABASE_NAME = "prefs"
        private const val TABLE_USER_PREFERENCES = "preferences"

        private const val COLUMN_ID = "_id"
        private const val COLUMN_USER_EMAIL = "email"
        private const val COLUMN_PREFS_DIFFICULTY = "difficulty"
        private const val COLUMN_PREFS_COUNT = "count"
        private const val COLUMN_PREFS_TYPE = "type"
        private const val COLUMN_PREFS_CATEGORY = "category"

        private const val TABLE_CURRENT_USER_QUESTIONS = "questions"

        private const val COLUMN_QUESTION_TEXT = "question_text"
        private const val COLUMN_QUESTION_ANSWER = "question_answer"
        private const val COLUMN_WRONG_ANSWER_1 = "wrong1"
        private const val COLUMN_WRONG_ANSWER_2 = "wrong2"
        private const val COLUMN_WRONG_ANSWER_3 = "wrong3"
        private const val COLUMN_QUESTION_TYPE = "type"
        private const val COLUMN_WAS_ANSWERED = "answered"
        private const val COLUMN_CORRECT_ANSWER = "correct"


        private const val TABLE_PREVIOUS_USER_QUESTIONS = "previous_questions"
        //uses all the current questions table columns except has been answered since these are old questions
    }

    fun newCurrentQuestionsTable() {
        //function to move all the previous days questions and get a fresh table to be called when timer goes off

        //creates the tables we need if they don't exist yet
        val currentQuestionsTable =
            "CREATE	TABLE IF NOT EXISTS $TABLE_CURRENT_USER_QUESTIONS($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_USER_EMAIL TEXT, $COLUMN_QUESTION_TEXT TEXT, $COLUMN_QUESTION_ANSWER TEXT, " +
                    "$COLUMN_WRONG_ANSWER_1 TEXT, $COLUMN_WRONG_ANSWER_2 TEXT, $COLUMN_WRONG_ANSWER_3 TEXT," +
                    " $COLUMN_QUESTION_TYPE TEXT, $COLUMN_WAS_ANSWERED TEXT DEFAULT FALSE," +
                    " $COLUMN_CORRECT_ANSWER TEXT DEFAULT FALSE)"
        val oldQuestionsTable =
            "CREATE	TABLE IF NOT EXISTS $TABLE_PREVIOUS_USER_QUESTIONS($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_USER_EMAIL TEXT, $COLUMN_QUESTION_TEXT TEXT, $COLUMN_QUESTION_ANSWER TEXT, " +
                    "$COLUMN_WRONG_ANSWER_1 TEXT, $COLUMN_WRONG_ANSWER_2 TEXT, $COLUMN_WRONG_ANSWER_3 TEXT," +
                    "$COLUMN_QUESTION_TYPE TEXT, $COLUMN_WAS_ANSWERED TEXT DEFAULT FALSE," +
                    " $COLUMN_CORRECT_ANSWER TEXT DEFAULT FALSE)"

        //now we move all the current questions into old questions table
        val moveCurrentQuestions =
            "INSERT INTO $TABLE_PREVIOUS_USER_QUESTIONS SELECT * FROM $TABLE_CURRENT_USER_QUESTIONS"
        //here we drop the table so we can make it again to reset it
        val dropTable =
            "DROP TABLE $TABLE_CURRENT_USER_QUESTIONS"

        val db = this.writableDatabase
        //make the tables
        Log.i("test306", "creating tables")
        db.execSQL(currentQuestionsTable)
        db.execSQL(oldQuestionsTable)
        //move the questions over
        Log.i("test306", "moving questions")
        db.execSQL(moveCurrentQuestions)
        //reset the current questions table
        Log.i("test306", "resetting table")
        db.execSQL(dropTable)
        db.execSQL(currentQuestionsTable)
    }

    fun getCurrentQuestions() : ArrayDeque<Question>{
        //get the current questions table as an array dequeue
        val query = "Select * FROM $TABLE_CURRENT_USER_QUESTIONS"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var questionArray : ArrayDeque<Question> = ArrayDeque()
        while (cursor.moveToNext()) {
            //id and email are 0 and 1
            val text = cursor.getString(2)
            val answer = cursor.getString(3)
            val incorrectAnswer1 = cursor.getString(4)
            val incorrectAnswer2 = cursor.getString(5)
            val incorrectAnswer3 = cursor.getString(6)
            val type = cursor.getString(7)
            val answered = cursor.getString(8)
            val correct = cursor.getString(9)

            val incorrectAnswerArray : Array<String> = arrayOf(incorrectAnswer1, incorrectAnswer2, incorrectAnswer3)
            val question = Question(text, answer, incorrectAnswerArray, answered.toBoolean(), correct.toBoolean(), type)
            questionArray.add(question)
        }
        cursor.close()

        return questionArray
    }

    fun getOldQuestions() {
        //get the users previous questions

    }

    fun answerAQuestion(id : String, correct : Boolean) {
        //boolean param is if the question was correctly answered, update table to reflect

    }

    fun getQuestionAnswer(id : String) : String {
        //get a question by its id and return the correct answer for checking
        val query = "Select * FROM $TABLE_CURRENT_USER_QUESTIONS WHERE $COLUMN_ID = $id"
        val db = this.writableDatabase
        var answer: String? = null

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            answer = cursor.getString(0)
        }
        cursor.close()

        if (answer == null) {
            answer = ""
        }
        return answer
    }

    fun addQuestions(questions: ArrayDeque<Question>) {
        //here we add the questions from the dequeue to the database
        while (questions.isNotEmpty()) {
            val question = questions.removeFirst()
            //format the question and add to db
            val values = ContentValues()
            currentUser = auth.currentUser
            val email = currentUser?.email

            values.put(COLUMN_USER_EMAIL, email)
            values.put(COLUMN_QUESTION_TEXT, question.text)
            values.put(COLUMN_QUESTION_ANSWER, question.answer)
            values.put(COLUMN_WRONG_ANSWER_1, question.incorrectAnswers[0])
            values.put(COLUMN_WRONG_ANSWER_2, question.incorrectAnswers[1])
            values.put(COLUMN_WRONG_ANSWER_3, question.incorrectAnswers[2])
            values.put(COLUMN_QUESTION_TYPE, question.type)
            values.put(COLUMN_WAS_ANSWERED, false)
            values.put(COLUMN_CORRECT_ANSWER, false)
            val db = this.writableDatabase
            db.insert(TABLE_CURRENT_USER_QUESTIONS, null, values)
        }
    }
    /*
    fun updateTask(task: Task) {
        val values = ContentValues()
        values.put(COLUMN_TASK_TITLE, task.name)
        val db = this.writableDatabase
        db.update(
            TABLE_TASKS,
            values,
            "$COLUMN_ID	= ?",
            arrayOf((task.id).toString())
        )
    }

    fun findTask(name: String): Task? {
        val query = "Select * FROM $TABLE_TASKS WHERE $COLUMN_TASK_TITLE = name"
        val db = this.writableDatabase
        var mTask: Task? = null
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(0))
            val taskName = cursor.getString(1)
            mTask = Task(id, taskName)
        }
        cursor.close()
        return mTask
    }
*/
    fun findUserPrefs(email: String): UserPreferences? {
        Log.i("test306", "in find prefs")
        val query = "Select * FROM $TABLE_USER_PREFERENCES WHERE $COLUMN_USER_EMAIL = email"
        val db = this.writableDatabase
        var mUserPref: UserPreferences? = null
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(0))
            Log.i("test306", id.toString())
            val uEmail = cursor.getString(1)
            Log.i("test306", uEmail.toString())
            val uDifficulty = cursor.getString(2)
            Log.i("test306", uDifficulty.toString())
            val uCount = cursor.getString(3)
            Log.i("test306", uCount.toString())
            val uType = cursor.getString(4)
            Log.i("test306", uType.toString())
            val uCategory = cursor.getString(5)
            Log.i("test306", uCategory.toString())
            mUserPref = UserPreferences(uEmail, uDifficulty, uCount, uType, uCategory)
        }
        cursor.close()
        return mUserPref
    }

    //this should work but cant be tested yet
    //take in user preferences to update in the database for the corresponding user
    fun updatePreferences(email : String, difficulty : String, count : String, type : String, category : String) {
        Log.i("test306", "in update prefs")
        val values = ContentValues()
        values.put(COLUMN_PREFS_DIFFICULTY, difficulty)
        values.put(COLUMN_PREFS_COUNT, count)
        values.put(COLUMN_PREFS_TYPE, type)
        values.put(COLUMN_PREFS_CATEGORY, category)
        val db = this.writableDatabase
        db.update(
            TABLE_USER_PREFERENCES,
            values,
            "$COLUMN_USER_EMAIL = email",
            null)
    }
}