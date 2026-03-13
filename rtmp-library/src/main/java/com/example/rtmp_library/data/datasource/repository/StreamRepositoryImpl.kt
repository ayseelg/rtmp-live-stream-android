package com.example.rtmplibrary.data.repository

import com.pedro.library.view.OpenGlView
import com.example.rtmplibrary.data.datasource.StreamDataSource
import com.example.rtmplibrary.domain.repository.StreamRepository
import javax.inject.Inject


class StreamRepositoryImpl @Inject constructor(
    private val dataSource: StreamDataSource
) : StreamRepository {

    override fun initCamera(openGlView: OpenGlView): Result<Unit> {
        return runCatching {
            dataSource.initCamera(openGlView)
        }
    }

    override fun startStream(url: String): Result<Unit> {
        return runCatching {
            dataSource.startStream(url)
        }
    }

    override fun stopStream(): Result<Unit> {
        return runCatching {
            dataSource.stopStream()
        }
    }

    override fun startPreview(): Result<Unit> {
        return runCatching {
            dataSource.startPreview()
        }
    }

    override fun stopPreview(): Result<Unit> {
        return runCatching {
            dataSource.stopPreview()
        }
    }

    override fun switchCamera(): Result<Unit> {
        return runCatching {
            dataSource.switchCamera()
        }
    }

    override fun isStreaming(): Result<Boolean> {
        return runCatching {
            dataSource.isStreaming
        }
    }

    override fun setStreamCallbacks(
        onConnectionStarted: () -> Unit,
        onConnectionSuccess: () -> Unit,
        onConnectionFailed: (String) -> Unit,
        onDisconnected: () -> Unit,
        onAuthError: () -> Unit,
        onPreparationFailed: (String) -> Unit
    ): Result<Unit> {
        return runCatching {
            dataSource.setCallbacks(
                onConnectionStarted = onConnectionStarted,
                onConnectionSuccess = onConnectionSuccess,
                onConnectionFailed = onConnectionFailed,
                onDisconnected = onDisconnected,
                onAuthError = onAuthError,
                onPreparationFailed = onPreparationFailed
            )
        }
    }
}
//Bu kod, StreamRepository interface'inde tanimlanan yayin baslatma, durdurma ve
// yayin durumunu izleme fonksiyonlarini gercek olarak calistiran
// (implement eden) siniftir ve ViewModel ile DataSource arasinda aracilik yapar.