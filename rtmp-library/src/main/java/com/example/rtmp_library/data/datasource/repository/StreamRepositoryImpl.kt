package com.example.rtmplibrary.data.repository

import com.pedro.library.view.OpenGlView
import com.example.rtmplibrary.data.datasource.StreamDataSource
import com.example.rtmplibrary.domain.repository.StreamRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class StreamRepositoryImpl (
    private val dataSource: StreamDataSource
) : StreamRepository {

    override suspend fun initCamera(openGlView: OpenGlView): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataSource.initCamera(openGlView)
        }
    }

    override suspend fun bindLifecycle(lifecycle: androidx.lifecycle.Lifecycle): Result<Unit> = withContext(Dispatchers.Main) {
        runCatching {
            dataSource.bindLifecycle(lifecycle)
        }
    }

    override suspend fun startStream(url: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataSource.startStream(url)
        }
    }

    override suspend fun stopStream(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataSource.stopStream()
        }
    }

    override suspend fun startPreview(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataSource.startPreview()
        }
    }

    override suspend fun stopPreview(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataSource.stopPreview()
        }
    }

    override suspend fun switchCamera(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataSource.switchCamera()
        }
    }

    override suspend fun isStreaming(): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            dataSource.isStreaming
        }
    }

    override suspend fun release(): Result<Unit> = withContext(Dispatchers.Main) {
        // UI thread üzerinde release edilmeli çünkü içerdeki View referanslarýný serbest býrakýyoruz
        runCatching {
            dataSource.release()
        }
    }

    override suspend fun setStreamCallbacks(
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
