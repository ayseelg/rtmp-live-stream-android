# rtmp-live-stream-android

Android üzerinde RTMP protokolü kullanarak canlı yayın yapabilen bir uygulama. Kamera görüntüsünü alıp bir RTMP sunucusuna (YouTube, Twitch vb.) stream ediyor.

## Ne yapıyor?

- Kameradan canlı görüntü alıp RTMP URL'e yayınlıyor
- Ön/arka kamera geçişi var
- Yayın durumunu (Bağlanıyor, Yayında, Durdu vs.) takip ediyor
- URL ve stream key girişi fragment üzerinden yapılıyor

## Proje yapısı

Projeyi iki modüle ayırdım:

**`app`** → Kullanıcı arayüzü. `StreamFragment` burada, kamerayı açıp RTMP bağlantısını yönetiyor.

**`rtmp-library`** → Stream işlemlerinin iş mantığı. Clean architecture uyguladım:
- `data/` → `StreamDataSource`, stream state'ini tutuyor
- `domain/` → model, repository interface ve use case'ler
- `presentation/` → `StreamViewModel`

## Kullanılan teknolojiler

- Kotlin
- RTMP: [rtmp-rtsp-stream-client-java](https://github.com/pedroSG94/rtmp-rtsp-stream-client-java) (Pedro kütüphanesi)
- Coroutines + StateFlow
- ViewModel
- minSdk: 29

## Kurulum

1. Projeyi clone'la
2. Android Studio'da aç
3. Sync yap, çalıştır
4. RTMP URL ve stream key'ini gir, yayını başlat

## İzinler

Uygulama kamera ve mikrofon izni istiyor, runtime'da handle ediliyor.
