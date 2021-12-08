package com.android.multimedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.multimedia.Albom
import com.android.multimedia.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.io.InputStreamReader

class SongRepositoryImplement(private val resource: InputStream): SongRepository  {

    private   val gson = Gson()
    private   val listSongType = TypeToken.getParameterized(Albom::class.java).type
    private   val myReader = InputStreamReader(resource)
    private   val albom: Albom = gson.fromJson(myReader, listSongType)
    private   val songPrefix = "https://${albom.subtitle}/examples/mp3/${albom.title}"
    private   val songs = albom.tracks
//    private val data = MutableLiveData(songs)
//    override fun getSongs(): LiveData<List<Song>> = data

    override fun getSongs() = songs

    override fun getPrefix() = songPrefix

    override fun getAlbom() = albom.title

    override fun getArtist() = albom.artist

    override fun getInfo() = "${albom.published}  ${albom.genre}"

    fun getSongName(id:Int) = albom.tracks.get(id).file

    fun getsongPrefix() = "https://${albom.subtitle}/examples/mp3/${albom.title}"

}