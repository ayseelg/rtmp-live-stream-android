package com.example.rtmplibrary.domain.usecase

import com.example.rtmplibrary.domain.repository.StreamRepository

class ReleaseStreamUseCase (
    private val repository: StreamRepository
) {

    suspend operator fun invoke(): Result<Unit> {
        return repository.release()
    }

}
//Bu kod, hafıza sızıntılarını (memory leak) önlemek için yayınla 
//ilgili tüm referansları (kamera görünümü, callbackler vb.) temizler.
