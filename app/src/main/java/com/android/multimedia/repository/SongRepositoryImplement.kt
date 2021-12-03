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
    val myReader = InputStreamReader(resource)
    val albom: Albom = gson.fromJson(myReader, listSongType)
    val songPrefix = "https://${albom.subtitle}/examples/mp3/${albom.title}"
    val songs = albom.tracks.map{song ->
        song.copy(file = "${songPrefix}${song.file}")}
    private val data = MutableLiveData(songs)


    override fun getSongs(): LiveData<List<Song>> = data

    override fun getPrefix(): String{
        return songPrefix }
}