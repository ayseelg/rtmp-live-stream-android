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
import com.example.rtmplibrary.domain.usecase.ReleaseStreamUseCase
import com.example.rtmplibrary.domain.usecase.BindLifecycleUseCase
import com.example.rtmplibrary.domain.model.StreamState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamViewModel @Inject constructor(
    private val observeStreamStateUseCase: ObserveStreamStateUseCase,
    private val startStreamUseCase: StartStreamUseCase,
    private val stopStreamUseCase: StopStreamUseCase,
    private val initCameraUseCase: InitCameraUseCase,
    private val startPreviewUseCase: StartPreviewUseCase,
    private val stopPreviewUseCase: StopPreviewUseCase,
    private val switchCameraUseCase: SwitchCameraUseCase,
    private val releaseStreamUseCase: ReleaseStreamUseCase,
    private val bindLifecycleUseCase: BindLifecycleUseCase
) : ViewModel() {

    private fun handleActionResult(result: Result<Unit>) {
        result.exceptionOrNull()?.let { error ->
            _streamState.value = StreamState.Error(error.message ?: "Bilinmeyen hata")
        }
    }

    private val _streamState = MutableStateFlow<StreamState>(StreamState.Idle)
    val streamState: StateFlow<StreamState> = _streamState.asStateFlow()

    init {
        //RTMP yayınının durumunu dinler
        //ve yayının durumuna göre state günceller.
        viewModelScope.launch {
            handleActionResult(
                observeStreamStateUseCase(
                    onConnectionStarted = {//onConnectionStarted → RTMP sunucusuna bağlantı başlatıldığında çalışır ve state’i Connecting olarak günceller.
                        _streamState.value = StreamState.Connecting
                    },
                    onConnectionSuccess = {
                        _streamState.value = StreamState.Streaming
                    },
                    onConnectionFailed = { reason ->
                        _streamState.value = StreamState.Error(reason)
                    },
                    onDisconnected = {
                        _streamState.value = StreamState.Stopped
                    },
                    onAuthError = {
                        _streamState.value = StreamState.Error("Sunucu auth hatası")
                    },
                    onPreparationFailed = { reason ->
                        _streamState.value = StreamState.Error(reason)
                    }
                )
            )
        }
    }

    val isStreaming: Boolean get() = streamState.value is StreamState.Streaming

    fun bindLifecycle(lifecycle: androidx.lifecycle.Lifecycle) {
        viewModelScope.launch {
            bindLifecycleUseCase(lifecycle)
        }
    }

    fun initCamera(openGlView: OpenGlView) {
        viewModelScope.launch {
            handleActionResult(initCameraUseCase(openGlView))
        }
    }

    fun startStream(url: String) {
        _streamState.value = StreamState.Connecting
        viewModelScope.launch {
            handleActionResult(startStreamUseCase(url))
        }
    }

    fun stopStream() {
        viewModelScope.launch {
            handleActionResult(stopStreamUseCase())
            if (_streamState.value !is StreamState.Error) {
                _streamState.value = StreamState.Stopped
            }
        }
    }

    fun startPreview() {
        viewModelScope.launch {
            handleActionResult(startPreviewUseCase())
        }
    }

    fun stopPreview() {
        viewModelScope.launch {
            handleActionResult(stopPreviewUseCase())
        }
    }

    fun switchCamera() {
        viewModelScope.launch {
            handleActionResult(switchCameraUseCase())
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            releaseStreamUseCase()
        }
        super.onCleared()
    }
}
//Bu kod, yayini baslatma, durdurma ve durumunu takip etme
// islemlerini yoneten ViewModel dir; UI, bu sinif uzerinden stream i baslatir,
// durdurur ve durumunu gercek zamanli olarak alir.