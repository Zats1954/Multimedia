package com.android.multimedia.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import com.android.multimedia.Song
import com.android.multimedia.repository.SongRepository
import com.android.multimedia.repository.SongRepositoryImplement
import java.io.InputStream


class SongViewModel(private val myResource: InputStream): ViewModel() {


    private val repository: SongRepository = SongRepositoryImplement(myResource)
    val albom = repository.getAlbom()
    val artist = repository.getArtist()
    val info = repository.getInfo()
    val songPrefix = repository.getPrefix()
    var listSongs = repository.getSongs()
    private val player: MediaPlayer? = MediaPlayer()

    fun preparePlay(song:Song){
        player?.reset()
        player?.setDataSource( songPrefix + song.file)
        player?.prepare()
        val playSec: Int = player?.duration?.let { it / 1000 } ?: 1
        val playLength = TimeToString(playSec)

    }

    fun playNext(song: Song) {
         if(song.id == listSongs.lastIndex){
             play(listSongs.get(0))
         } else {
             play(listSongs.get(song.id + 1))
         }
    }


    private fun play(song: Song) {

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
