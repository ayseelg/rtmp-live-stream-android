package com.example.rtmplibrary.domain.usecase

import com.pedro.library.view.OpenGlView
import com.example.rtmplibrary.domain.repository.StreamRepository

class InitCameraUseCase (
    private val repository: StreamRepository
) {

    suspend operator fun invoke(openGlView: OpenGlView): Result<Unit> {
        return repository.initCamera(openGlView)
    }

}
//Bu kod, RtmpCamera2'yi başlatmak için yazılmış bir "use case" sınıfıdır;
// Fragment'ten gelen OpenGlView'i data katmanına iletir

