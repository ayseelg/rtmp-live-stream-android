package com.example.rtmplibrary.domain.usecase

import com.example.rtmplibrary.domain.repository.StreamRepository

class ObserveStreamStateUseCase(
    private val repository: StreamRepository
) {

    operator fun invoke() = repository.observeStreamState()

}
//Bu sınıf, yayının durumunu almak için bir aracı  sağlar,
// kendisi yayını başlatmaz veya durdurmaz, sadece durumu sorar ve geri döndürür.