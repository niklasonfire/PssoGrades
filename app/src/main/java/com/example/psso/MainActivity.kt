package com.example.psso

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import org.jsoup.Connection
import org.jsoup.Jsoup
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    val LOGIN_ACTIVITY = 0
    val PREF_NAME = "myPrefs"
    var username = ""
    var pwd = ""
    internal val Background = newFixedThreadPoolContext(2, "bg")


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if (resultCode == Activity.RESULT_OK) {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            val sharedPreferences = EncryptedSharedPreferences.create(
                PREF_NAME,
                masterKey,
                applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            username = sharedPreferences.getString("username", "") as String
            pwd = sharedPreferences.getString("pwd", "") as String

            startCrawling(username, pwd)


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            PREF_NAME,
            masterKey,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        username = sharedPreferences.getString("username", "") as String
        pwd = sharedPreferences.getString("pwd", "") as String


        if (username == "" && pwd == "") {
            val i = Intent(this, LoginActivity::class.java)
            startActivityForResult(i, LOGIN_ACTIVITY)

        } else {

            startCrawling(username, pwd)
        }


    }

    private fun startCrawling(username: String, pwd: String) {
        GlobalScope.launch(Background) {
            val grades = getGrades(username, pwd)

            if(grades.size == 0){

                val i = Intent(this@MainActivity, LoginActivity::class.java)
                startActivityForResult(i, LOGIN_ACTIVITY)
            }
            else{
                val mainIntent = Intent(applicationContext, GradesActivity::class.java)
                mainIntent.putExtra("data", grades)
                startActivity(mainIntent)
                finish()
            }




        }

    }


    private fun getGrades(username: String, pwd: String): ArrayList<Array<String>> {
        val toRet = ArrayList<Array<String>>()

        if (username == "" || pwd == "") {
            println("No username or password")
            return toRet
        }
        val url =
            "https://psso.th-koeln.de/qisserver/rds?state=user&type=1&category=auth.login&startpage=portal.vm&breadCrumbSource=portal"
        println("Loading website")
        val response: Connection.Response =
            Jsoup.connect(url)
                .method(Connection.Method.POST)
                .data("asdf", username)
                .data("fdsa", pwd)
                .followRedirects(true)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .execute()

        val doc = response.parse()
        var sessionId = ""
        if (response.hasCookie("JSESSIONID")) {
            sessionId = response.cookie("JSESSIONID")
        }
        var logInStatus = doc.body().select("div.divloginstatus").first().children()

        if(logInStatus.size <2)
            return toRet

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
        var count = 0

        for (t in table) {
            if (t.children().size > 4) {


                if (t.children()[3].toString().contains("MO")) {

                    val fach = t.children()[1].html()
                    var note = t.children()[5].html()
                    var sortHelper = ""
                    val semester = t.children()[4].html()
                    val tmpArr = arrayOf(semester, fach, note, sortHelper)
                    if (note == "")
                        note = "--"
                    toRet.add(tmpArr)
                    count++
                }
            }

        }

        println("loading data finished")



        return toRet


    }

}


