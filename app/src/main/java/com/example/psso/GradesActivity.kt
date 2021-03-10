package com.example.psso


import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys


class GradesActivity : AppCompatActivity() {


    lateinit var scrollContainer: LinearLayout
    var listOfSemesters = arrayListOf("Alle Semester")
    val PREF_NAME = "myPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grades)
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            PREF_NAME,
            masterKey,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val editor = sharedPreferences.edit()


        if (!sharedPreferences.contains("SEMESTER_SELECT")) {
            editor.putInt("SEMESTER_SELECT", 0)
            editor.putString("LAST_SEMESTER", "Alle Semester")
            editor.apply()
        }



        supportActionBar?.hide()
        scrollContainer = findViewById(R.id.scrollContainer)

        generateView(sharedPreferences.getString("LAST_SEMESTER", "Alle Semester") as String)


        val semesterButton = findViewById<Button>(R.id.semesterButton)
        var clickedItem = sharedPreferences.getInt("SEMESTER_SELECT", 0)
        semesterButton.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View?) {

                    val los = listOfSemesters.toTypedArray()
                    AlertDialog.Builder(this@GradesActivity, R.style.DarkDialogTheme)

                        .setTitle("Semester auswählen")
                        .setNeutralButton("cancel") { dialog, which ->
                            // Respond to neutral button press
                        }
                        .setPositiveButton("ok") { dialog, which ->
                            editor.putInt("SEMESTER_SELECT", clickedItem)
                            editor.putString("LAST_SEMESTER", los[clickedItem])
                            editor.apply()
                            println(sharedPreferences.getInt("SEMESTER_SELECT", 0))
                            generateView(los[clickedItem])
                        }
                        // Single-choice items (initialized with checked item)
                        .setSingleChoiceItems(los, clickedItem) { dialog, which ->
                            clickedItem = which
                        }
                        .show()
                }
            })


        val refreshButton = findViewById<Button>(R.id.refreshButton)

        refreshButton.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View?) {


                    AlertDialog.Builder(this@GradesActivity, R.style.DarkDialogTheme)


                        .setNegativeButton("Notenliste aktualisieren") {dialog , which ->
                            val activity2Intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(activity2Intent)
                            finish()
                        }
                        .setPositiveButton("App zurücksetzen") { dialog, which ->
                            editor.clear()
                            editor.apply()
                            val activity2Intent =
                                Intent(applicationContext, MainActivity::class.java)
                            startActivity(activity2Intent)
                            finish()
                        }

                        .show()




                }
            })

    }


    fun generateView(sortBy: String = "Alle Semester") {
        val extras = intent.extras


        if (scrollContainer.childCount > 0)
            scrollContainer.removeAllViewsInLayout()


        val label = findViewById<TextView>(R.id.labelView)
        label.text = sortBy


        var table: ArrayList<Array<String>> = ArrayList(0)
        if (extras != null) {
            table = extras.getSerializable("data") as ArrayList<Array<String>>
        }


        for (t in table) {


            if (!listOfSemesters.contains(t[0]))
                listOfSemesters.add(t[0])


            // t.format[Semester,fach,note]
            if (sortBy != "" && t[0] == sortBy) {

                val fach = t[1]
                var note = t[2]
                if (note == "")
                    note = "--"

                val notenView = TextView(this)
                val fachView = TextView(this)


                val fachText = fach
                val notenText = note + "\n\n"

                fachView.text = fachText
                fachView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                fachView.gravity = Gravity.CENTER
                fachView.setTextColor(Color.WHITE)
                fachView.typeface = Typeface.MONOSPACE

                notenView.text = notenText
                notenView.gravity = Gravity.CENTER
                notenView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                notenView.setTextColor(Color.WHITE)
                notenView.typeface = Typeface.MONOSPACE

                scrollContainer.addView(fachView)
                scrollContainer.addView(notenView)


            }
            if (sortBy == "Alle Semester") {

                val semester = t[0]
                val fach = t[1]
                var note = t[2]
                if (note == "")
                    note = "--"


                val notenView = TextView(this)
                val fachView = TextView(this)
                val semesterView = TextView(this)

                val fachText = fach
                val semesterText = semester
                val notenText = note + "\n\n"


                fachView.text = fachText
                notenView.text = notenText
                semesterView.text = semesterText

                fachView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                fachView.gravity = Gravity.CENTER
                fachView.setTextColor(Color.WHITE)
                fachView.typeface = Typeface.MONOSPACE

                semesterView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                semesterView.gravity = Gravity.CENTER
                semesterView.setTextColor(Color.WHITE)
                semesterView.typeface = Typeface.MONOSPACE

                notenView.gravity = Gravity.CENTER
                notenView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                notenView.setTextColor(Color.WHITE)
                notenView.typeface = Typeface.MONOSPACE

                scrollContainer.addView(fachView)
                scrollContainer.addView(semesterView)
                scrollContainer.addView(notenView)


            }


        }


    }
}






