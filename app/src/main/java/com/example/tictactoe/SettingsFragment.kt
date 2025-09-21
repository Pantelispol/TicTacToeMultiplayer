package com.example.tictactoe


import android.R
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.example.tictactoe.databinding.FragmentSettingsBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth : FirebaseAuth

    private lateinit var database : MyAppDatabase
    private lateinit var database1 : MyAppDatabase1

    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
//        val sharedPreferences = this.getActivity()?.getSharedPreferences("Mode", Context.MODE_PRIVATE)
//        val editor = sharedPreferences?.edit()
//        val nightMode = sharedPreferences?.getBoolean("night",false)
//        if(nightMode == true){
//            binding.switch1.isChecked = true
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        }
//        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
//            if (!isChecked) {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                editor?.putBoolean("night",false)
//                editor?.apply()
//            } else {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                editor?.putBoolean("night",true)
//                editor?.apply()
//            }
//        }
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

        binding.switch2.isChecked = SwitchButtonManager.loadSwitchState(requireContext())
        binding.switch2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SwitchButtonManager.saveSwitchState(requireContext(), isChecked)
                LanguageManager.setLanguage(requireContext(), "el") // Switch to Greek
                recreateActivityWithAnimation(requireActivity())
            } else {
                SwitchButtonManager.saveSwitchState(requireContext(), isChecked)
                LanguageManager.setLanguage(requireContext(), "en") // Switch to Greek
                recreateActivityWithAnimation(requireActivity())
            }
        }
        val currentUser = FirebaseAuth.getInstance().currentUser
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)
        binding.switch1.isChecked = isDarkMode
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("isDarkMode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        val emailUser = currentUser?.email.toString()
        binding.deleteAccount.setOnClickListener {
            FirebaseAuth.getInstance().currentUser?.delete()
            CoroutineScope(Dispatchers.IO).launch {
                database.userDao().deleteUserByEmail(emailUser)
                val id = database.userDao().getUserIdByEmail(emailUser)
                Log.d("id:",id.toString())
                database1.userStatsDao().deleteByUserId(id)
            }
            Toast.makeText(
                requireContext(),
                "The account have been deleted",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
            binding.changeButton.setOnClickListener {
                val username = binding.username.text.toString()
                val password = binding.password.text.toString()
                val confirmPassword = binding.confirmpassword.text.toString()
                if (username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                    if(password == confirmPassword) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val id = database.userDao().getUserIdByEmail(emailUser)
                            val user = User(id, username, emailUser, password)
                            database.userDao().updateUser(user)
                        }
                    }else{
                        Toast.makeText(requireContext(),"The password doesn't mathc",Toast.LENGTH_SHORT).show()
                    }
    //
    //                changePassword(confirmPassword)

                }

            }
        return binding.root
    }

//    fun changePassword(newPassword: String) {
//        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
//
//        if (user != null) {
//            user.updatePassword(newPassword)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        // Password updated successfully
//                        println("Password updated successfully")
//                    } else {
//                        // Handle password update failure
//                        println("Failed to update password: ${task.exception?.message}")
//                    }
//                }
//        } else {
//            println("No user is currently signed in.")
//        }
//    }



    override fun onResume() {
        super.onResume()
        LanguageManager.loadLanguage(requireContext())
    }

    fun recreateActivityWithAnimation(activity: FragmentActivity) {
        val fadeAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
        activity.window.decorView.startAnimation(fadeAnimation)
        activity.window.decorView.postDelayed({
            activity.recreate()
        }, fadeAnimation.duration)
    }

//    private fun deleteUser(){
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setPositiveButton("Yes"){ _ , _ ->
//            mUserViewModel.deleteUser(com.example.tictactoe.User())
//        }
//        builder.setNegativeButton("No"){ _, _ -> }
//        builder.setTitle("Delete ${args..userName}?")
//        builder.setMessage("Are you sure you want to delete ${args.currentUser.username}?")
//        builder.create().show()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showCustomDialogBox(message: String?){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(com.example.tictactoe.R.layout.custom_dialog_box)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnNo : MaterialButton = dialog.findViewById(com.example.tictactoe.R.id.btnDialogCancel)
        val btnYes : MaterialButton = dialog.findViewById(com.example.tictactoe.R.id.btnDialogLogout)
        btnYes.setOnClickListener{

        }
        btnNo.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }


}