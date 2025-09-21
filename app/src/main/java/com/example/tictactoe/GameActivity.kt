package com.example.tictactoe

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.example.tictactoe.databinding.ActivityGameBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GameActivity : AppCompatActivity(),View.OnClickListener {

    lateinit var binding: ActivityGameBinding

    private var gameModel : GameModel? = null

    private lateinit var drawerLayout: DrawerLayout

    private val CHANNEL_ID = "Your_Channel_Id"
    private val NOTIFICATION_ID = 123

    private lateinit var database: MyAppDatabase
    private lateinit var database1: MyAppDatabase1

    private var winnerID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GameData.fetchGameModel()

        setSupportActionBar(binding.toolbar)


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.btn4.setOnClickListener(this)
        binding.btn5.setOnClickListener(this)
        binding.btn6.setOnClickListener(this)
        binding.btn7.setOnClickListener(this)
        binding.btn8.setOnClickListener(this)

        binding.startGame.setOnClickListener {
            startGame()

        }
        GameData.gameModel.observe(this){
            gameModel = it
            setUI()
        }

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


    }



    fun setUI(){
        gameModel?.apply {
            binding.btn0.text = filledPos[0]
            binding.btn1.text = filledPos[1]
            binding.btn2.text = filledPos[2]
            binding.btn3.text = filledPos[3]
            binding.btn4.text = filledPos[4]
            binding.btn5.text = filledPos[5]
            binding.btn6.text = filledPos[6]
            binding.btn7.text = filledPos[7]
            binding.btn8.text = filledPos[8]

            binding.startGame.visibility = View.VISIBLE

            binding.gameStatusText.text =
                when(gameStatus){
                    GameStatus.CREATED -> {
                        binding.startGame.visibility = View.INVISIBLE
                        "Game ID :"+ gameId
                    }
                    GameStatus.JOINED ->{
                        "Click on start game"
                    }
                    GameStatus.INPROGRESS ->{
                        binding.startGame.visibility = View.INVISIBLE
                        when(GameData.myID){
                            currentPlayer -> "Your turn"
                            else ->  currentPlayer + " turn"
                        }

                    }
                    GameStatus.FINISHED ->{
                        if(winner.isNotEmpty()) {
                            when(GameData.myID){
                                winner -> "You won"
                                else ->   winner + " Won"
                            }

                        }
                        else "DRAW"
                    }
                }

        }
    }

    fun startGame(){
        gameModel?.apply {
            updateGameData(
                GameModel(
                    gameId = gameId,
                    gameStatus = GameStatus.INPROGRESS
                )
            )
        }
    }

    fun updateGameData(model : GameModel){
        GameData.saveGameModel(model)
    }

    fun checkForWinner(){
        val winningPos = arrayOf(
            intArrayOf(0,1,2),
            intArrayOf(3,4,5),
            intArrayOf(6,7,8),
            intArrayOf(0,3,6),
            intArrayOf(1,4,7),
            intArrayOf(2,5,8),
            intArrayOf(0,4,8),
            intArrayOf(2,4,6),
        )
        gameModel?.apply {
            for ( i in winningPos){
                //012
                if(
                    filledPos[i[0]] == filledPos[i[1]] &&
                    filledPos[i[1]]== filledPos[i[2]] &&
                    filledPos[i[0]].isNotEmpty()
                ){
                    gameStatus = GameStatus.FINISHED
                    winner = filledPos[i[0]]
                    winnerID = filledPos[i[0]]
//                    if (winnerID == "X") {
//                        CoroutineScope(Dispatchers.IO).launch {
//                            // Increment wins count in the database
//                            val email = FirebaseAuth.getInstance().currentUser?.email.toString()
//                            val id = database.userDao().getUserIdByEmail(email)
//                            val userStats = database1.userStatsDao().getUserStatsByUserId(id)
//                            userStats?.let {
//                                it.wins++
//                                database1.userStatsDao().insertUser(it)
//                            }
//
//                        }
//                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        val email = FirebaseAuth.getInstance().currentUser?.email.toString()
                        val id = database.userDao().getUserIdByEmail(email)
                        val userStats = database1.userStatsDao().getUserStatsByUserId(id)
                        userStats?.let {
                            if (winnerID == "X") {
                                it.wins++
                            } else if (winnerID == "O") {
                                // Increment wins for "O" as well
                                it.losses++
                            }
                            database1.userStatsDao().insertUser(it)
                        }
                    }
                }
            }
            if( filledPos.none(){ it.isEmpty() }){
                gameStatus = GameStatus.FINISHED
                CoroutineScope(Dispatchers.IO).launch {
                    val email = FirebaseAuth.getInstance().currentUser?.email.toString()
                    val id = database.userDao().getUserIdByEmail(email)
                    val userStats = database1.userStatsDao().getUserStatsByUserId(id)
                    userStats?.let {
                        it.draws++
                        database1.userStatsDao().insertUser(it)
                    }
                }
            }

            updateGameData(this)
        }
    }

    override fun onClick(v: View?) {
        gameModel?.apply {
            if(gameStatus!= GameStatus.INPROGRESS){
                Toast.makeText(applicationContext,"Game not started",Toast.LENGTH_SHORT).show()
                return
            }
            //game is in progress
            if(gameId!="-1" && currentPlayer!= GameData.myID ){
                Toast.makeText(applicationContext,"Not your turn",Toast.LENGTH_SHORT).show()
                return
            }
            val clickedPos =(v?.tag  as String).toInt()
            if(filledPos[clickedPos].isEmpty()){
                filledPos[clickedPos] = currentPlayer
                currentPlayer = if(currentPlayer=="X") "O" else "X"
                checkForWinner()
                updateGameData(this)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}