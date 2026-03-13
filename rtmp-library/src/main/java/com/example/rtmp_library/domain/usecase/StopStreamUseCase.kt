package com.example.rtmplibrary.domain.usecase

import com.example.rtmplibrary.domain.repository.StreamRepository
import javax.inject.Inject

class StopStreamUseCase @Inject constructor(
    private val repository: StreamRepository
) {

    operator fun invoke(): Result<Unit> {
        return repository.stopStream()
    }

}
//Bu kod, yayını durdurmak için yazılmış bir “use case” sınıfıdır
//Kodun amacı, yayını durdurma işlemini tek bir satırla çağırmayı sağlayacak bir aracı katman oluştur