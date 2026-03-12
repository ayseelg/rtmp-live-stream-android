package com.example.rtmplibrary.data.repository

import com.pedro.library.view.OpenGlView
import com.example.rtmplibrary.data.datasource.StreamDataSource
import com.example.rtmplibrary.domain.model.StreamState
import com.example.rtmplibrary.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class StreamRepositoryImpl @Inject constructor(
    private val dataSource: StreamDataSource
) : StreamRepository {

    override fun initCamera(openGlView: OpenGlView) {
        dataSource.initCamera(openGlView)
    }

    override fun startStream(url: String) {
        dataSource.startStream(url)
    }

    override fun stopStream() {
        dataSource.stopStream()
    }

    override fun startPreview() {
        dataSource.startPreview()
    }

    override fun stopPreview() {
        dataSource.stopPreview()
    }

    override fun switchCamera() {
        dataSource.switchCamera()
    }

    override val isStreaming: Boolean get() = dataSource.isStreaming

    override fun observeStreamState(): Flow<StreamState> {
        return dataSource.observeState()
    }
}
//Bu kod, StreamRepository interface'inde tanimlanan yayin baslatma, durdurma ve
// yayin durumunu izleme fonksiyonlarini gercek olarak calistiran
// (implement eden) siniftir ve ViewModel ile DataSource arasinda aracilik yapar.