package com.example.rtmplibrary.data.datasource

import com.pedro.common.ConnectChecker
import com.pedro.library.rtmp.RtmpCamera2
import com.pedro.library.view.OpenGlView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamDataSource @Inject constructor() : ConnectChecker {

    private var onConnectionStarted: (() -> Unit)? = null
    private var onConnectionSuccess: (() -> Unit)? = null
    private var onConnectionFailed: ((String) -> Unit)? = null
    private var onDisconnected: (() -> Unit)? = null
    private var onAuthError: (() -> Unit)? = null
    private var onPreparationFailed: ((String) -> Unit)? = null


    private lateinit var rtmpCamera: RtmpCamera2

    private fun getCameraOrThrow(): RtmpCamera2 {
        if (!::rtmpCamera.isInitialized) {
            throw IllegalStateException("Kamera initialize edilmedi")
        }
        return rtmpCamera
    }


    fun initCamera(openGlView: OpenGlView) {
        rtmpCamera = RtmpCamera2(openGlView, this)
    }

    fun startStream(url: String) {// kamera ve mikrofon hazılanıp yayyın başlatılıyor
        val camera = getCameraOrThrow()
        onConnectionStarted?.invoke()
        camera.startPreview()
        if (camera.prepareAudio() && camera.prepareVideo()) {
            camera.startStream(url)
        } else {
            camera.stopPreview()
            val reason = "Kamera/ses hazırlanamadı"
            onPreparationFailed?.invoke(reason)
            throw IllegalStateException(reason)
        }
    }

    fun stopStream() {//yyaın durdurma
        val camera = getCameraOrThrow()
        if (camera.isStreaming) camera.stopStream()
        camera.stopPreview()
        onDisconnected?.invoke()
    }

    fun startPreview() {
        getCameraOrThrow().startPreview()
    }

    fun stopPreview() {
        getCameraOrThrow().stopPreview()
    }

    fun switchCamera() {// kamera değiştirme
        getCameraOrThrow().switchCamera()
    }

    val isStreaming: Boolean get() = getCameraOrThrow().isStreaming

    fun setCallbacks(
        onConnectionStarted: () -> Unit,
        onConnectionSuccess: () -> Unit,
        onConnectionFailed: (String) -> Unit,
        onDisconnected: () -> Unit,
        onAuthError: () -> Unit,
        onPreparationFailed: (String) -> Unit
    ) {
        this.onConnectionStarted = onConnectionStarted
        this.onConnectionSuccess = onConnectionSuccess
        this.onConnectionFailed = onConnectionFailed
        this.onDisconnected = onDisconnected
        this.onAuthError = onAuthError
        this.onPreparationFailed = onPreparationFailed
    }


    // ConnectChecker callback'leri — yayın durumunu state'e yansıtır
    override fun onConnectionStarted(url: String) {
        onConnectionStarted?.invoke()
    }

    override fun onConnectionSuccess() {
        onConnectionSuccess?.invoke()
    }

    override fun onConnectionFailed(reason: String) {
        val camera = getCameraOrThrow()
        camera.stopStream()
        camera.stopPreview()
        onConnectionFailed?.invoke(reason)
    }

    override fun onNewBitrate(bitrate: Long) {}

    override fun onDisconnect() {
        onDisconnected?.invoke()
    }

    override fun onAuthError() {
        onAuthError?.invoke()
    }

    override fun onAuthSuccess() {}
}
//Bu sınıf, yayının durumunu (Idle, Connecting, Streaming, Stopped, Error) tutan
// ve yayın başlatılıp durdurulduğunda bu durumu güncelleyen veri kaynağıdır.