package com.example.tictactoe

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tictactoe.databinding.FragmentPlayBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.random.Random
import kotlin.random.nextInt

class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val CHANNEL_ID = "my_channel_id"
        const val NOTIFICATION_ID = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayBinding.inflate(inflater,container,false)

        binding.playOffline1.setOnClickListener{
            createOffLineGame()
        }

        binding.createOnlineGameBtn.setOnClickListener{
            createOnlineGame()
        }

        binding.joinOnlineGameBtn.setOnClickListener{
            joinOnlineGame()
        }


        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun createOffLineGame(){
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.JOINED
            )
        )
        startGame()
    }

    fun createOnlineGame(){
        GameData.myID = "X"
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.CREATED ,
                gameId = Random.nextInt(1000..9999).toString()
            )
        )
        startGame()
    }

    fun joinOnlineGame(){
        var gameId = binding.gameIdInput.text.toString()
        if(gameId.isEmpty()){
            binding.gameIdInput.setError("Please enter game ID")
            return
        }
        GameData.myID = "O"
        Firebase.firestore.collection("games")
            .document(gameId)
            .get()
            .addOnSuccessListener {
                val model = it?.toObject(GameModel::class.java)
                if(model==null){
                    binding.gameIdInput.setError("Please enter valid game ID")
                }else{
                    model.gameStatus = GameStatus.JOINED
                    GameData.saveGameModel(model)
                    startGame()
                }
            }
    }

    fun startGame(){
        startActivity(Intent(requireContext(),GameActivity::class.java))
    }

//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                "Channel Name",
//                NotificationManager.IMPORTANCE_DEFAULT
//            ).apply {
//                description = "Channel Description"
//            }
//
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//
//    private fun createNotification() {
//        val notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_notification) // your icon
//            .setContentTitle("Notification Title")
//            .setContentText("Notification Text")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//        val notificationManager = NotificationManagerCompat.from(requireContext())
//        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
//    }


}

