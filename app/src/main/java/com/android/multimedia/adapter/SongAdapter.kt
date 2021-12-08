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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.atomic.AtomicBoolean


interface OnInteractionListener {
    fun onComplite(song: Song) {}
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
    private var checkButton: Boolean = false
    private val player = mediaObserver.player
    private var isPaused: AtomicBoolean = AtomicBoolean()

    fun bind(song: Song) {
        binding.apply {
            player?.reset()
            player?.setDataSource(
                 songPrefix + song.file
//                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
            )
            player?.prepare()
            val playSec: Int = player?.duration?.let { it / 1000 } ?: 1
            val playLength = TimeToString(playSec)

            tvSongName.setText(song.file)
            tvLength.setText("0:00/${playLength}")
            progressBar.max = player?.duration ?: 1
            progressBar.progress = 0


//            val barPosition: Flow<Int> = flow {
//                delay(500L)
//                if (player?.isPlaying == true)
//                    emit(player?.getCurrentPosition())
//                else
//                    emit(playSec)
//            }

            play.setOnClickListener {
                var currentSec: Int
                var currentSecOld: Int = 0
                var currentTime: String
                if (checkButton) {
                    player?.pause()
                    isPaused.set(true)
                    checkButton = !checkButton
                } else {
                    isPaused.set(false)
                    player?.start()
                    CoroutineScope(Dispatchers.IO).launch {
                        while (!isPaused.get()) {
                            withTimeout(checkTime) {
                                try {
//                                    barPosition.collect {
                                    delay(100)  /* для возможности включения паузы */
                                    if (player?.isPlaying ?: false) {
                                        currentSec = player?.currentPosition ?: 1
                                    } else {
                                        currentSec = currentSecOld  /* для отражения на паузе */
                                    }
                                    currentSecOld = currentSec
                                    currentTime = TimeToString(currentSec / 1000) + "/" + playLength
                                    tvLength.text = currentTime
                                    progressBar.progress = currentSec
//                                } /*  barPosition.collect
                                } catch (ex: java.lang.IllegalStateException) {
                                    progressBar.progress = 0
                                    tvLength.text = TimeToString(0) + "/" + playLength
                                    play
                                    isPaused.set(true)
                                    player?.release()
                                }
                            }
                        }
                    }
                    checkButton = !checkButton
                }
            }

            player?.setOnCompletionListener {
                player.release()
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
}

class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }
}