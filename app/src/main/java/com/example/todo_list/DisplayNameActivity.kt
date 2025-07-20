package com.example.todo_list

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.todo_list.database.AppDatabase
import com.example.todo_list.databinding.ActivityDisplayNameBinding
import com.example.todo_list.model.UserInformation
import com.example.todo_list.view.DisplayNameViewModel
import kotlin.random.Random

class DisplayNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDisplayNameBinding
    private lateinit var display_Name: EditText
    private lateinit var displayNameViewModel: DisplayNameViewModel
    private lateinit var save_button: AppCompatButton
    private lateinit var generate_random_button: AppCompatImageButton

    private lateinit var loadingOverlay: View
    private lateinit var contentLayout: View

    private var isInitialCheckDone = false // Cờ để kiểm soát việc kiểm tra chỉ chạy một lần khi cần


    companion object {
        const val EXTRA_DISPLAY_NAME = "displayName"
        private const val PREFS_NAME = "MyAppPrefs"
        private const val KEY_USER_ID = "firebase_user_id"
        private const val SAVE_NAME_COMPLETED = "save_name_completed"
        private const val RANDOM_STRING_LENGTH = 15 // Độ dài chuỗi ngẫu nhiên
        private const val ALPHANUMERIC_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDisplayNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
        val userDao = database.userDao()
        save_button = binding.btnSave
        display_Name = binding.edtDisplayName
        generate_random_button = binding.btnGenerateRandom

        loadingOverlay = binding.loadingOverlay
        contentLayout = binding.contentLayout
        loadingOverlay.visibility = View.VISIBLE
        contentLayout.visibility = View.GONE

        displayNameViewModel = ViewModelProvider(this).get(DisplayNameViewModel::class.java)

        displayNameViewModel.userDao = userDao

        val displayName = getDisplayNameFromPrefs() ?: "default_displayName"
        displayName?.let {
            display_Name.setText(it)
            displayNameViewModel.setDisplayName(it)
        }
        observeViewModel()
        setupUIListeners()
        checkAndNavigateIfUserExists()

    }
    override fun onStart() {
        super.onStart()
        // Bạn có thể giữ logic này nếu muốn kiểm tra lại khi Activity quay lại từ back stack
        // hoặc xóa nếu bạn chỉ muốn kiểm tra một lần khi ứng dụng mở.
        // Với cách "loading UI" này, việc gọi lại ở đây là chấp nhận được.
        if (!isInitialCheckDone) {
            loadingOverlay.visibility = View.VISIBLE
            contentLayout.visibility = View.GONE
            checkAndNavigateIfUserExists()
        }
    }

    private fun observeViewModel() {
        displayNameViewModel.isLoading.observe(this) { isLoading ->
            // Hiển thị/ẩn ProgressBar hoặc các chỉ báo tải khác
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            save_button.isEnabled = !isLoading // Vô hiệu hóa nút khi đang tải
            generate_random_button.isEnabled = !isLoading
        }

        displayNameViewModel.isDisplayNameExists.observe(this) { exists ->
            if (exists) {
                binding.edtDisplayName.error = "Tên hiển thị này đã tồn tại!"
                // Vô hiệu hóa nút lưu nếu tên đã tồn tại
                save_button.isEnabled = false
            } else {
                binding.edtDisplayName.error = null // Xóa lỗi
                // Kích hoạt lại nút lưu nếu tên khả dụng (và không đang tải)
                save_button.isEnabled = !displayNameViewModel.isLoading.value!!
            }
        }

        displayNameViewModel.saveSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Lưu thông tin thành công!", Toast.LENGTH_SHORT).show()
                val currentDisplayName = displayNameViewModel.displayName.value
                if (!currentDisplayName.isNullOrBlank()){
                    saveDisplayNameToPrefs(currentDisplayName)
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                // Chuyển sang màn hình tiếp theo hoặc đóng Activity
                // finish()
            }
        }

        displayNameViewModel.saveError.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
        displayNameViewModel.isUserIdDb.observe(this) { isInDb ->
            // Đặt cờ là đã hoàn thành kiểm tra ban đầu
            isInitialCheckDone = true
            if (isInDb) {
                Log.d("DisplayNameActivity", "User exists in database. Navigating to MainActivity.")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Đóng Activity hiện tại
            } else {
                Log.d("DisplayNameActivity", "User not found in database. User needs to set display name.")
                // Không làm gì cả ở đây, để UI của DisplayNameActivity hiển thị
                loadingOverlay.visibility = View.GONE
                contentLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setupUIListeners() {
        // Lắng nghe sự thay đổi văn bản trong EditText để kiểm tra sự tồn tại
        binding.edtDisplayName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // Khi EditText mất focus
                val displayName = binding.edtDisplayName.text.toString().trim()
                if (displayName.isNotBlank()) {
                    displayNameViewModel.checkDisplayNameExists(displayName)
                }
            }
        }

        // Lắng nghe sự kiện click nút lưu
        save_button.setOnClickListener {
            val displayNameText = binding.edtDisplayName.text.toString().trim()

            // Cập nhật displayName trong ViewModel trước khi gọi saveInformation
            displayNameViewModel.setDisplayName(displayNameText)

            // Lấy userId. Trong ứng dụng thực tế, bạn sẽ lấy userId từ Firebase Auth
            // hoặc từ SharedPreferences sau khi người dùng đăng nhập.
            // Ví dụ:
            val userId =
                getUserIdFromPrefs() ?: "default_user_id" // Dùng ID mặc định nếu không tìm thấy

            // Tạo đối tượng UserInformation
            val userToSave = UserInformation(
                userId = userId, // ID duy nhất cho người dùng
                displayName = displayNameText,
            )

            // Gọi hàm saveInformation từ ViewModel
            displayNameViewModel.saveInformation(userToSave)
        }
        generate_random_button.setOnClickListener {
            val randomName = generateRandomAlphanumericString(RANDOM_STRING_LENGTH)
            display_Name.setText(randomName)
            displayNameViewModel.setDisplayName(randomName) // Cập nhật cả ViewModel
            binding.edtDisplayName.error = null // Xóa lỗi cũ nếu có
            // Tùy chọn: Gọi kiểm tra tên ngay lập tức nếu muốn
            displayNameViewModel.checkDisplayNameExists(randomName)
        }
    }
    private fun saveDisplayNameToPrefs(displayName: String) {
        val sharedPrefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString(SAVE_NAME_COMPLETED, displayName)
            apply() // Sử dụng apply() để lưu bất đồng bộ
        }
    }

    fun getUserIdFromPrefs(): String? {
        val sharedPrefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString(KEY_USER_ID, null)
    }

    fun getDisplayNameFromPrefs(): String? {
        val sharedPrefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString(EXTRA_DISPLAY_NAME, null)
    }

    // Hàm tạo chuỗi ngẫu nhiên
    private fun generateRandomAlphanumericString(length: Int): String {
        return (1..length)
            .map { ALPHANUMERIC_CHARS[Random.nextInt(0, ALPHANUMERIC_CHARS.length)] }
            .joinToString("")
    }
    private fun isSaveName(): String? {
        val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return sharedPreferences.getString(SAVE_NAME_COMPLETED, null)
    }
    private fun checkAndNavigateIfUserExists() {
        val userId = getUserIdFromPrefs()
        if (userId != null) {
            Log.d("DisplayNameActivity", "User ID found in SharedPreferences: $userId. Checking database...")
            // Gọi ViewModel để kiểm tra người dùng trong database
            displayNameViewModel.checkUserExistsInDb(userId)
        } else {
            Log.d("DisplayNameActivity", "No User ID found in SharedPreferences. Proceeding to display name setup.")
            // Nếu không có userId, không làm gì cả, để người dùng nhập displayName
        }
    }

//    override fun onStart() {
//        super.onStart()
//        checkAndNavigateIfUserExists()
//    }
}