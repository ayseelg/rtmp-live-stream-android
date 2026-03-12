package com.example.rtmplibrary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.library.view.OpenGlView
import com.example.rtmplibrary.domain.usecase.InitCameraUseCase
import com.example.rtmplibrary.domain.usecase.StartStreamUseCase
import com.example.rtmplibrary.domain.usecase.StopStreamUseCase
import com.example.rtmplibrary.domain.usecase.ObserveStreamStateUseCase
import com.example.rtmplibrary.domain.usecase.StartPreviewUseCase
import com.example.rtmplibrary.domain.usecase.StopPreviewUseCase
import com.example.rtmplibrary.domain.usecase.SwitchCameraUseCase
import com.example.rtmplibrary.domain.model.StreamState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject

@HiltViewModel
class StreamViewModel @Inject constructor(
    observeStreamStateUseCase: ObserveStreamStateUseCase,
    private val startStreamUseCase: StartStreamUseCase,
    private val stopStreamUseCase: StopStreamUseCase,
    private val initCameraUseCase: InitCameraUseCase,
    private val startPreviewUseCase: StartPreviewUseCase,
    private val stopPreviewUseCase: StopPreviewUseCase,
    private val switchCameraUseCase: SwitchCameraUseCase
) : ViewModel() {

    val streamState = observeStreamStateUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            null
        )

    val isStreaming: Boolean get() = streamState.value is StreamState.Streaming

    fun initCamera(openGlView: OpenGlView) {
        initCameraUseCase(openGlView)
    }

    fun startStream(url: String) {
        startStreamUseCase(url)
    }

    fun stopStream() {
        stopStreamUseCase()
    }

    fun startPreview() {
        startPreviewUseCase()
    }

    fun stopPreview() {
        stopPreviewUseCase()
    }

    fun switchCamera() {
        switchCameraUseCase()
    }

}
//Bu kod, yayini baslatma, durdurma ve durumunu takip etme
// islemlerini yoneten ViewModel dir; UI, bu sinif uzerinden stream i baslatir,
// durdurur ve durumunu gercek zamanli olarak alir.