package com.example.rtmplibrary.domain.usecase

import com.example.rtmplibrary.domain.repository.StreamRepository

class StopPreviewUseCase (
    private val repository: StreamRepository
) {

    suspend operator fun invoke(): Result<Unit> {
        return repository.stopPreview()
    }

}
//Bu kod, kamera önizlemesini durdurmak için yazılmış bir "use case" sınıfıdır

