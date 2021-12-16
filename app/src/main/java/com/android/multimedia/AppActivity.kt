package com.android.multimedia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.multimedia.adapter.OnInteractionListener
import com.android.multimedia.adapter.SongAdapter
import com.android.multimedia.databinding.ActivityAppBinding
import com.android.multimedia.viewmodel.SongViewModel

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = SongViewModel(resources.openRawResource(R.raw.sng1))
        val binding = ActivityAppBinding.inflate(getLayoutInflater())
        setContentView(binding.root)


        val adapter = SongAdapter(object : OnInteractionListener {
                     override fun onComplite(song: Song): Song {
                          println("******** ended ${song.id}  ${song.file}")
                          return viewModel.getNext(song)
                }

                override fun clickSong(songId: Int): Song {
                    return viewModel.listSongs.first { it.id == songId }
                }
            },
            viewModel.songPrefix
        )


        binding.tvAlbomName.text = viewModel.albom
        binding.tvArtistName.text = viewModel.artist
        binding.tvInfo.text = viewModel.info

        binding.rvSongView.adapter = adapter

        adapter.submitList(viewModel.listSongs)
    }

}