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
    implementation("com.github.ayseelg:rtmp-library:1.0.0")
}
```

### Projeyi Direkt Çalıştırmak

Eğer kütüphane olarak değil de var olan örnek uygulamayı test etmek isterseniz:
1. Projeyi clone'layın
2. Android Studio'da açın
3. Sync yapıp cihazınızda çalıştırın
4. Ekranda istenilen RTMP URL ve stream key'ini girip, yayını başlatın

## İzinler

Kamera ve mikrofon izni isteniyor, runtime'da handle ediliyor.

---

## 📝 Sürüm Notları (Release Notes)

Her yeni güncellemeyle kütüphaneye eklenen özellikler ve değişiklikler burada listelenmektedir.

### v1.0.0
**Tarih:** 22 Nisan 2026

**Yayınlanan Özellikler (İlk Yayın):**
- Kameradan görüntü alıp belirtilen RTMP sunucusuna yayın yapma eklendi.
-  Ön/arka kamera geçişi eklendi (`Switch Camera`).
-  Yayın durumunun takibi (Bağlanıyor, Yayında, Durdu, Hata) eklendi.
-  Clean Architecture prensipleriyle katmanlı mimari oluşturuldu.
-  Hilt (Dependency Injection) altyapısı sağlandı.
-  Lifecycle Aware (Yaşam döngüsüne duyarlı) kamera kontrolü yapıldı (Memory Leak'ler önlendi).
-  Kütüphanenin `GitHub Packages` kullanılarak diğer projelerde dış bağımlılık olarak kullanılması entegre edildi.
