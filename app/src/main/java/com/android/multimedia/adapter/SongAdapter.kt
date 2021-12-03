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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow


typealias OnInteractionListener = (song: Song) -> Unit

class SongAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Song, SongViewHolder>(SongDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = CardSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song)
    }
}


class SongViewHolder(
    private val binding: CardSongBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    private val mediaObserver = MediaLifecycleObserver()
    private var checkButton: Boolean = false
    private val player = mediaObserver.player

    private var isPaused = false

    fun bind(song: Song) {
        binding.apply {

            player?.reset()
            player?.setDataSource(
                song.file
//                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
            )
            player?.prepare()
            val playSec: Int = player?.duration?.let { it / 1000 } ?: 1
            val playLength = TimeToString(playSec)
            tvLength.setText("0:00/${playLength}")
            progressBar.max = player?.duration ?: 1

            val barPosition: Flow<Int> = flow {
                delay(10L)
                player?.currentPosition?.let {
                    emit(it)
                }
            }

            play.setOnClickListener {
                var currentSec: Int
                if (checkButton) {
                    player?.pause()
                    isPaused = true
                    checkButton = !checkButton
                } else {
                    isPaused = false
                    player?.start()
                    CoroutineScope(Dispatchers.Main).launch {
                        while (!isPaused) {
                            withTimeout(1000L) {
                                barPosition.collect {
                                    currentSec = player?.currentPosition?.let { it / 1000 } ?: 1
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