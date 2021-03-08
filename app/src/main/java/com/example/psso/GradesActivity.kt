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
import org.jsoup.nodes.Element


class GradesActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grades)
        supportActionBar?.hide()
        val layout = findViewById<LinearLayout>(R.id.linear_layout)
        val extras = intent.extras
        var grades : ArrayList<Pair<String,String>> = ArrayList(0)
        var table : ArrayList<Array<String>> = ArrayList(0)
        if (extras!=null){
            table = extras.getSerializable("data") as ArrayList<Array<String>>
        }

        for (t in table) {
                // t.format[Semester,fach,note]
                    print(t[0].toString())
            print(t[1].toString())
            print(t[2].toString())
                    var note = t[0]
                    if (note == "")
                        note = "--"
                    grades.add(Pair(t[1], note))

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




