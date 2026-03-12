package com.example.rtmplibrary.domain.usecase

import com.example.rtmplibrary.domain.repository.StreamRepository
import javax.inject.Inject

class StartStreamUseCase @Inject constructor(
    private val repository: StreamRepository
) {

    operator fun invoke(url: String) {
        repository.startStream(url)
    }

}
//Bu kod, yayını başlatmak için yazılmış bir “use case” sınıfıdır
//kodun amacı, yayını başlatma işlemini tek
// bir satırla çağırmayı sağlayacak bir aracı katman oluşturmaktır