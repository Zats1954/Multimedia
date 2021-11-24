package com.android.multimedia


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow


class AppActivity : AppCompatActivity(R.layout.activity_app) {
    private val mediaObserver = MediaLifecycleObserver()
    private var checkButton: Boolean = false
    private val player = mediaObserver.player
    private val checkTime = 1000L


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvLength = findViewById<TextView>(R.id.tvLength)
        var isPaused = false
        lifecycle.addObserver(mediaObserver)


        player?.reset()
        player?.setDataSource(
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        )
        player?.prepare()
        val playSec:Int = player?.duration?.let{ it /1000} ?: 1
        val playLength = TimeToString(playSec)
        tvLength.setText("0:00/${playLength}")
        progressBar.max = player?.duration ?: 1


        val barPosition: Flow<Int> = flow {
            delay(10L)
            player?.currentPosition?.let {
                emit(it)
            }
        }

        findViewById<Button>(R.id.play).setOnClickListener {
            var currentSec:Int
            if (checkButton) {
                player?.pause()
                isPaused= true
                checkButton = !checkButton
            } else {
                isPaused= false
                mediaObserver.apply {
                    player?.start()
                    val job = CoroutineScope(Dispatchers.IO).launch {
                        while (!isPaused) {
                            withTimeout(1000L) {
                                barPosition.collect {
                                    currentSec  = player?.currentPosition?.let{ it /1000} ?: 1
                                    tvLength.setText("${TimeToString(currentSec)}/${playLength}")
                                    progressBar.progress = it
                                }
                            }
                        }
                    }
                    checkButton = !checkButton
                }
            }
        }
    }

    private fun TimeToString(playSec: Int): String {
        var playSec1 = playSec
        val playMin: Int = playSec1 / 60
        playSec1 = playSec1 % 60
        if(playSec1 < 10)
           return "${playMin}:0${playSec1}"
        else
            return "${playMin}:${playSec1}"
    }


}