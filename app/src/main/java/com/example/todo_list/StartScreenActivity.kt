package com.example.todo_list

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo_list.databinding.ActivityStartScreenBinding

class StartScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartScreenBinding
    private lateinit var login_button: Button
    private lateinit var register_button: Button
    private lateinit var btn_back: ImageView
    companion object{
        private const val PREFS_NAME = "MyAppPrefs"
        private const val KEY_USER_ID = "firebase_user_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStartScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        login_button = binding.loginButton
        register_button = binding.registerButton
        btn_back = binding.btnBack

        login_button.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        register_button.setOnClickListener {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
        }
        btn_back.setOnClickListener{
            finish()
        }
    }
    fun getUserIdFromPrefs(): String? {
        val sharedPrefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString(KEY_USER_ID, null)
    }


    override fun onStart() {
        super.onStart()
        val userId = getUserIdFromPrefs()
        if (userId != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}