package com.example.psso

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys


class LoginActivity : AppCompatActivity() {

    var userMail = ""
    var userPwd = ""
    val PREF_NAME = "myPrefs"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            PREF_NAME,
            masterKey,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val editor = sharedPreferences.edit()
        editor.putString("username", "")
        editor.putString("pwd", "")
        editor.apply()
        val button = findViewById<Button>(R.id.loginButton)
        val mailView = findViewById<EditText>(R.id.loginMail)
        val pwdView = findViewById<EditText>(R.id.loginPassword)

        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                userMail = mailView.text.toString()
                userPwd = pwdView.text.toString()
                if(userMail != "" && userPwd != "") {
                    val resultIntent = Intent()
                    resultIntent.putExtra("mail", userMail)
                    resultIntent.putExtra("pwd",userPwd)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }


            }
        })





    }




}