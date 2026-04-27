package com.example.rtmplibrary.data.datasource

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.pedro.common.ConnectChecker
import com.pedro.library.rtmp.RtmpCamera2
import com.pedro.library.view.OpenGlView

class StreamDataSource () : ConnectChecker, DefaultLifecycleObserver {

    private var onConnectionStarted: (() -> Unit)? = null
    private var onConnectionSuccess: (() -> Unit)? = null
    private var onConnectionFailed: ((String) -> Unit)? = null
    private var onDisconnected: (() -> Unit)? = null
    private var onAuthError: (() -> Unit)? = null
    private var onPreparationFailed: ((String) -> Unit)? = null


    private lateinit var rtmpCamera: RtmpCamera2

    private fun getCameraOrThrow(): RtmpCamera2 {
        if (!this::rtmpCamera.isInitialized) {
            throw IllegalStateException("Kamera initialize edilmedi")
        }
        return rtmpCamera
    }

    fun initCamera(openGlView: OpenGlView) {
        rtmpCamera = RtmpCamera2(openGlView, this)
    }

    fun release() {
        if (this::rtmpCamera.isInitialized) {
            if (rtmpCamera.isStreaming) {
                rtmpCamera.stopStream()
            }
            rtmpCamera.stopPreview()
        }
        // Memory leak'leri Ã¶nlemek iÃ§in callback'leri temizliyoruz.
        onConnectionStarted = null
        onConnectionSuccess = null
        onConnectionFailed = null
        onDisconnected = null
        onAuthError = null
        onPreparationFailed = null
    }

    fun bindLifecycle(lifecycle: androidx.lifecycle.Lifecycle) {
        lifecycle.addObserver(this)
    }

    fun startStream(url: String) {// kamera ve mikrofon hazÄ±lanÄ±p yayyÄ±n baÅŸlatÄ±lÄ±yor
        val camera = getCameraOrThrow()
        onConnectionStarted?.invoke()
        camera.startPreview()
        if (camera.prepareAudio() && camera.prepareVideo()) {
            camera.startStream(url)
        } else {
            camera.stopPreview()
            val reason = "Kamera/ses hazÄ±rlanamadÄ±"
            onPreparationFailed?.invoke(reason)
            throw IllegalStateException(reason)
        }
    }

    fun stopStream() {//yyaÄ±n durdurma
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

    fun switchCamera() {// kamera deÄŸiÅŸtirme
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


    // ConnectChecker callback'leri â€” yayÄ±n durumunu state'e yansÄ±tÄ±r
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

    // --- LifecycleAwareness: Uygulama Arkaplana GeÃ§tiÄŸinde Memory Leak ve Ã‡Ã¶kmeleri Ã–nleme ---
    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        // EÄŸer arkaplan task'Ä± veya Foreground Service entegrasyonumuz tam deÄŸilse,
        // kullanÄ±cÄ± uygulamadan Ã§Ä±kÄ±nca crash almamak iÃ§in yayÄ±nÄ±/kamerayÄ± arkaplanda durdurmak iyi bir pratiktir.
        if (this::rtmpCamera.isInitialized && rtmpCamera.isStreaming) {
            stopStream()
        }
        if (this::rtmpCamera.isInitialized) {
            stopPreview()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        // Activity/Fragment tamamen yok edilince bellekten Objeleri temizle. (Memory Leak prevention)
        release()
    }
}
//Bu sÄ±nÄ±f, yayÄ±nÄ±n durumunu (Idle, Connecting, Streaming, Stopped, Error) tutan
// ve yayÄ±n baÅŸlatÄ±lÄ±p durdurulduÄŸunda bu durumu gÃ¼ncelleyen veri kaynaÄŸÄ±dÄ±r.

