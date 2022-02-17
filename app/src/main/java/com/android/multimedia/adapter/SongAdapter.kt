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
    fun onComplite(song: Song)
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

    override fun onBindViewHolder(holder: SongViewHolder, position: Int, payLoads: List<Any>) {
        if (payLoads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            payLoads.forEach {
                if (it is Song) {
                    holder.play(it)
                }
            }
        }
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

    private val scope = CoroutineScope(Dispatchers.IO)

    private val player = mediaObserver.player

    fun play(song: Song) {
        with(binding) {
            println("***************************  playing song ${song.id} ${song.file}")
            val songFile = onInteractionListener.clickSong(song.id)
            if (songFile.id != playingSongId) {
                songData(song) /* to install song's duration time */
            }

            playingSongId = song.id
            val playLength = player?.duration
            var currentSec: Int
            var currentSecOld = 0
            var currentTime: String


            if (player?.isPlaying == true) {
                player.pause()
                play.isChecked = false
            } else {
                player?.start()
                play.isChecked = true
                scope.coroutineContext.cancelChildren()
                scope.launch {
                    while (player?.isPlaying == true) {
                        withTimeout(checkTime) {
                            try {
                                delay(100)  /* для возможности включения паузы */
                                currentSec = if (player.isPlaying) {
                                    player.currentPosition
                                } else {
                                    currentSecOld  /* для отражения на паузе */
                                }
                                currentSecOld = currentSec
                                currentTime =
                                    TimeToString(currentSec / 1000) + "/" + playLength
                                progressBar.progress = currentSec
//                                  tvLength.text = currentTime
                            } catch (ex: IllegalStateException) {
                                progressBar.progress = 0
                                scope.coroutineContext.cancelChildren()
//                                  tvLength.text = TimeToString(0) + "/" + playLength
                                play.isChecked = false
                            }
                        }
                    }
                }
            }
        }
    }

    fun bind(song: Song) {
        binding.apply {
            songData(song)    /* to install song's duration time */
            play.setOnClickListener {
                play(song)
            }

            player?.setOnCompletionListener {
                progressBar.progress = 0
                scope.coroutineContext.cancelChildren()
                play.isChecked = false
                player.stop()
                onInteractionListener.onComplite(song)
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

