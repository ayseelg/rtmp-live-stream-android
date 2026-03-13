# rtmp-live-stream-android

Android üzerinde RTMP ile canlı yayın yapan bir uygulama. Kamera görüntüsünü alıp RTMP sunucusuna (YouTube, Twitch vb.) gönderiyor.

## Ne yapıyor?

- Kameradan görüntü alıp RTMP URL'e yayınlıyor
- Ön/arka kamera geçişi var
- Yayın durumu takip ediliyor (Bağlanıyor, Yayında, Durdu, Hata)
- URL ve stream key fragment üzerinden giriliyor

## Proje yapısı

İki modül var:

**`app`** → UI katmanı. `StreamFragment` burada.

**`rtmp-library`** → Asıl iş burda. Clean architecture:
- `data/` → `StreamDataSource` sadece kamera ve network işleri yapıyor, state tutmuyor
- `domain/` → model, repository interface ve use case'ler. Repository'deki her fonksiyon `Result` dönüyor
- `presentation/` → `StreamViewModel`, state'in tek sahibi burada. `MutableStateFlow` burada tutuluyor, DataSource'dan gelen callback'leri state'e çeviriyor

## Kullanılan teknolojiler

- Kotlin
- RTMP: [rtmp-rtsp-stream-client-java](https://github.com/pedroSG94/rtmp-rtsp-stream-client-java) (Pedro kütüphanesi)
- StateFlow, ViewModel
- Hilt
- minSdk: 29

## Kurulum

1. Projeyi clone'la
2. Android Studio'da aç
3. Sync yap, çalıştır
4. RTMP URL ve stream key'ini gir, yayını başlat

## İzinler

Kamera ve mikrofon izni isteniyor, runtime'da handle ediliyor.
