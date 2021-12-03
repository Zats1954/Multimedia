package com.android.multimedia


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.multimedia.adapter.SongAdapter
import com.android.multimedia.databinding.ActivityAppBinding
import com.android.multimedia.viewmodel.SongViewModel



class AppActivity : AppCompatActivity(R.layout.activity_app) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(getLayoutInflater())
        setContentView(binding.root)

        val viewModel = SongViewModel(resources.openRawResource(R.raw.sng))


        val adapter = SongAdapter { }

        binding.rvSongView.adapter = adapter

        viewModel.data.observe(this){songs ->
            adapter.submitList(songs)
        }
    }
}