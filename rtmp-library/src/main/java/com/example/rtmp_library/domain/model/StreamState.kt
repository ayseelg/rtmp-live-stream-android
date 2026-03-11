package com.example.rtmplibrary.domain.model

sealed class StreamState {

    object Idle : StreamState()

    object Connecting : StreamState()

    object Streaming : StreamState()

    object Stopped : StreamState()

    data class Error(val message: String) : StreamState()
}
//Bu kod, yayının hangi durumda olduğunu
// (Beklemede ,bağlanıyor,yayın yapıyor,yayın durduruldu,hata oluştu)
// temsil eden durum türlerini tanımlayan bir modeldir.

