package com.example.nammanala

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nammanala.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            val username =
                binding.etUsername.text.toString()

            val password =
                binding.etPassword.text.toString()

            if (username == "admin" && password == "1234") {

                startActivity(
                    Intent(
                        this,
                        AdminDashboardActivity::class.java
                    )
                )

            } else if (username == "farmer" && password == "1234") {

                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                )

            } else {

                Toast.makeText(
                    this,
                    "Invalid Credentials",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}