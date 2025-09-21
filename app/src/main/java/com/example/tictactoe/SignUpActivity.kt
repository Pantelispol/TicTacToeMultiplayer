package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.tictactoe.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var database: MyAppDatabase
    private lateinit var database1: MyAppDatabase1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            val userName = binding.userEt.text.toString()


            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && userName.isNotEmpty()) {
                if (pass == confirmPass) {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            database = Room.databaseBuilder(
                                applicationContext,
                                MyAppDatabase::class.java,
                                "my_app_database"
                            ).build()
                            database1 = Room.databaseBuilder(
                                applicationContext,
                                MyAppDatabase1::class.java,
                                "my_app_database1"
                            ).build()
                            val user = User(0,username = userName, email = email, password = pass)
                            CoroutineScope(Dispatchers.IO).launch {
                                database.userDao().insertUser(user)
                                val id = database.userDao().getUserIdByEmail(firebaseAuth.currentUser?.email.toString())
                                Log.d("id:", id.toString())
                                val userStats = UserStats(0, id , wins = 0, draws = 0, losses = 0)
                                database1.userStatsDao().insertUser(userStats)
                            }
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }

//    private fun insertDataToDatabase() {
//        val email = binding.emailEt.text.toString()
//        val pass = binding.passET.text.toString()
//        val userName = binding.userEt.text.toString()
//        val user = User(0,userName,email,pass)
//        mUserViewModel.addUser(user)
//        Toast.makeText(this,"Succesfully added!",Toast.LENGTH_SHORT).show()
//    }
}