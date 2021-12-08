package com.android.multimedia.repository

import androidx.lifecycle.LiveData
import com.android.multimedia.Song
import java.io.InputStream

interface SongRepository {
//    fun getSongs(): LiveData<List<Song>>
    fun getSongs():List<Song>
    fun getPrefix(): String
    fun getAlbom(): String
    fun getArtist(): String
    fun getInfo(): String
}