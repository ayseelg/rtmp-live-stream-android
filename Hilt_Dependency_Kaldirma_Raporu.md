# RTMP Kütüphanesi - Mimari Refaktör Raporu (Hilt Bağımlılığının Kaldırılması)

## 1. Problemin Tanımı ve Murat Bey'in Geri Bildirimi
Önceki mimaride, `rtmp-library` modülümüz Dependency Injection (Bağımlılık Enjeksiyonu) için doğrudan **Hilt** kullanıyordu. Kütüphane içerisindeki `StreamViewModel`, `StreamDataSource`, `StreamRepositoryImpl` ve tüm UseCase'ler `@Inject`, `@HiltViewModel`, `@Singleton` gibi Hilt/Dagger etiketleriyle donatılmıştı.

**Bu durumun yarattığı mimari sorun:**
Bir 3. parti SDK veya kütüphane (library), kendisini tüketecek olan ana uygulamalara (app) belli bir mimariyi (örneğin Hilt kullanmayı) **dayatmamalıdır**. Eğer kütüphanemiz Hilt kullanımını zorunlu kılarsa:
* Projesinde **Koin** kullanan bir geliştirici kütüphanemizi kullanamaz.
* Projesinde eski **Dagger 2** kullanan bir geliştirici entegrasyonda çakışmalar yaşar.
* Projesinde hiçbir DI kütüphanesi kullanmayan (yeni veya manuel DI kullanan) geliştiriciler kütüphanemizi projesine ekleyemez.

Murat Bey'in haklı uyarısı doğrultusunda, kütüphanenin "kendi kendine yetebilen (self-contained)" ve dışarıdan sadece temel Android gereksinimlerini (Context, Lifecycle vb.) alan saf bir yapıya dönüştürülmesi gerekiyordu.

---

## 2. Yapılan Değişiklikler ve Adımlar

### Adım 1: `build.gradle.kts` Dosyasından Hilt'in Çıkarılması
`rtmp-library` modülünün gradle dosyasında bulunan tüm Hilt bağımlılıkları temizlendi. Böylece kütüphane dış dünyaya "ben Hilt kurulumu gerektiriyorum" mesajı vermekten kurtarıldı.
* **Silinenler:** `alias(libs.plugins.ksp)`, `api(libs.hilt.android)`, `ksp(libs.hilt.compiler)`

### Adım 2: Kotlin Sınıflarındaki Hilt Etiketlerinin Temizlenmesi (Vanilla Kotlin'e Geçiş)
Kütüphane içindeki tüm sınıflar saf Kotlin sınıflarına dönüştürüldü.
* `StreamViewModel`: `@HiltViewModel` ve `@Inject constructor` etiketleri kaldırıldı.
* `StreamDataSource`: `@Singleton` ve `@Inject constructor` etiketleri kaldırıldı.
* `StreamRepositoryImpl` ve tüm **UseCase** sınıflarından: `@Inject constructor` silinerek standart yapıcı (constructor) metodlara çevrildi.

### Adım 3: Kütüphaneye Özel `StreamViewModelFactory` Yazılması
Hilt'in bizim yerimize yaptığı "ilgili sınıfları sırasıyla birbirine bağlama (Dependency Graph)" işini, kütüphanenin içinde güvenli bir şekilde gizleyerek (encapsulation) kendimiz modelledik.

Dış dünyaya sadece `StreamViewModelFactory` sınıfını açtık. Bu fabrika sınıfı şu mantıkla çalışıyor:
1. `StreamDataSource`'u oluştur.
2. Bu DataSource'u vererek `StreamRepositoryImpl`'i oluştur.
3. Bu Repository'i vererek UseCase'leri (StartStream, InitCamera vb.) oluştur.
4. Tüm bunları bir araya getirerek `StreamViewModel`'i yarat ve dışarı ver.

```kotlin
// Oluşturulan Factory sınıfının çekirdek yapısı
val dataSource = StreamDataSource()
val repository = StreamRepositoryImpl(dataSource)

return StreamViewModel(
    observeStreamStateUseCase = ObserveStreamStateUseCase(repository),
    startStreamUseCase = StartStreamUseCase(repository),
    // ... diğer UseCase'ler
)
```

### Adım 4: Örnek Projenin (App Modülü) Yeni Sisteme Adaptasyonu
Kütüphaneyi kullanan ana örnek projede (`StreamFragment`), ViewModel çağrımı Hilt reflection'ından çıkarılarak kütüphanenin sağladığı Factory mimarisine geçirildi.

**Eski Hilt Kullanımı:**
```kotlin
private val viewModel: StreamViewModel by viewModels() 
```

**Yeni Saf ve Bağımsız Kullanım:**
```kotlin
private val viewModel: StreamViewModel by viewModels { StreamViewModelFactory() }
```

---

## 3. Sonuç ve Avantajlar
* **Agnostik Kütüphane:** `rtmp-library`, tamamen DI-agnostic (herhangi bir DI aracından bağımsız) hale getirilmiştir.
* **Tak-Çalıştır (Plug & Play):** Bu SDK'yı projesine ekleyen bir geliştiricinin, projesine ekstra bir kurulum yapmasına, projesini Hilt annotations (örn: `@HiltAndroidApp`) ile kirletmesine gerek kalmamıştır. Sadece factory sınıfını çağırarak saniyeler içinde yayına başlayabilir.
* **Profesyonel Standartlar:** "Single Responsibility" ve kütüphanelerin kendi iç dünyalarını dışarıdan saklaması ilkesi yüzde yüz sağlanmıştır.
