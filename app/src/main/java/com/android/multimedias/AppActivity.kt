package com.android.multimedias

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*

class AppActivity : AppCompatActivity() {
    private   val gson = Gson()
    private   val listSongType = TypeToken.getParameterized(Albom::class.java).type

    private val mediaObserver = MediaLifecycleObserver()

    private val player = mediaObserver.player
    private val checkTime = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val myReader = InputStreamReader(resources.openRawResource(R.raw.sng))
        val listSongType = object : TypeToken<Albom>() {}.type
        val songs: Albom = gson.fromJson(myReader, listSongType)
        println(songs)
        val songPrefix = "https://" +songs.subtitle + "/examples/mp3/" + songs.title
        println(songPrefix)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvLength = findViewById<TextView>(R.id.tvLength)
        var isPaused: Boolean
        var checkButton = false

        lifecycle.addObserver(mediaObserver)

        player?.reset()
        player?.setDataSource(
            "${songPrefix}${songs.tracks[10].file}"
//            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
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
                    CoroutineScope(Dispatchers.IO).launch {
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