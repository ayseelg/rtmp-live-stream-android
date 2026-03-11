package com.example.rtmplibrary.domain.repository

import kotlinx.coroutines.flow.Flow
import com.example.rtmplibrary.domain.model.StreamState

interface StreamRepository {

    fun startStream(url: String)

    fun stopStream()

    fun observeStreamState(): Flow<StreamState>

}
//Bu kod, yayını başlatma, durdurma ve yayın durumunu izleme
// işlemlerinin hangi fonksiyonlarla yapılacağını tanımlayan bir interface’tir.