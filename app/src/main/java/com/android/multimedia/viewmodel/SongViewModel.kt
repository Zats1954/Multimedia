package com.android.multimedia.viewmodel

import androidx.lifecycle.ViewModel
import com.android.multimedia.repository.SongRepository
import com.android.multimedia.repository.SongRepositoryImplement
import java.io.InputStream


class SongViewModel(private val myResource: InputStream): ViewModel() {

    private val repository: SongRepository = SongRepositoryImplement(myResource)
     var data = repository.getSongs()

}
