package com.example.rtmplibrary.domain.usecase

import com.example.rtmplibrary.domain.repository.StreamRepository
import javax.inject.Inject

class SwitchCameraUseCase @Inject constructor(
    private val repository: StreamRepository
) {

    operator fun invoke(): Result<Unit> {
        return repository.switchCamera()
    }

}
//Bu kod, ön/arka kamera geçişini yapmak için yazılmış bir "use case" sınıfıdır
