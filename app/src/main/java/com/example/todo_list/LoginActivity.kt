package com.example.todo_list

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo_list.databinding.ActivityLoginBinding
import com.google.android.material.imageview.ShapeableImageView

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var login_button: AppCompatButton
    private lateinit var btn_back: ShapeableImageView
    private lateinit var login_with_gg_button: AppCompatButton
    private lateinit var login_with_fb_button: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btn_back = binding.btnBack
        login_button = binding.loginButton
        login_with_gg_button = binding.loginWithGgButton
        login_with_fb_button = binding.loginWithFbButton
        btn_back.setOnClickListener{
            finish()
        }

        login_button.setOnClickListener {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
        }

        login_with_gg_button.setOnClickListener {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
        }

        login_with_fb_button.setOnClickListener {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
        }

    }
}