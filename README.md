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

## Kurulum ve Kullanım

### Kütüphaneyi Projenize Eklemek (GitHub Packages)

Bu projedeki `rtmp-library` modülünü kendi uygulamanızda dışarıdan bir kütüphane olarak kullanabilirsiniz.

**1. Proje seviyesi `settings.gradle.kts` dosyasına depoyu ekleyin:**
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/ayseelg/rtmp-live-stream-android")
            credentials {
                // Okuma iznine (read:packages) sahip bir GitHub Token girilmelidir.
                username = "GITHUB_KULLANICI_ADINIZ"
                password = "GITHUB_TOKEN"
            }
        }
    }
}
```

**2. Uygulama seviyesi `build.gradle.kts` dosyasına bağımlılığı ekleyin:**
```kotlin
dependencies {
    implementation("com.github.ayseelg:rtmp-library:1.0.1")
}
```

### Kütüphanenin Kullanımı (Activity / Fragment İçerisinde)

Kütüphane **Hilt** kullanmaktadır. Bu yüzden uygulamanızın varsayılan olarak bir `Application` sınıfında `@HiltAndroidApp` bulunmalı ve Activity/Fragment üzerinde `@AndroidEntryPoint` eklenmeli.

**1. Arayüzünüz (XML) İçin Kamera Ekranı Ekleme:**
Kamera önizlemesi için kütüphanenin kullandığı `OpenGlView` ekran bileşenini layout.xml'e ekleyin:
```xml
<com.pedro.library.view.OpenGlView
    android:id="@+id/openGlView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

**2. Fragment / Activity İçinde Kurulumu (Init):**
Aşağıda temel bir `Fragment` (veya `Activity`) üzerinde yayın başlatma, durdurma ve yayın durumunu dinleme örneğini bulabilirsiniz.

```kotlin
@AndroidEntryPoint
class LiveStreamFragment : Fragment() {

    // 1. Kütüphaneden gelen ViewModel'i Inject ediyoruz
    private val viewModel: StreamViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val openGlView = view.findViewById<OpenGlView>(R.id.openGlView)

        // 2. Lifecycle Desteği: Uygulama arkaplana geçtiğinde memory leak veya çökme yaşanmaması için yaşam döngüsünü bağlayın.
        viewModel.bindLifecycle(viewLifecycleOwner.lifecycle)

        // 3. Kamera Başlatma: Layout'taki görünüm modülünü SDK'ya verin.
        viewModel.initCamera(openGlView)
        
        // 4. Önizleme (Preview): Kamera görünümü Android sisteminde hazır olduğunda tetiklenir.
        openGlView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                // Not: Kamerayı başlatmadan önce Camera & Audio izinlerini kontrol edin!
                viewModel.startPreview()
            }
            override fun surfaceChanged(h: SurfaceHolder, f: Int, w: Int, height: Int) {}
            override fun surfaceDestroyed(h: SurfaceHolder) {}
        })

        // 5. Yayını Başlat & Durdur
        btnStart.setOnClickListener {
            val rtmpUrl = "rtmp://sunucu-adresi/live"
            val streamKey = "my_stream_key"
            viewModel.startStream(rtmpUrl, streamKey)
        }
        
        btnStop.setOnClickListener {
            viewModel.stopStream()
        }

        // 6. Yayın Durumunu (State) Dinleme: Bağlanıyor, Yayında, Hata vs.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.streamState.collectLatest { state ->
                    when (state) {
                        is StreamState.Streaming -> { /* Canlı Yayındayız */ }
                        is StreamState.Connecting -> { /* Sunucuya Bağlanıyor... */ }
                        is StreamState.Stopped -> { /* Yayın Durduruldu */ }
                        is StreamState.Error -> { 
                             val hataMesaji = state.message 
                             /* Toast veya Log ile hatayı gösterin */
                        }
                        else -> { /* Beklemede (Idle) */ }
                    }
                }
            }
        }
    }
}
```
**Not:** Yayını başlatmadan önce Kamera (`Manifest.permission.CAMERA`) ve Mikrofon (`Manifest.permission.RECORD_AUDIO`) izinlerini kullanıcıdan runtime'da almış olmanız gerekmektedir. Yüksek stabilite için kütüphane kendi içinde Lifecycle Aware olarak çalışır, arkaplan görevlerini dert etmenize gerek kalmaz.

### Projeyi Direkt Çalıştırmak

Eğer kütüphane olarak değil de var olan örnek uygulamayı test etmek isterseniz:
1. Projeyi clone'layın
2. Android Studio'da açın
3. Sync yapıp cihazınızda çalıştırın
4. Ekranda istenilen RTMP URL ve stream key'ini girip, yayını başlatın

## İzinler

Kamera ve mikrofon izni isteniyor, runtime'da handle ediliyor.

---

## Sürüm Notları (Release Notes)

Her yeni güncellemeyle kütüphaneye eklenen özellikler ve değişiklikler burada listelenmektedir.

### v1.0.1 (Güncel Sürüm)
**Yayınlanan Özellikler ve İyileştirmeler:**
- Kameradan görüntü alıp belirtilen RTMP sunucusuna yayın yapma eklendi.
- Ön/arka kamera geçişi eklendi (`Switch Camera`).
- Yayın durumunun takibi (Bağlanıyor, Yayında, Durdu, Hata) eklendi.
- Clean Architecture prensipleriyle katmanlı mimari oluşturuldu.
- Hilt (Dependency Injection) altyapısı sağlandı.
- Lifecycle Aware (Yaşam döngüsüne duyarlı) kamera kontrolü yapıldı (Memory Leak'ler önlendi).
- Kütüphanenin `GitHub Packages` kullanılarak diğer projelerde dış bağımlılık olarak kullanılması entegre edildi.
- Kurulum aşamasına Fragment/Activity içerisindeki Init/Kurulum kod parçaları detaylıca eklendi.
- **Dependency Fix:** SDK içerisindeki `OpenGlView` ve `Hilt` sınıflarının dış projelerde görülmesi için Gradle `api` bağımlılık güncellemesi yapıldı.
