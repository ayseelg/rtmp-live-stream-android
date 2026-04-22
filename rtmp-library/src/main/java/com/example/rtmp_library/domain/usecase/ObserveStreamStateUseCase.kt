package com.example.rtmplibrary.domain.usecase

import com.example.rtmplibrary.domain.repository.StreamRepository
import javax.inject.Inject

class ObserveStreamStateUseCase @Inject constructor(
    private val repository: StreamRepository
) {

    suspend operator fun invoke(
        onConnectionStarted: () -> Unit,
        onConnectionSuccess: () -> Unit,
        onConnectionFailed: (String) -> Unit,
        onDisconnected: () -> Unit,
        onAuthError: () -> Unit,
        onPreparationFailed: (String) -> Unit
    ): Result<Unit> {
        return repository.setStreamCallbacks(
            onConnectionStarted = onConnectionStarted,
            onConnectionSuccess = onConnectionSuccess,
            onConnectionFailed = onConnectionFailed,
            onDisconnected = onDisconnected,
            onAuthError = onAuthError,
            onPreparationFailed = onPreparationFailed
        )
    }

}
//Bu sınıf, yayının durumunu almak için bir aracı  sağlar,
// kendisi yayını başlatmaz veya durdurmaz, sadece durumu sorar ve geri döndürür.