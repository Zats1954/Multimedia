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

//        val viewModel = SongViewModel(resources.openRawResource(R.raw.sng1))
        val viewModel = SongViewModel( )
        val binding = ActivityAppBinding.inflate(getLayoutInflater())
        setContentView(binding.root)

        val adapter = SongAdapter(object : OnInteractionListener {
                     override fun onComplite(song: Song) {
                         println("******** ended ${song.id}  ${song.file}")
                         val next = viewModel.getNext(song)
                         val adapter = binding.rvSongView.adapter?: return
                         val position = viewModel.listSongs.indexOfFirst { nom ->
                             nom.id == song.id
                         }
                         val nextPosition = position.inc().let {
                             if (it == adapter.itemCount) 0 else it
                         }
                         adapter.notifyItemChanged(nextPosition, next)
                     }

                override fun clickSong(songId: Int): Song {
                    return viewModel.listSongs.first { it.id == songId }
                }
            }, viewModel.repository.getPrefix())

        binding.tvAlbomName.text = viewModel.albom
        binding.tvArtistName.text = viewModel.artist
        binding.tvInfo.text = viewModel.info

        binding.rvSongView.adapter = adapter

        adapter.submitList(viewModel.listSongs)
    }
}