package com.example.psso

//import android.R
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class GradesActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grades)
        supportActionBar?.hide()
        val layout = findViewById<LinearLayout>(R.id.linear_layout)
        val extras = intent.extras
        var grades : ArrayList<Pair<String,String>> = ArrayList(0)
        if (extras!=null){
            grades = extras.getSerializable("data") as ArrayList<Pair<String,String>>
        }




        if(grades.size >0) {
            for (grade in grades) {
                val note = TextView(this)
                val fach = TextView(this)

                val fachText = grade.first

                val notenText = grade.second + "\n\n"
                fach.text = fachText
                note.text = notenText
                fach.setGravity(Gravity.CENTER)
                fach.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                fach.setGravity(Gravity.CENTER)
                note.setGravity(Gravity.CENTER)
                note.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)

                layout.addView(fach)
                layout.addView(note)
            }
        }

        val button = Button(this)
        button.setText("Reload")

        button.setGravity(Gravity.CENTER)
        layout.addView(button)


        //val buttonOne = findViewById(R.id.ReloadButton) as Button
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val activity2Intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(activity2Intent)
                finish()
            }
        })

    }

}




