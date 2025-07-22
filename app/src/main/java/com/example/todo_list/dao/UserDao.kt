package com.example.todo_list.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todo_list.model.UserInformation


@Dao
interface UserDao{
    @Insert
    suspend fun insertUser(user: UserInformation)

    // Các phương thức khác của DAO
    @Query("SELECT * FROM users WHERE displayName = :displayName")
    suspend fun selectDisplay(displayName: String) :List<UserInformation>

    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun selectId(id: String) :List<UserInformation>
}