package com.example.rtmplibrary.data.datasource

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.rtmplibrary.domain.model.StreamState

class StreamDataSource {

    private val state = MutableStateFlow<StreamState>(StreamState.Idle)

    fun startStream(url: String) {
        state.value = StreamState.Connecting
        state.value = StreamState.Streaming
    }

    fun stopStream() {
        state.value = StreamState.Stopped
    }

    fun observeState(): StateFlow<StreamState> = state
}
//Bu sınıf, yayının durumunu (Idle, Connecting, Streaming, Stopped) tutan
// ve yayın başlatılıp durdurulduğunda bu durumu güncelleyen veri kaynağıdır.