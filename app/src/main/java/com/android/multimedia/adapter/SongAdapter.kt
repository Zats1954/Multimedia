package com.android.multimedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.multimedia.MediaLifecycleObserver
import com.android.multimedia.Song
import com.android.multimedia.databinding.CardSongBinding
import kotlinx.coroutines.*


interface OnInteractionListener {
    fun onComplite(song: Song): Song
    fun clickSong(songId: Int): Song
}

class SongAdapter(
    private val onInteractionListener: OnInteractionListener,
    private val songPrefix: String

) : ListAdapter<Song, SongViewHolder>(SongDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = CardSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding, onInteractionListener, songPrefix)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song)
    }
}

class SongViewHolder(
    private val binding: CardSongBinding,
    private val onInteractionListener: OnInteractionListener,
    private val songPrefix: String
) : RecyclerView.ViewHolder(binding.root) {
    private val mediaObserver = MediaLifecycleObserver()
    private val checkTime = 1000L
    var playDuration: Int = 1
    var playingSongId: Int = 0

    private val player = mediaObserver.player

    fun bind( song: Song) {

        binding.apply {

            songData(song)   /* to install song's duration time */

            play.setOnClickListener {
                println("***************************  playing song ${song.id} ${song.file}")
                val songFile = onInteractionListener.clickSong(song.id)
                if (songFile.id != playingSongId) {
                    songData(song)
                }
                /*   ***************************************************** */
                playingSongId = song.id
                val playLength = player?.duration
                var currentSec: Int
                var currentSecOld: Int = 0
                var currentTime: String


                if (!play.isChecked) {
                    player?.pause()
                } else {
                    player?.start()
                    CoroutineScope(Dispatchers.IO).launch {
                        while (play.isChecked) {
                            withTimeout(checkTime) {
                                try {
                                    delay(100)  /* для возможности включения паузы */
                                    if (player?.isPlaying ?: false) {
                                        currentSec = player?.currentPosition ?: 1
                                    } else {
                                        currentSec = currentSecOld  /* для отражения на паузе */
                                    }
                                    currentSecOld = currentSec
                                    currentTime = TimeToString(currentSec / 1000) + "/" + playLength
                                    progressBar.progress = currentSec
//                                  tvLength.text = currentTime
                                } catch (ex: IllegalStateException) {
                                    progressBar.progress = 0
//                                  tvLength.text = TimeToString(0) + "/" + playLength
                                    play.isChecked = false
                                }
                            }
                        }
                    }
                }
/* ***************************************************** */
            }

            player?.setOnCompletionListener {
                player?.stop()
                val songNext = onInteractionListener.onComplite(song)
                println("******************* ${songNext.id}  ${songNext.file}")
                songData(songNext)
                this@SongViewHolder.bind(songNext)
                player?.start()
             }
        }
    }


    private fun TimeToString(playSec: Int): String {
        var playSec1 = playSec
        val playMin: Int = playSec1 / 60
        playSec1 = playSec1 % 60
        if (playSec1 < 10)
            return "${playMin}:0${playSec1}"
        else
            return "${playMin}:${playSec1}"
    }

    private fun CardSongBinding.songData(song: Song) {
        player?.reset()
        player?.setDataSource(songPrefix + song.file)
        player?.prepare()
        playDuration = player?.duration ?: 1
        val playSec: Int = player?.duration?.let { it / 1000 } ?: 1
        val playLength = TimeToString(playSec)
        tvSongName.setText(song.file)
        tvLength.setText("0:00/${playLength}")
        progressBar.max = player?.duration ?: 1
        progressBar.progress = 0
    }
}

class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }
}

