package com.gsench.senchenok

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gsench.senchenok.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainToolbar.title = getString(R.string.popular_title)
        setSupportActionBar(binding.mainToolbar)
    }

}