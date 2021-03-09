package com.example.psso

//import android.R
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
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
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class GradesActivity : AppCompatActivity() {


    lateinit var layout: LinearLayout
    var listOfSemesters = arrayListOf<String>("Alle Semester")
    val PREF_NAME = "myPrefs"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grades)
        val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        if(!sharedPreferences.contains("SEMESTER_SELECT")) {
            editor.putInt("SEMESTER_SELECT", 0)
            editor.putString("LAST_SEMESTER","Alle Semester")
            editor.apply()
        }



        supportActionBar?.hide()
        layout = findViewById<LinearLayout>(R.id.scrollContainer)

        generateView(sharedPreferences.getString("LAST_SEMESTER","Alle Semester") as String)


        val semesterButton = findViewById<Button>(R.id.semesterButton)
        var clickedItem = sharedPreferences.getInt("SEMESTER_SELECT",0)
        semesterButton.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View?) {

                    var los = listOfSemesters.toTypedArray()
                    val builder = AlertDialog.Builder(this@GradesActivity,R.style.DarkDialogTheme)

                        .setTitle("Semester auswählen")
                        .setNeutralButton("cancel") { dialog, which ->
                            // Respond to neutral button press
                        }
                        .setPositiveButton("ok") { dialog, which ->
                            editor.putInt("SEMESTER_SELECT",clickedItem)
                            editor.putString("LAST_SEMESTER",los[clickedItem])
                            editor.apply()
                            println(sharedPreferences.getInt("SEMESTER_SELECT",0))
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
                    val activity2Intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(activity2Intent)
                    finish()
                }
            })

    }


    fun generateView(a: String = "Alle Semester") {
        val extras = intent.extras
        var sortBy = a

        if (layout.childCount > 0)
            layout.removeAllViewsInLayout()


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
                var semester = t[0]
                var fach = t[1]
                var note = t[2]
                if (note == "")
                    note = "--"

                val notenView = TextView(this)
                val fachView = TextView(this)


                var fachText = fach
                var notenText = note + "\n\n"

                fachView.text = fachText
                fachView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                fachView.setGravity(Gravity.CENTER)
                fachView.setTextColor(Color.WHITE)
                fachView.setTypeface(Typeface.MONOSPACE)

                notenView.text = notenText
                notenView.setGravity(Gravity.CENTER)
                notenView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                notenView.setTextColor(Color.WHITE)
                notenView.setTypeface(Typeface.MONOSPACE)

                layout.addView(fachView)
                layout.addView(notenView)


            }
            if (sortBy == "Alle Semester") {

                var semester = t[0]
                var fach = t[1]
                var note = t[2]

                val notenView = TextView(this)
                val fachView = TextView(this)
                val semesterView = TextView(this)

                var fachText = fach + "\n"
                var semesterText = semester + "\n"
                var notenText = note + "\n\n"
                fachView.text = fachText
                notenView.text = notenText
                semesterView.text = semesterText

                fachView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                fachView.setGravity(Gravity.CENTER)
                fachView.setTextColor(Color.WHITE)
                fachView.setTypeface(Typeface.MONOSPACE)

                semesterView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                semesterView.setGravity(Gravity.CENTER)
                semesterView.setTextColor(Color.WHITE)
                semesterView.setTypeface(Typeface.MONOSPACE)

                notenView.setGravity(Gravity.CENTER)
                notenView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                notenView.setTextColor(Color.WHITE)
                notenView.setTypeface(Typeface.MONOSPACE)

                layout.addView(fachView)
                layout.addView(semesterView)
                layout.addView(notenView)
            }

        }



    }
}






