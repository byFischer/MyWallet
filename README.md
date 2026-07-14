# MyWallet

Aylık aboneliklerin ve taksitli ödemelerin toplam gider tutarını otomatik hesaplayan, Android için geliştirilmiş kişisel finans uygulaması.

## Özellikler

- Abonelik ekleme, düzenleme ve silme (aylık / yıllık / haftalık ödeme periyodu desteği)
- Taksit ekleme, düzenleme ve silme (kalan taksit sayısı ve bitiş tarihi otomatik hesaplanır)
- Tüm giderlerin aylık toplamının anlık ve otomatik hesaplanması
- Abonelik ve taksitler için ayrı sekmeli görünüm

## Teknoloji

- **Dil:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Mimari:** MVVM (ViewModel + Repository)
- **Veritabanı:** Room (local-first, internet bağlantısı gerektirmez)
- **Navigasyon:** Navigation Compose
- **Asenkron işlemler:** Kotlin Coroutines & Flow

## Kurulum

1. Projeyi klonla
2. Android Studio ile aç
3. Gradle sync'in tamamlanmasını bekle
4. Bir cihaz/emulator seçip çalıştır

**Minimum SDK:** 26 (Android 8.0)

## Brandfetch yapılandırması

Abonelik hizmeti araması ve marka logoları için ücretsiz bir Brandfetch Client ID gerekir.

1. Brandfetch Developer Portal üzerinden bir Client ID oluştur.
2. Projenin takip edilmeyen `local.properties` dosyasına aşağıdaki satırı ekle:

```properties
BRANDFETCH_CLIENT_ID=client_id_degerin
```

Client ID tanımlı değilse uygulama çalışmaya devam eder; abonelik adı elle girilebilir ve kartta nötr bir yer tutucu gösterilir.

## Durum

Aktif geliştirme aşamasında.
