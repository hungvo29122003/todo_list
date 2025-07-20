package com.example.todo_list.view

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.todo_list.LoginActivity
import com.example.todo_list.LoginActivity.Companion
import com.example.todo_list.dao.UserDao
import com.example.todo_list.database.AppDatabase
import com.example.todo_list.model.UserInformation
import kotlinx.coroutines.launch


class DisplayNameViewModel(): ViewModel() {
    lateinit var userDao: UserDao
    private val _displayName = MutableLiveData<String>()
    val displayName: MutableLiveData<String> = _displayName
    fun setDisplayName(displayName: String) {
        _displayName.value = displayName
    }
    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess : LiveData<Boolean> = _saveSuccess
    private val _saveError = MutableLiveData<String?>()
    val saveError: LiveData<String?> = _saveError
    // LiveData để theo dõi trạng thái tồn tại của displayName
    private val _isDisplayNameExists = MutableLiveData<Boolean>()
    val isDisplayNameExists: LiveData<Boolean> = _isDisplayNameExists

    // LiveData để theo dõi trạng thái tải (loading) khi kiểm tra
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _isUserIdDb = MutableLiveData<Boolean>()
    val isUserIdDb: LiveData<Boolean> = _isUserIdDb
    companion object{
        private const val PREFS_NAME = "MyAppPrefs"
        private const val KEY_USER_ID = "firebase_user_id"
        private val DISPLAY_NAME_REGEX = "^[a-zA-Z0-9 _-]{3,20}$".toRegex()
        private const val MIN_LENGTH = 3
        private const val MAX_LENGTH = 20
    }

    private fun isValidDisplayName(displayName: String): Boolean {
        return displayName.matches(DISPLAY_NAME_REGEX)
    }

    fun saveInformation(users: UserInformation){
        val currentDisplayName = _displayName.value
        if (currentDisplayName.isNullOrEmpty()){
            _saveError.value = "Không được để trống"
            return
        }
        if (currentDisplayName.length < MIN_LENGTH) {
            _saveError.value = "Tên hiển thị phải có ít nhất $MIN_LENGTH ký tự."
            _saveSuccess.value = false
            return
        }
        if (currentDisplayName.length > MAX_LENGTH) {
            _saveError.value = "Tên hiển thị không được vượt quá $MAX_LENGTH ký tự."
            _saveSuccess.value = false
            return
        }
        if (!isValidDisplayName(currentDisplayName)) {
            _saveError.value = "Tên hiển thị chỉ được chứa chữ cái, số, khoảng trắng, gạch dưới và gạch ngang."
            _saveSuccess.value = false
            return
        }
        _saveSuccess.value = true
        _saveError.value = null
        viewModelScope.launch {
            try {
                userDao.insertUser(users)
                _saveSuccess.value = true
                _saveError.value = null
                Log.d("DisplayNameModel", "Saved displayName: $currentDisplayName")
            } catch (e: Exception) {
                Log.e("DisplayNameModel", "Error saving displayName: ${e.message}", e)
                _saveSuccess.value = false
                _saveError.value = "Lỗi khi lưu người dùng vào DB: ${e.message}"


            }

        }
    }
    fun checkDisplayNameExists(displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val users = userDao.selectDisplay(displayName)
                _isDisplayNameExists.value = users.isNotEmpty()
                Log.d("DisplayNameModel", "Checked displayName: $displayName, Exists: ${users.isNotEmpty()}")
            } catch (e: Exception) {
                Log.e("DisplayNameModel", "Error checking displayName existence: ${e.message}", e)
                _isDisplayNameExists.value = false
                _saveError.value = "Lỗi khi kiểm tra tên hiển thị: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun checkUserExistsInDb(userId: String) {
        if (!::userDao.isInitialized) {
            _saveError.value = "Database not initialized. Please call initDao() first."
            _isUserIdDb.value = false
            return
        }

        viewModelScope.launch {
            try {
                val user = userDao.selectId(userId) // Gọi DAO để lấy người dùng theo ID
                _isUserIdDb.value = user.isNotEmpty() // Nếu danh sách không rỗng, người dùng tồn tại
                Log.d("DisplayNameModel", "Checked user ID: $userId, Exists in DB: ${user.isNotEmpty()}")
            } catch (e: Exception) {
                Log.e("DisplayNameModel", "Error checking user existence in DB: ${e.message}", e)
                _isUserIdDb.value = false
                _saveError.value = "Lỗi khi kiểm tra người dùng trong DB: ${e.message}"
            }
        }
    }
}