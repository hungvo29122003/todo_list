package com.example.todo_list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo_list.StartScreenActivity.Companion
import com.example.todo_list.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var btn_logout: AppCompatButton
    companion object{
        const val EXTRA_DISPLAY_NAME = "displayName"
        private const val PREFS_NAME = "MyAppPrefs"
        private const val KEY_USER_ID = "firebase_user_id"
        private const val SAVE_NAME_COMPLETED = "save_name_completed"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btn_logout = binding.logoutButton
        btn_logout.setOnClickListener {
            clearSharedPreferences()
            Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, StartScreenActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
    private fun clearSharedPreferences() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        Log.d("MainActivity", "Clearing shared preferences: $sharedPreferences")
        val editor = sharedPreferences.edit()
        editor.remove(EXTRA_DISPLAY_NAME)
        editor.remove(KEY_USER_ID)
        editor.remove(SAVE_NAME_COMPLETED)
        editor.apply()
    }
}