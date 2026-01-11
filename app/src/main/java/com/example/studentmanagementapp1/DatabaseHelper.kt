package com.example.studentmanagementapp1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "StudentDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_STUDENTS = "students"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
        private const val KEY_COURSE = "course"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_STUDENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_COURSE + " TEXT" + ")")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        onCreate(db)
    }

    fun addStudent(student: Student): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, student.name)
        values.put(KEY_EMAIL, student.email)
        values.put(KEY_COURSE, student.course)
        val success = db.insert(TABLE_STUDENTS, null, values)
        db.close()
        return success
    }

    fun getAllStudents(): List<Student> {
        val studentList = mutableListOf<Student>()
        val selectQuery = "SELECT * FROM $TABLE_STUDENTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val student = Student(
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_COURSE))
                )
                studentList.add(student)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return studentList
    }

    fun updateStudent(student: Student): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, student.name)
        values.put(KEY_EMAIL, student.email)
        values.put(KEY_COURSE, student.course)

        val success = db.update(TABLE_STUDENTS, values, "$KEY_ID=?", arrayOf(student.id.toString()))
        db.close()
        return success
    }

    fun deleteStudent(id: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_STUDENTS, "$KEY_ID=?", arrayOf(id.toString()))
        db.close()
        return success
    }
}
