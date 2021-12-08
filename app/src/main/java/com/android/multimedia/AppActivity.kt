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
        val binding = ActivityAppBinding.inflate(getLayoutInflater())
        setContentView(binding.root)

        val viewModel = SongViewModel(resources.openRawResource(R.raw.sng))


        val songPrefix = viewModel.songPrefix

        val adapter = SongAdapter(object : OnInteractionListener {
            override fun onComplite(song: Song) {
                println("******** ${song.file}")
            }
        }, songPrefix)

        binding.rvSongView.adapter = adapter
        binding.tvAlbomName.text = viewModel.albom
        binding.tvArtistName.text = viewModel.artist
        binding.tvInfo.text = viewModel.info

//        viewModel.data.observe(this){ songs ->
            adapter.submitList(viewModel.listSongs)
    }

}