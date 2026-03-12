package com.example.rtmplibrary.domain.usecase

import com.example.rtmplibrary.domain.repository.StreamRepository
import javax.inject.Inject

class ObserveStreamStateUseCase @Inject constructor(
    private val repository: StreamRepository
) {

    operator fun invoke() = repository.observeStreamState()

}
//Bu sınıf, yayının durumunu almak için bir aracı  sağlar,
// kendisi yayını başlatmaz veya durdurmaz, sadece durumu sorar ve geri döndürür.