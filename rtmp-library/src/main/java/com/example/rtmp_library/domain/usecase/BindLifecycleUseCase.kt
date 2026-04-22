package com.example.rtmplibrary.domain.usecase

import androidx.lifecycle.Lifecycle
import com.example.rtmplibrary.domain.repository.StreamRepository
import javax.inject.Inject

class BindLifecycleUseCase @Inject constructor(
    private val repository: StreamRepository
) {

    suspend operator fun invoke(lifecycle: Lifecycle): Result<Unit> {
        return repository.bindLifecycle(lifecycle)
    }

}
//Bu kod, View(Activity/Fragment) yaşam döngüsünü dinlemek,
//arkaplana atıldığında yayın çökmesini/memory leak olmasını önlemek içindir.