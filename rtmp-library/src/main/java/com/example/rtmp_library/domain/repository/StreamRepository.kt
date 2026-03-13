package com.example.rtmplibrary.domain.repository

import com.pedro.library.view.OpenGlView

interface StreamRepository {

    fun initCamera(openGlView: OpenGlView): Result<Unit>

    fun startStream(url: String): Result<Unit>

    fun stopStream(): Result<Unit>

    fun startPreview(): Result<Unit>

    fun stopPreview(): Result<Unit>

    fun switchCamera(): Result<Unit>

    fun isStreaming(): Result<Boolean>

    fun setStreamCallbacks(
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