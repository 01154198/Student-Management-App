package com.example.studentmanagementapp1
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.studentmanagementapp1.ui.theme.STUDENTMANAGEMENTAPP1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            STUDENTMANAGEMENTAPP1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudentManagementScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentManagementScreen() {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var studentList by remember { mutableStateOf(dbHelper.getAllStudents()) }
    var editingStudent by remember { mutableStateOf<Student?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Student Management") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = course,
                onValueChange = { course = it },
                label = { Text("Course") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotEmpty() && email.isNotEmpty() && course.isNotEmpty()) {
                        if (editingStudent == null) {
                            val newStudent = Student(name = name, email = email, course = course)
                            dbHelper.addStudent(newStudent)
                            Toast.makeText(context, "Student Added", Toast.LENGTH_SHORT).show()
                        } else {
                            val updatedStudent = editingStudent!!.copy(name = name, email = email, course = course)
                            dbHelper.updateStudent(updatedStudent)
                            Toast.makeText(context, "Student Updated", Toast.LENGTH_SHORT).show()
                            editingStudent = null
                        }
                        name = ""
                        email = ""
                        course = ""
                        studentList = dbHelper.getAllStudents()
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (editingStudent == null) "Add Student" else "Update Student")
            }

            if (editingStudent != null) {
                TextButton(onClick = {
                    editingStudent = null
                    name = ""
                    email = ""
                    course = ""
                }) {
                    Text("Cancel Edit")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(studentList) { student ->
                    StudentItem(
                        student = student,
                        onEdit = {
                            editingStudent = student
                            name = student.name
                            email = student.email
                            course = student.course
                        },
                        onDelete = {
                            dbHelper.deleteStudent(student.id)
                            studentList = dbHelper.getAllStudents()
                            Toast.makeText(context, "Student Deleted", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StudentItem(student: Student, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = student.name, style = MaterialTheme.typography.titleMedium)
                Text(text = student.email, style = MaterialTheme.typography.bodySmall)
                Text(text = student.course, style = MaterialTheme.typography.bodySmall)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
