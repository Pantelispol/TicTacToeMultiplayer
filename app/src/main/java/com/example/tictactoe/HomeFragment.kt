package com.example.tictactoe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.room.Room

import com.example.tictactoe.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: MyAppDatabase
    private lateinit var database1: MyAppDatabase1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        database = Room.databaseBuilder(
            requireContext().applicationContext,
            MyAppDatabase::class.java,
            "my_app_database"
        ).build()
        database1 = Room.databaseBuilder(
            requireContext().applicationContext,
            MyAppDatabase1::class.java,
            "my_app_database1"
        ).build()
        CoroutineScope(Dispatchers.IO).launch {
            val email = FirebaseAuth.getInstance().currentUser?.email.toString()
            val id = database.userDao().getUserIdByEmail(email)
            val userStats = database1.userStatsDao().getUserStatsByUserId(id)
            val username = database.userDao().getUsernameById(id).toString()
//            binding.wins.text = "Wins: " +database1.userStatsDao().getTotalWinsByUserId(id)
//            binding.draws.text = "Draws: " +database1.userStatsDao().getTotalDrawsByUserId(id)
//            binding.loses.text = "Loses: " +database1.userStatsDao().getTotalLossesByUserId(id)

            // Perform UI updates on the main thread
            withContext(Dispatchers.Main) {
                // Update UI with the latest statistics
                userStats?.let {
                    binding.username.text = "Welcome ${username}"
                    binding.wins.text = "Wins: ${it.wins}"
                    binding.draws.text = "Draws: ${it.draws}"
                    binding.loses.text = "Losses: ${it.losses}"
                }
            }
        }

        binding.root.invalidate()
        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}