package com.example.todo_list.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todo_list.dao.UserDao
import com.example.todo_list.model.UserInformation

@Database(entities = [UserInformation::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}