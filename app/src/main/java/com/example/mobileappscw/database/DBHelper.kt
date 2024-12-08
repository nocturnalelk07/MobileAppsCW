package com.example.mobileappscw.database

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
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
    /*
        fun listTasks(): MutableList<Task> {
            val sql = "select * from $TABLE_TASKS"
            val db = this.readableDatabase
            val storeTasks = arrayListOf<Task>()
            val cursor = db.rawQuery(sql, null)
            if (cursor.moveToFirst()) {
                do {
                    val id = Integer.parseInt(cursor.getString(0))
                    val name = cursor.getString(1)
                    storeTasks.add(Task(id, name))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return storeTasks
        }

        fun addTask(taskName: String) {
            val values = ContentValues()
            values.put(COLUMN_TASK_TITLE, taskName)
            val db = this.writableDatabase
            db.insert(TABLE_TASKS, null, values)
        }
    */
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

    /*
        fun deleteTask(id: Int) {
            val db = this.writableDatabase
            db.delete(TABLE_TASKS, "$COLUMN_ID	= ?", arrayOf(id.toString()))
        }
    */
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
        private const val COLUMN_WAS_ANSWERED = "answered"
        private const val COLUMN_CORRECT_ANSWER = "correct"
        private const val COLUMN_QUESTION_TYPE = "type"

        private const val TABLE_PREVIOUS_USER_QUESTIONS = "previous_questions"
        //uses all the current questions table columns except has been answered since these are old questions
    }

    fun newCurrentQuestionsTable() {
        //function to move all the previous days questions and get a fresh table to be called when timer goes off

        //creates the tables we need if they don't exist yet
        val currentQuestionsTable =
            "CREATE	TABLE IF NOT EXISTS $TABLE_CURRENT_USER_QUESTIONS($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_USER_EMAIL TEXT, $COLUMN_QUESTION_TEXT TEXT, $COLUMN_QUESTION_ANSWER TEXT, " +
                    "$COLUMN_QUESTION_TYPE TEXT, $COLUMN_WAS_ANSWERED TEXT DEFAULT FALSE, $COLUMN_CORRECT_ANSWER TEXT DEFAULT FALSE)"
        val oldQuestionsTable =
            "CREATE	TABLE IF NOT EXISTS $TABLE_PREVIOUS_USER_QUESTIONS($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_USER_EMAIL TEXT, $COLUMN_QUESTION_TEXT TEXT, $COLUMN_QUESTION_ANSWER TEXT, " +
                    "$COLUMN_QUESTION_TYPE TEXT, $COLUMN_WAS_ANSWERED TEXT DEFAULT FALSE, $COLUMN_CORRECT_ANSWER TEXT DEFAULT FALSE)"

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

    fun getCurrentQuestions() {
        //get the current questions table
    }

    fun getOldQuestions() {
        //get the users previous questions
    }

    fun answerAQuestion(correct : Boolean) {
        //boolean param is if the question was correctly answered, update table to reflect
    }

    fun getQuestionAnswer(id : String) : String {
        //get a question by its id and return the correct answer for checking
        Log.i("test306", "in question answer")
        val query = "Select * FROM $TABLE_CURRENT_USER_QUESTIONS WHERE $COLUMN_ID = 1"
        Log.i("test306", query)
        val db = this.writableDatabase
        var answer: String? = null
        Log.i("test306", "creating cursor")
        val cursor = db.rawQuery(query, null)
        Log.i("test306", "moving to first")
        if (cursor.moveToFirst()) {
            Log.i("test306", "getting string")
            answer = cursor.getString(0)
        }
        Log.i("test306", "closing cursor")
        cursor.close()
        if (answer == null) {
            answer = ""
        }
        return answer

        //Log.i("test306", "in get question answer")
        //val query = "Select * FROM $TABLE_CURRENT_USER_QUESTIONS WHERE $COLUMN_ID = id"
        //val db = this.writableDatabase
        //var mUserPref: UserPreferences? = null
        //val cursor = db.rawQuery(query, null)
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