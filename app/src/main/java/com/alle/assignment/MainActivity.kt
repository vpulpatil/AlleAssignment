package com.alle.assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.alle.assignment.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
