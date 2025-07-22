package com.example.todo_list.viewmodel

import android.content.Intent
import android.text.BoringLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todo_list.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth

class RegisterViewModel : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email
    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password
    private val _confirmPassword = MutableLiveData<String>()
    val confirmPassword: LiveData<String> = _confirmPassword

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setConfirmPassword(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
    }

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    private val _registerError = MutableLiveData<String?>()
    val registerError: LiveData<String?> = _registerError

    private val auth: FirebaseAuth = Firebase.auth

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()
        return emailRegex.matches(email)

    }

    fun register() {
        val currentEmail = _email.value
        val currentPassword = _password.value
        val currentConfirmPassword = _confirmPassword.value

        if (currentEmail.isNullOrEmpty()) {
            _registerError.value = "Email không được để trống"
            return
        }
        if (currentPassword.isNullOrEmpty()) {
            _registerError.value = "Mật khẩu không được để trống"
            return
        }
        if (currentConfirmPassword.isNullOrEmpty()) {
            _registerError.value = "Xác nhận mật khẩu không được để trống"
            return
        }

        if (!isValidEmail(currentEmail)) {
            _registerError.value = "Định dạng Email không hợp lệ"
            return
        }
        if (currentPassword != currentConfirmPassword) {
            _registerError.value = "Mật khẩu không khớp nhau"
            return
        }
        if (currentPassword.length < 6) {
            _registerError.value = "Mật khẩu phải có ít nhất 6 ký tự"
            return
        }

//        auth.createUserWithEmailAndPassword(currentEmail, currentPassword).addOnCompleteListener {
//            task ->
//            if (task.isSuccessful){
//                _registerSuccess.value = true
//            } else {
//                _registerError.value = task.exception?.message
//
//            }
//        }

        auth.fetchSignInMethodsForEmail(currentEmail).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val signInMethods = task.result?.signInMethods
                if (signInMethods != null && signInMethods.isNotEmpty()){
                    _registerError.value = "Email đã được sử dụng"
                } else {
                    createUserWithEmailAndPassword(currentEmail, currentPassword)
                }
            } else {
                _registerError.value = task.exception?.message ?: "Đăng ký thất bại. Vui lòng thử lại."
            }
        }
    }

    private fun createUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _registerSuccess.value = true
                    // Reset lỗi nếu có lỗi trước đó nhưng đăng ký thành công
                    _registerError.value = null
                } else {
                    // Xử lý lỗi đăng ký (ví dụ: mật khẩu yếu mặc dù đã có check length)
                    val errorMessage = if (task.exception is FirebaseAuthException) {
                        when ((task.exception as FirebaseAuthException).errorCode) {
                            "ERROR_WEAK_PASSWORD" -> "Mật khẩu quá yếu. Vui lòng sử dụng mật khẩu mạnh hơn."
                            // Thêm các trường hợp lỗi khác nếu muốn tùy chỉnh thông báo
                            else -> task.exception?.message ?: "Đăng ký thất bại. Vui lòng thử lại."
                        }
                    } else {
                        task.exception?.message ?: "Đăng ký thất bại. Vui lòng thử lại."
                    }
                    _registerError.value = errorMessage
                    _registerSuccess.value = false // Đặt lại trạng thái thành công
                }
            }
    }

}