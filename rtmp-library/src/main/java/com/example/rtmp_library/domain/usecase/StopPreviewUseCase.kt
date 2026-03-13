package com.example.rtmplibrary.domain.usecase

import com.example.rtmplibrary.domain.repository.StreamRepository
import javax.inject.Inject

class StopPreviewUseCase @Inject constructor(
    private val repository: StreamRepository
) {

    operator fun invoke(): Result<Unit> {
        return repository.stopPreview()
    }

}
//Bu kod, kamera önizlemesini durdurmak için yazılmış bir "use case" sınıfıdır
