package com.example.todo_list

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.todo_list.databinding.ActivityLoginBinding
import com.example.todo_list.view.LoginViewModel
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var login_button: AppCompatButton
    private lateinit var btn_back: ShapeableImageView
    private lateinit var login_with_gg_button: AppCompatButton
    private lateinit var login_with_fb_button: AppCompatButton
    private lateinit var register_button: TextView
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var edi_email: EditText
    private lateinit var edt_password: EditText
    private val db = Firebase.firestore // THÊM DÒNG NÀY

    companion object {
        private const val PREFS_NAME = "MyAppPrefs"
        private const val KEY_USER_ID = "firebase_user_id"
        const val EXTRA_EMAIL = "email"
        const val EXTRA_DISPLAY_NAME = "displayName"

    }

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
        register_button = binding.registerButton
        edt_password = binding.edtPassword
        edi_email = binding.edtEmail
        btn_back.setOnClickListener {
            finish()
        }
        auth = FirebaseAuth.getInstance()
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        val emailFormRegister = intent.getStringExtra("email")
        emailFormRegister?.let {
            edi_email.setText(it)
            loginViewModel.setEmail(it)
        }

        edi_email.addTextChangedListener {
            loginViewModel.setEmail(it.toString())
        }
        edt_password.addTextChangedListener {
            loginViewModel.setPassword(it.toString())
        }



        login_button.setOnClickListener {
            loginViewModel.logic()
        }

        loginViewModel.loginSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                val currentUser = auth.currentUser
                currentUser?.let { user ->
                    saveUserIdToPress(user.uid)
                    Log.d("LOGIN", "User ID = ${user.uid}")
                    Toast.makeText(
                        this,
                        "Đăng nhập thành công! User ID: ${user.uid}",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, DisplayNameActivity::class.java)
                    startActivity(intent)
                    finish()
                    // --- GỬI EMAIL CHÀO MỪNG QUA FIRESTORE EXTENSION ---
                    sendWelcomeEmailToFirestore(user.email, user.displayName)
                    // --- KẾT THÚC GỬI EMAIL ---
                    Log.d("EmailTrigger", "User logged in: ${user.email}")
                } ?: run {
                    Toast.makeText(
                        this,
                        "Đăng nhập thành công nhưng không lấy được User ID.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        loginViewModel.loginError.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, "Lỗi: $it", Toast.LENGTH_SHORT).show()
            }
        }

        login_with_gg_button.setOnClickListener {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
        }

        login_with_fb_button.setOnClickListener {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
        }
        register_button.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveUserIdToPress(userId: String) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    private fun saveDisplayNameToPrefs(displayName: String) {
        val sharedPrefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString(EXTRA_DISPLAY_NAME, displayName)
        editor.apply()
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

    private fun sendWelcomeEmailToFirestore(email: String?, displayName: String?) {
        if (email.isNullOrEmpty()) {
            Log.w("FirestoreEmail", "Email is null or empty, cannot send welcome email.")
            return
        }

        // Tùy chỉnh nội dung email
        val recipientName = displayName ?: email.split('@')[0]
        saveDisplayNameToPrefs(recipientName)
        Log.d("DisplayName", "Recipient Name: $recipientName")
        val subject = "Chào mừng ${recipientName} đến với ứng dụng Todo List!"
        val htmlContent = """
            <p>Kính gửi <strong>$recipientName</strong>,</p>
            <p>Cảm ơn bạn đã đăng nhập vào ứng dụng Todo List của chúng tôi!</p>
            <p>Chúng tôi rất vui được chào đón bạn. Hãy bắt đầu quản lý các công việc của mình ngay bây giờ.</p>
            <br>
            <p>Trân trọng,</p>
            <p>Đội ngũ phát triển Todo List</p>
            <p><small>Email này được gửi tự động, vui lòng không trả lời.</small></p>
        """.trimIndent()

//        val emailDocument = hashMapOf(
//            "to" to email,
//            "subject" to subject,
//            "html" to htmlContent
//        )
        val emailDocument = hashMapOf(
            "to" to email, // PHẢI là List<String>, không phải String đơn
            "message" to mapOf(
                "subject" to subject,
                "html" to htmlContent
            )
        )

        // Thêm document vào collection 'mail' (hoặc tên collection bạn đã cấu hình cho Extension)
        db.collection("email_user") // ĐẢM BẢO TÊN COLLECTION NÀY KHỚP VỚI CẤU HÌNH EXTENSION CỦA BẠN
            .add(emailDocument)
            .addOnSuccessListener { documentReference ->
                Log.d("FirestoreEmail", "Email document added with ID: ${documentReference.id}")
                Toast.makeText(this, "Yêu cầu gửi email đã được tạo.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreEmail", "Error adding email document", e)
                Toast.makeText(
                    this,
                    "Lỗi khi tạo yêu cầu gửi email: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

}