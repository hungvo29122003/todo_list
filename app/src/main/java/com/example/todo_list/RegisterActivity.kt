package com.example.todo_list

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.todo_list.databinding.ActivityRegisterBinding
import com.example.todo_list.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var btn_back: ImageView
    private lateinit var register_button: AppCompatButton
    private lateinit var register_with_gg_button: AppCompatButton
    private lateinit var edit_email: EditText
    private lateinit var edit_password: EditText
    private lateinit var edit_confirm_password: EditText
    private lateinit var registerViewModel : RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btn_back = binding.backButton
        register_button = binding.registerButton
        register_with_gg_button = binding.registerWithGgButton
        edit_email = binding.editEmail
        edit_password = binding.ediPassword
        edit_confirm_password = binding.editConfirmPassword
        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        btn_back.setOnClickListener {
            finish()
        }
        edit_email.addTextChangedListener{
            registerViewModel.setEmail(it.toString())
        }
        edit_password.addTextChangedListener {
            registerViewModel.setPassword(it.toString())
        }
        edit_confirm_password.addTextChangedListener {
            registerViewModel.setConfirmPassword(it.toString())
        }

        register_button.setOnClickListener {
            registerViewModel.register()
        }

        registerViewModel.registerSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("email", edit_email.text.toString())
                startActivity(intent)
                finish()
            }
        }
        registerViewModel.registerError.observe(this){ errorMessage ->
            if (!errorMessage.isNullOrEmpty()){
                Toast.makeText(this,"Lỗi: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }

        register_with_gg_button.setOnClickListener {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
        }
    }
}