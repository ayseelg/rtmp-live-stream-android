package com.example.rtmplibrary.domain.usecase

import com.example.rtmplibrary.domain.repository.StreamRepository

class StartPreviewUseCase(
    private val repository: StreamRepository
) {

    operator fun invoke() {
        repository.startPreview()
    }

}
//Bu kod, kamera önizlemesini başlatmak için yazılmış bir "use case" sınıfıdır
