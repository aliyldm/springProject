# GPU Bilgi Sistemi

Bu proje, NVIDIA GPU'ları hakkında detaylı bilgi sunan, kullanıcıların yorum yapabildiği ve beğeni paylaşabildiği bir web uygulamasıdır.

## Özellikler

- Kullanıcı kaydı ve girişi
- GPU listesi görüntüleme
- GPU detay sayfaları
- GPU'lara yorum yapma ve beğenme
- Farklı kullanıcı rolleri (Süper Admin, Veri Yöneticisi, Normal Kullanıcı)
- Güvenli kimlik doğrulama ve yetkilendirme

## Teknolojiler

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL
- Thymeleaf
- HTML/CSS
- Maven

## Kurulum

### Gereksinimler

- Java 17 veya üzeri
- MySQL 8.0
- Maven

### Veritabanı Kurulumu

1. MySQL'de yeni bir veritabanı oluşturun:
```sql
CREATE DATABASE buttondemo;
```

### Uygulama Kurulumu

1. Projeyi klonlayın:
```bash
git clone https://github.com/aliyldm/springProject.git
cd springProject
```

2. `src/main/resources/application.properties` dosyasını oluşturun ve aşağıdaki yapılandırmayı ekleyin:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/buttondemo?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
```

3. Projeyi derleyin ve çalıştırın:
```bash
mvn clean install
mvn spring-boot:run
```

4. Tarayıcınızda http://localhost:8080 adresine gidin

## Kullanıcı Rolleri

1. **Süper Admin**
   - Kullanıcı adı: `admin`
   - Şifre: `admin123`
   - Tüm işlemleri yapabilir

2. **Veri Yöneticisi**
   - Kullanıcı adı: `manager`
   - Şifre: `manager123`
   - GPU verilerini yönetebilir

3. **Normal Kullanıcı**
   - Kayıt olarak oluşturulur
   - GPU'ları görüntüleyebilir, yorum yapabilir ve beğenebilir

## API Endpoints

- `GET /api/gpu`: Tüm GPU'ları listeler
- `GET /api/gpu/{id}`: Belirli bir GPU'nun detaylarını gösterir
- `POST /api/gpu/{id}/comment`: GPU'ya yorum ekler
- `POST /api/gpu/{id}/like`: GPU'yu beğenir/beğeniyi kaldırır
- `POST /register`: Yeni kullanıcı kaydı
- `POST /login`: Kullanıcı girişi

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakın. 