package com.example.rtmplibrary.domain.usecase

import com.example.rtmplibrary.domain.repository.StreamRepository
import javax.inject.Inject

class StartPreviewUseCase @Inject constructor(
    private val repository: StreamRepository
) {

    operator fun invoke() {
        repository.startPreview()
    }

}
//Bu kod, kamera önizlemesini başlatmak için yazılmış bir "use case" sınıfıdır
