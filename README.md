# Logisight

**Logisight**, kullanıcı hareketlerini izleyen, anomali tespiti gerçekleştiren ve veriye dayalı raporlar sunan gelişmiş bir veri analiz ve izleme platformudur. Proje hâlen aktif geliştirme aşamasında olup; backend Java Spring Boot ve PostgreSQL ile, frontend ise React ve JavaScript ile tasarlanmaktadır.

---

## 🚀 Özellikler

- **Kullanıcı Hareket Takibi**: Detaylı izleme ve analiz
- **Anomali Tespiti**: Yapay zeka destekli anomalileri belirleme (yakında)
- **PDF Raporlama**: Zamanlanmış ve manuel rapor üretimi (iText)
- **Veri Yönetimi**: CRUD operasyonları ve RESTful API mimarisi
- **Gerçek Zamanlı Veri Akışı**: WebSocket entegrasyonu
- **Performans & Cache**: Akıllı önbellek kontrolü ve optimizasyon
- **Güvenlik**: JWT tabanlı kimlik doğrulama, rol tabanlı erişim kontrolü
- **HTTPS Desteği**: Güvenli veri iletimi

---

## 🛠 Teknoloji Yığını

- **Backend**: Java, Spring Boot, Spring Security
- **AI**: Spring AI (yakında)
- **Veritabanı**: PostgreSQL (DBeaver ile yönetim)
- **Frontend**: React, JavaScript (geliştirme aşamasında)
- **Raporlama**: iText ile PDF üretimi
- **Test**: JUnit5, Mock testler
- **Kod Prensipleri**: Clean code, RESTful API, katmanlı mimari

---

## 📁 Proje Yapısı

```
Logisight/
├── aspect/        # Cross-cutting concerns (AOP, logging, vs)
├── config/        # Spring konfigürasyonları
├── controller/    # RESTful API controller'ları
├── dto/           # Data Transfer Object'ler
├── entity/        # Veritabanı tabloları
├── init/          # Başlangıç/seed işlemleri
├── jexception/    # Custom exception & error code'lar
├── mapper/        # MapStruct mapping
├── report/        # PDF & raporlama fonksiyonları
├── repository/    # JpaRepository arabirimleri
├── scheduler/     # Zamanlanmış görevler
├── security/      # JWT, rol, güvenlik konfigürasyonu
├── service/       # İş mantığı (abstract & concrete)
└── util/          # Yardımcı sınıflar ve fonksiyonlar
```

---

## ⚙️ Kurulum ve Başlatma

1. PostgreSQL’i kurup gerekli veritabanını oluşturun.
2. `application.properties` dosyasındaki veritabanı ayarlarını güncelleyin.
3. Maven ile projeyi derleyip çalıştırın:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. Frontend geliştirme tamamlandığında React uygulaması ile entegre edilecektir.

---

## 📌 Proje Durumu

- **Backend**: CRUD ve raporlama işlevleri aktif, geliştirme devam ediyor.
- **Frontend**: React arayüzü geliştirme aşamasında.
- **Yapay Zeka**: Anomali tespiti planlanıyor.
- **Flyway**: Veritabanı migration yönetimi kurulum aşamasında.

---


**Logisight ile verinizi daha akıllıca analiz edin, anomaliyi hızla tespit edin!**
