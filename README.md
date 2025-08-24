# Logisight

**Logisight**, kullanıcı hareketlerini takip eden, anomali tespiti yapan ve veriye dayalı raporlar üreten bir veri analiz ve izleme sistemidir. Proje hâlen geliştirme aşamasındadır; backend Java Spring Boot ve PostgreSQL ile geliştirilmekte, frontend React ve JavaScript ile ilerlemektedir.

---

## 🚀 Özellikler

- [x] Kullanıcı hareketlerini takip ve analiz
- [ ] Yapay zekâ tabanlı anomali tespiti
- [x] Scheduled ve manuel PDF rapor oluşturma (iText)
- [x] CRUD ve RESTful API tabanlı veri yönetimi
- [x] WebSocket ile gerçek zamanlı veri akışı
- [x] Cache kontrolü ve performans optimizasyonu
- [x] JWT tabanlı güvenli kimlik doğrulama ve rol hiyerarşisi
- [x] HTTPS uyumlu ve güvenli veri erişimi

---

## 🛠 Teknoloji Yığını

- [x] Backend: Java, Spring Boot, Spring Security
- [ ] Spring AI (gelecekte)
- [x] Veritabanı: PostgreSQL (DBeaver üzerinden yönetildi)
- [ ] Frontend: React, JavaScript (geliştirme aşamasında)
- [x] Raporlama: iText ile PDF oluşturma, scheduled ve manuel
- [x] Testler: JUnit5 ve Mock testleri
- [x] Kod Yapısı: CRUD, RESTful API, clean code prensipleri

---

## 📦 Paket Yapısı

aspect/ # Aspect tabanlı cross-cutting concerns
config/ # Spring konfigürasyonları
controller/ # RESTful API controller'ları
dto/ # Data Transfer Object
entity/ # Veritabanı tabloları
init/ # Başlangıç ve seed işlemleri
jexception/ # Custom exception ve error code'lar
mapper/ # MapStruct mapping
report/ # PDF ve raporlama işlemleri
repository/ # JpaRepository
scheduler/ # Scheduled görevler
security/ # JWT, rol ve güvenlik konfigürasyonu
service/ # Business logic (abstract & concrete)
util/ # Yardımcı sınıflar ve fonksiyonlar

yaml
Kopyala
Düzenle

---

## ⚙️ Kurulum

1. PostgreSQL’i çalıştırın ve gerekli veritabanını oluşturun
2. `application.properties` dosyasını veritabanı bilgilerinizle güncelleyin
3. Maven ile projeyi derleyin ve çalıştırın:

```bash
mvn clean install
mvn spring-boot:run
Frontend geliştirme tamamlandığında React uygulaması ile entegre edilecektir

🔧 Durum
Backend: CRUD ve raporlama işlevleri aktif, geliştirme devam ediyor

Frontend: React tarafı geliştirme aşamasında

Yapay zekâ tabanlı anomali tespiti: Planlanıyor

Flyway: Kurulum aşamasında
