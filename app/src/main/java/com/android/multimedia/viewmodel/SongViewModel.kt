package com.android.multimedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.multimedia.Song
import com.android.multimedia.api.APIService
import com.android.multimedia.repository.SongRepository
import com.android.multimedia.repository.SongRepositoryImplement

class SongViewModel( ): ViewModel() {
    val service = APIService()
    val repository: SongRepository = SongRepositoryImplement(service)

    val albom = repository.getAlbom()
    val artist = repository.getArtist()
    val info = repository.getInfo()
    val listSongs = repository.getSongs()


//    val _playingTime = MutableLiveData<Int>()
//    val playingTime: LiveData<Int>
//        get() = _playingTime


    fun getNext(song: Song): Song {
        listSongs.indexOfFirst { nom -> nom.id == song.id
        }.let{
            if(it == listSongs.lastIndex){
                listSongs.get(0)
            } else {
                listSongs.get(it + 1)
            }
        }.let{ return it }
//        println("!!нет песни с id ${song.id}")
//        throw Exception()
    }
}
