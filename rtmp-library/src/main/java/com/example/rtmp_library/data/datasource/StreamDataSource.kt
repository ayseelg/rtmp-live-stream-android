package com.example.rtmplibrary.data.datasource

import com.pedro.common.ConnectChecker
import com.pedro.library.rtmp.RtmpCamera2
import com.pedro.library.view.OpenGlView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.rtmplibrary.domain.model.StreamState

// RtmpCamera2 artık data katmanında tutuluyor — yayın kaynağı burası
class StreamDataSource : ConnectChecker {

    private val state = MutableStateFlow<StreamState>(StreamState.Idle)

    // Fragment'ten OpenGlView alındıktan sonra initCamera() ile başlatılır
    private lateinit var rtmpCamera: RtmpCamera2

    // Fragment, openGlView hazır olunca bu metodu çağırır
    fun initCamera(openGlView: OpenGlView) {
        rtmpCamera = RtmpCamera2(openGlView, this)
    }

    fun startStream(url: String) {// kamera ve mikrofon hazılanıp yayyın başlatılıyor
        state.value = StreamState.Connecting
        rtmpCamera.startPreview()
        if (rtmpCamera.prepareAudio() && rtmpCamera.prepareVideo()) {
            rtmpCamera.startStream(url)
        } else {
            rtmpCamera.stopPreview()
            state.value = StreamState.Error("Kamera/ses hazırlanamadı")
        }
    }

    fun stopStream() {//yyaın durdurma
        if (rtmpCamera.isStreaming) rtmpCamera.stopStream()
        rtmpCamera.stopPreview()
        state.value = StreamState.Stopped
    }

    fun startPreview() {
        rtmpCamera.startPreview()
    }

    fun stopPreview() {
        rtmpCamera.stopPreview()
    }

    fun switchCamera() {// kamera değiştirme
        rtmpCamera.switchCamera()
    }

    val isStreaming: Boolean get() = rtmpCamera.isStreaming

    fun observeState(): StateFlow<StreamState> = state

    // ConnectChecker callback'leri — yayın durumunu state'e yansıtır
    override fun onConnectionStarted(url: String) {
        state.value = StreamState.Connecting
    }

    override fun onConnectionSuccess() {
        state.value = StreamState.Streaming
    }

    override fun onConnectionFailed(reason: String) {
        rtmpCamera.stopStream()
        rtmpCamera.stopPreview()
        state.value = StreamState.Error(reason)
    }

    override fun onNewBitrate(bitrate: Long) {}

    override fun onDisconnect() {
        state.value = StreamState.Stopped
    }

    override fun onAuthError() {
        state.value = StreamState.Error("Sunucu auth hatası")
    }

    override fun onAuthSuccess() {}
}
//Bu sınıf, yayının durumunu (Idle, Connecting, Streaming, Stopped, Error) tutan
// ve yayın başlatılıp durdurulduğunda bu durumu güncelleyen veri kaynağıdır.