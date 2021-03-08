package com.example.psso

import android.app.Activity
import android.content.Context

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

import org.jsoup.Connection
import org.jsoup.Jsoup
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    val LOGIN_ACTIVITY = 0
    val PREF_NAME = "myPrefs"

    internal val Background = newFixedThreadPoolContext(2, "bg")


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



            if (resultCode == Activity.RESULT_OK) {
                val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                if (data != null) {
                    editor.putString("username",data.getStringExtra("mail") as String)
                    editor.putString("pwd",data.getStringExtra("pwd") as String)
                    editor.apply()
                    var username = sharedPreferences.getString("username","") as String
                    var pwd = sharedPreferences.getString("pwd","") as String

                    startCrawling(username,pwd)
                }




        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        var username = sharedPreferences.getString("username","") as String
        var pwd = sharedPreferences.getString("pwd","") as String


            if (username == "" && pwd == "") {
                val i = Intent(this, LoginActivity::class.java);
                startActivityForResult(i, LOGIN_ACTIVITY);

        }
        else{

                startCrawling(username,pwd)
            }



    }

    fun startCrawling(username :String, pwd : String) {
        val job = GlobalScope.launch(Background) {
            val grades = getGrades(username, pwd)
            val mainIntent = Intent(applicationContext, GradesActivity::class.java)
            mainIntent.putExtra("data", grades)
            startActivity(mainIntent)
            finish()


        }

    }


    fun getGrades(username :String, pwd : String): ArrayList<Pair<String, String>> {
        var toRet = ArrayList<Pair<String, String>>()

        if(username == "" || pwd == ""){
            println("No username or password")
            return toRet
        }
        val url =
            "https://psso.th-koeln.de/qisserver/rds?state=user&type=1&category=auth.login&startpage=portal.vm&breadCrumbSource=portal"
        println("Loading website")
        var response: Connection.Response =
            Jsoup.connect(url)

                .method(Connection.Method.POST)
                .data("asdf", username)
                .data("fdsa", pwd)
                .followRedirects(true)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .execute()
        println("website loaded")
        val doc = response.parse()
        var sessionId = ""
        if (response.hasCookie("JSESSIONID")) {
            sessionId = response.cookie("JSESSIONID")
        }


        var urls = doc.body().select("a[href]")
        var urlToNextPage = ""
        for (link in urls) {
            if (link.toString().contains("Prüfungsverwaltung"))
                urlToNextPage = link.attr("href").toString()
        }

        var newResponse: Connection.Response = Jsoup.connect(urlToNextPage)
            .cookie("JSESSIONID", sessionId)
            .execute()
        var newDoc = newResponse.parse()
        urls = newDoc.body().select("a[href]")
        urlToNextPage = ""
        for (link in urls) {

            if (link.toString().contains("Notenspiegel"))
                urlToNextPage = link.attr("href").toString()
        }

        newResponse = Jsoup.connect(urlToNextPage)
            .cookie("JSESSIONID", sessionId)
            .execute()
        newDoc = newResponse.parse()
        urls = newDoc.body().select("a[href]")
        urlToNextPage = ""
        for (link in urls) {

            if (link.toString().contains("Abschluss BA"))
                urlToNextPage = link.attr("href").toString()
        }


        newResponse = Jsoup.connect(urlToNextPage)
            .cookie("JSESSIONID", sessionId)
            .execute()
        newDoc = newResponse.parse()
        urls = newDoc.body().select("a[href]")
        urlToNextPage = ""

        for (link in urls) {
            if (link.toString().contains("Leistungen für Medientechnologie")) {
                urlToNextPage = link.attr("href")
            }
        }
        val targetResponse = Jsoup.connect(urlToNextPage)
            .cookie("JSESSIONID", sessionId)
            .execute()

        val targetDoc = targetResponse.parse()

        val table = targetDoc.select("tbody").select("tr")
        for (t in table) {
            if (t.children().size > 4) {

                if (t.children()[4].toString().contains("WS 20/21"))
                    if (t.children()[3].toString().contains("MO")) {
                        var note = t.children()[5].html()
                        if (note == "")
                            note = "--"
                        toRet.add(Pair(t.children()[1].html(), note))
                    }
            }
        }
        println("loading data finished")



        return toRet


    }
}


