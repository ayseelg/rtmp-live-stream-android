package com.example.rtmplibrary.domain.usecase

import com.example.rtmplibrary.domain.repository.StreamRepository

class StartStreamUseCase (
    private val repository: StreamRepository
) {

    suspend operator fun invoke(url: String): Result<Unit> {
        return repository.startStream(url)
    }

}
//Bu kod, yayını başlatmak için yazılmış bir “use case” sınıfıdır
//kodun amacı, yayını başlatma işlemini tek
// bir satırla çağırmayı sağlayacak bir aracı katman oluşturmaktır
