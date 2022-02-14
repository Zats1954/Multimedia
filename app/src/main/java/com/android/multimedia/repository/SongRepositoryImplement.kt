package com.android.multimedia.repository

import com.android.multimedia.Albom
import com.android.multimedia.BuildConfig.BASE_URL
import com.android.multimedia.Song
import com.android.multimedia.api.APIService

class SongRepositoryImplement(service: APIService) : SongRepository {

    var albom : Albom? = service.getAlbom()

    override fun getSongs(): List<Song> {
        return albom?.tracks ?: emptyList()
    }

    override fun getPrefix(): String {return  BASE_URL}

    override fun getAlbom()= albom?.title ?: ""

    override fun getArtist() = albom?.artist ?: " "

    override fun getInfo() = "${albom?.published}  ${albom?.genre}"
}