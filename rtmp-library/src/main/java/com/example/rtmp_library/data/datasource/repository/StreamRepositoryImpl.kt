package com.example.rtmplibrary.data.repository

import com.example.rtmplibrary.data.datasource.StreamDataSource
import com.example.rtmplibrary.domain.model.StreamState
import com.example.rtmplibrary.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow


class StreamRepositoryImpl(
    private val dataSource: StreamDataSource
) : StreamRepository {

    override fun startStream(url: String) {
        dataSource.startStream(url)
    }

    override fun stopStream() {
        dataSource.stopStream()
    }

    override fun observeStreamState(): Flow<StreamState> {
        return dataSource.observeState()
    }
}
//Bu kod, StreamRepository interface’inde tanımlanan yayın başlatma, durdurma ve
// yayın durumunu izleme fonksiyonlarını gerçek olarak çalıştıran
// (implement eden) sınıftır ve ViewModel ile DataSource arasında aracılık yapar.