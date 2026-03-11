package com.example.rtmplibrary.domain.usecase

import com.example.rtmplibrary.domain.repository.StreamRepository

class StopStreamUseCase(
    private val repository: StreamRepository
) {

    operator fun invoke() {
        repository.stopStream()
    }

}
//Bu kod, yayını durdurmak için yazılmış bir “use case” sınıfıdır
//Kodun amacı, yayını durdurma işlemini tek bir satırla çağırmayı sağlayacak bir aracı katman oluştur