package com.example.rtmplibrary.domain.repository

import androidx.lifecycle.Lifecycle
import com.pedro.library.view.OpenGlView

interface StreamRepository {

    suspend fun initCamera(openGlView: OpenGlView): Result<Unit>

    suspend fun bindLifecycle(lifecycle: Lifecycle): Result<Unit>

    suspend fun startStream(url: String): Result<Unit>

    suspend fun stopStream(): Result<Unit>

    suspend fun startPreview(): Result<Unit>

    suspend fun stopPreview(): Result<Unit>

    suspend fun switchCamera(): Result<Unit>

    suspend fun isStreaming(): Result<Boolean>

    suspend fun release(): Result<Unit>

    suspend fun setStreamCallbacks(
        onConnectionStarted: () -> Unit,
        onConnectionSuccess: () -> Unit,
        onConnectionFailed: (String) -> Unit,
        onDisconnected: () -> Unit,
        onAuthError: () -> Unit,
        onPreparationFailed: (String) -> Unit
    ): Result<Unit>

}
//Bu kod, yayını başlatma, durdurma ve yayın durumunu izleme
// işlemlerinin hangi fonksiyonlarla yapılacağını tanımlayan bir interface’tir.