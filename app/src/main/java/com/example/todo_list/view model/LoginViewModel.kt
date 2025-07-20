package com.example.todo_list.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth


class LoginViewModel : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email
    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password
    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess
    private val _loginError = MutableLiveData<String?>()
    val loginError: LiveData<String?> = _loginError
    private val auth: FirebaseAuth = Firebase.auth

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()
        return emailRegex.matches(email)

    }


    fun logic() {
        val currentEmail = _email.value
        val currentPassword = _password.value

        if (currentEmail.isNullOrEmpty()) {
            _loginError.value = "Email không được để trống"
            return
        }
        if (currentPassword.isNullOrEmpty()){
            _loginError.value = "Mật khẩu không được để trống"
            return
        }
        if (!isValidEmail(currentEmail)){
            _loginError.value = "Định dạng Email không hợp lệ"
        }

        auth.signInWithEmailAndPassword(currentEmail, currentPassword).addOnCompleteListener {task ->
            if (task.isSuccessful){
                _loginSuccess.value = true
                _loginError.value = null
            } else {
                val errorMessage = if (task.exception is FirebaseAuthException) {
                    when ((task.exception as FirebaseAuthException).errorCode) {
                        "ERROR_USER_NOT_FOUND" -> "Tài khoản không tồn tại. Vui lòng đăng ký."
                        "ERROR_WRONG_PASSWORD" -> "Mật khẩu không đúng."
                        "ERROR_INVALID_EMAIL" -> "Địa chỉ email không hợp lệ."
                        "ERROR_INVALID_CREDENTIAL" -> "Thông tin đăng nhập không hợp lệ hoặc đã hết hạn." // Thêm lỗi này
                        "ERROR_TOO_MANY_REQUESTS" -> "Tài khoản này đã bị tạm khóa do quá nhiều lần thử đăng nhập thất bại. Vui lòng thử lại sau." // Lỗi phổ biến
                        "ERROR_USER_DISABLED" -> "Tài khoản của bạn đã bị vô hiệu hóa." // Lỗi phổ biến
                        else -> {
                            val originalMessage = task.exception?.message
                            if (originalMessage != null && originalMessage.contains("auth credential is incorrect")) {
                                "Thông tin đăng nhập không hợp lệ hoặc đã hết hạn."
                            } else {
                                "Đăng nhập thất bại. Vui lòng thử lại. (${originalMessage ?: "Lỗi không xác định"})"
                            }
                        }
                    }
                } else {
                    task.exception?.message ?: "Đăng nhập thất bại. Vui lòng thử lại."

                }
                _loginError.value = errorMessage
                _loginSuccess.value = false

            }
        }
    }
}