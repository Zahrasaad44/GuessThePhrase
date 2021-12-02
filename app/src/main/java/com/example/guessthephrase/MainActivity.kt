package com.example.guessthephrase

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var rootLayout: ConstraintLayout
    private lateinit var phraseTV: TextView
    private lateinit var guessedLettersTV: TextView
    private lateinit var highScoreTV: TextView
    private lateinit var phraseRecyclerView: RecyclerView
    private lateinit var userInputTextField: EditText
    private lateinit var guessBtn: Button

    private lateinit var entries: ArrayList<String>

    private val phrase = "more time is needed"
    private val myPhraseDictionary = mutableMapOf<Int, Char>()
    private var myPhrase = ""
    private var guessedLetters = ""
    private var count = 0    // Used to count the number of times the user guessed
    private var guessPhrase = true
    private var score = 0
    private var highScore = 0

    private lateinit var preferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootLayout = findViewById(R.id.rootLayout)
        phraseTV = findViewById(R.id.phraseTV)
        guessedLettersTV = findViewById(R.id.guessedLettersTV)
        highScoreTV = findViewById(R.id.highScoreTV)
        userInputTextField = findViewById(R.id.userInputTextField)
        guessBtn = findViewById(R.id.guessBtn)
        phraseRecyclerView = findViewById(R.id.phraseRecyclerView)

        entries = ArrayList()

        phraseRecyclerView.adapter = PhraseAdapter(this, entries)
        phraseRecyclerView.layoutManager = LinearLayoutManager(this)



        for(i in phrase.indices){  // To replace the text in the TextView with space if the phrase has a space and replace the letters with *
            if(phrase[i] == ' ') {
                myPhraseDictionary[i] = ' '
                myPhrase += ' '
            } else {
                myPhraseDictionary[i] = '*'
                myPhrase += '*'
            }
        }

        guessBtn.setOnClickListener { addUserGuesses() }

        updateText()

        preferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        highScore = preferences.getInt("HighScore", 0)

        highScoreTV.text = "High Score: $highScore"
    }

    private fun addUserGuesses () {
        val userPhrase = userInputTextField.text.toString()

        if(guessPhrase) {
            if(userPhrase == phrase) {
                disableEntry()
                updateScore()
                showAlert("You win!\n\nPlay again?")
            } else {
                entries.add("Wrong guess: $userPhrase")
                guessPhrase = false
                updateText()
            }
            } else {
                if(userPhrase.isNotEmpty() && userPhrase.length == 1) {
                    myPhrase = ""
                    guessPhrase = true
                    checkLetters(userPhrase[0])
                } else {
                    Snackbar.make(rootLayout, "Please enter one letter only", Snackbar.LENGTH_LONG).show()
                }
            }
        userInputTextField.text.clear()
        phraseRecyclerView.adapter?.notifyDataSetChanged()
        }

    private fun checkLetters(guessedLetter: Char) {
        var lettersFound = 0
        for (i in phrase.indices) {
            if (phrase[i] == guessedLetter) {
                myPhraseDictionary[i] = guessedLetter
                lettersFound++
            }
        }
        for (i in myPhraseDictionary) {
            myPhrase += myPhraseDictionary[i.key]
        }
        if (myPhrase == phrase) {
            disableEntry()
            updateScore()
            showAlert("You win!!\n\nWant to play again?")
        }
        if (guessedLetters.isEmpty()) {
            guessedLetters += guessedLetter
        } else {
            guessedLetters +=", "+guessedLetter
        }
        if (lettersFound > 0) {
            entries.add("Found $lettersFound ${guessedLetter.toUpperCase()}(s)")
        } else {
            entries.add("No (${guessedLetter.toUpperCase()})s found")
        }
        count++

        val guessesLeft = 10 - count
        if (count < 10) {
            entries.add("$guessesLeft guesses remaining")
        } else {
            disableEntry()
            showAlert("You lose :(\n\nYou guessed $count times\n\nWant to play again?")
        }
        updateText()
        phraseRecyclerView.scrollToPosition(entries.size - 1)  // To scroll to the last input the user entered
    }

    private fun updateText() {
        phraseTV.text = "Phrase: " + myPhrase.toUpperCase()
        guessedLettersTV.text = "Guessed letters: " + guessedLetters

        if (guessPhrase) {
            userInputTextField.hint = "Guess the full phrase"
        } else {
            userInputTextField.hint = "Guess a letter"
        }
    }

    private fun updateScore() {
        score = 10 - count
        if(score >= highScore) {  // The highest score it can be is 10
            highScore = score
            with(preferences.edit()) {
                putInt("HighScore", highScore )
                apply()
            }
        }
    }

    private fun disableEntry() {
        userInputTextField.isClickable = false
        userInputTextField.isEnabled = false
        guessBtn.isEnabled = false
        guessBtn.isClickable = false
    }

    private fun showAlert(title: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(title)
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener {
                    dialog, id -> this.recreate()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })
        val alert = dialogBuilder.create()
        alert.setTitle("Game Over")
        alert.show()
    }

}