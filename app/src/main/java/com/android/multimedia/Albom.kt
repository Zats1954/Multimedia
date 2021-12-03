package com.android.multimedia

data class Albom(val id: Int,
                 val title: String,
                 val subtitle: String,
                 val artist: String,
                 val published: String,
                 val genre: String,
                 val tracks: List<Song> )

data class Song(val id: Int,
                 val file: String)
