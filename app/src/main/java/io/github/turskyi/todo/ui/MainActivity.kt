package io.github.turskyi.todo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.turskyi.todo.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}