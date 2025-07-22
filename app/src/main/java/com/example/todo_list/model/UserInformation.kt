package com.example.todo_list.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserInformation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val displayName: String,
)
