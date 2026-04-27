package com.example.rtmplibrary.domain.usecase

import com.example.rtmplibrary.domain.repository.StreamRepository

class SwitchCameraUseCase (
    private val repository: StreamRepository
) {

    suspend operator fun invoke(): Result<Unit> {
        return repository.switchCamera()
    }

}
//Bu kod, ön/arka kamera geçişini yapmak için yazılmış bir "use case" sınıfıdır

