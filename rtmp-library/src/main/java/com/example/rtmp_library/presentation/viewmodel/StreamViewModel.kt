package com.example.rtmplibrary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtmplibrary.domain.usecase.StartStreamUseCase
import com.example.rtmplibrary.domain.usecase.StopStreamUseCase
import com.example.rtmplibrary.domain.usecase.ObserveStreamStateUseCase
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted



class StreamViewModel(
    observeStreamStateUseCase: ObserveStreamStateUseCase,
    private val startStreamUseCase: StartStreamUseCase,
    private val stopStreamUseCase: StopStreamUseCase
) : ViewModel() {

    val streamState = observeStreamStateUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            null
        )

    fun startStream(url: String) {
        startStreamUseCase(url)
    }

    fun stopStream() {
        stopStreamUseCase()
    }

}
//Bu kod, yayını başlatma, durdurma ve durumunu takip etme
// işlemlerini yöneten ViewModel’dir; UI, bu sınıf üzerinden stream’i başlatır,
// durdurur ve durumunu gerçek zamanlı olarak alır.