package com.example.rtmplibrary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rtmplibrary.data.datasource.StreamDataSource
import com.example.rtmplibrary.data.repository.StreamRepositoryImpl
import com.example.rtmplibrary.domain.usecase.BindLifecycleUseCase
import com.example.rtmplibrary.domain.usecase.InitCameraUseCase
import com.example.rtmplibrary.domain.usecase.ObserveStreamStateUseCase
import com.example.rtmplibrary.domain.usecase.ReleaseStreamUseCase
import com.example.rtmplibrary.domain.usecase.StartPreviewUseCase
import com.example.rtmplibrary.domain.usecase.StartStreamUseCase
import com.example.rtmplibrary.domain.usecase.StopPreviewUseCase
import com.example.rtmplibrary.domain.usecase.StopStreamUseCase
import com.example.rtmplibrary.domain.usecase.SwitchCameraUseCase

class StreamViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StreamViewModel::class.java)) {
            val dataSource = StreamDataSource()
            val repository = StreamRepositoryImpl(dataSource)
            
            return StreamViewModel(
                observeStreamStateUseCase = ObserveStreamStateUseCase(repository),
                startStreamUseCase = StartStreamUseCase(repository),
                stopStreamUseCase = StopStreamUseCase(repository),
                initCameraUseCase = InitCameraUseCase(repository),
                startPreviewUseCase = StartPreviewUseCase(repository),
                stopPreviewUseCase = StopPreviewUseCase(repository),
                switchCameraUseCase = SwitchCameraUseCase(repository),
                releaseStreamUseCase = ReleaseStreamUseCase(repository),
                bindLifecycleUseCase = BindLifecycleUseCase(repository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
