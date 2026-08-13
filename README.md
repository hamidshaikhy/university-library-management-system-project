# سامانه مدیریت کتابخانه دانشگاه

پروژهٔ درس **مهندسی اینترنت** با بک‌اند Java/Spring Boot، فرانت‌اند React و دیتابیس MySQL.

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen?logo=springboot)
![React](https://img.shields.io/badge/React-18-149eca?logo=react)
![Vite](https://img.shields.io/badge/Vite-5-646cff?logo=vite)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue?logo=mysql)

## اعضای گروه

- امیر حسین بیات
- محمدرضا صادقی
- مهدی اکبری

## پروژه چه کاری انجام می‌دهد؟

کاربران می‌توانند ثبت‌نام و وارد سامانه شوند، کتاب‌ها را جست‌وجو کنند، درخواست رزرو بدهند و وضعیت رزروها و امانت‌های خود را ببینند. مدیر سامانه می‌تواند کتاب‌ها، نویسندگان و دسته‌بندی‌ها را با عملیات CRUD مدیریت کند، رزروها را تأیید یا رد کند، امانت جدید بسازد و بازگشت کتاب را ثبت کند.

## تطبیق با شرایط استاد

| شرط پروژه | پیاده‌سازی |
|---|---|
| فرانت‌اند | React 18، React Router، Bootstrap و Vite |
| بک‌اند Java | Java 17 و Spring Boot |
| دیتابیس | MySQL با Spring Data JPA و Hibernate |
| حداقل ۵ موجودیت + User | `Book`، `Author`، `Category`، `Reservation`، `Borrow` + `User` |
| Authentication | ورود/ثبت‌نام، BCrypt و Session/Cookie |
| حداقل یک CRUD | CRUD کامل کتاب، نویسنده و دسته‌بندی |

## روش اتصال React به Java

این پروژه از رایج‌ترین روش برای اتصال React به Spring Boot استفاده می‌کند: **REST API + JSON**.

```text
React Component
      ↓ Axios HTTP Request
Spring Boot REST Controller (/api/...)
      ↓
Service (قوانین برنامه)
      ↓
Repository (Spring Data JPA)
      ↓
MySQL
      ↑
JSON Response → React State → UI
```

مثلاً React برای گرفتن کتاب‌ها درخواست زیر را می‌فرستد:

```http
GET /api/books?q=تمیز&categoryId=1
```

و Spring Boot داده را به شکل JSON برمی‌گرداند:

```json
{
  "books": [
    {
      "id": 1,
      "title": "کدنویسی تمیز",
      "isbn": "9780132350884",
      "quantity": 4,
      "author": { "id": 1, "fullName": "رابرت سی. مارتین" },
      "category": { "id": 1, "title": "برنامه‌نویسی" }
    }
  ],
  "categories": []
}
```

### چرا Axios؟

- برای درخواست‌های `GET`، `POST`، `PUT` و `DELETE` ساده و رایج است.
- پاسخ JSON را مستقیم در `response.data` قرار می‌دهد.
- ارسال Cookie مربوط به Session با `withCredentials: true` مشخص است.
- خطاهای HTTP در یک محل مدیریت می‌شوند.

فایل اتصال فرانت به بک‌اند: `frontend/src/lib/api.js`

### Authentication چگونه کار می‌کند؟

1. React ایمیل و رمز را با `POST /api/auth/login` می‌فرستد.
2. Spring Boot رمز را با BCrypt بررسی می‌کند.
3. در صورت صحیح بودن، شناسه و نقش کاربر در `HttpSession` ذخیره می‌شود.
4. مرورگر Cookie استاندارد `JSESSIONID` را نگه می‌دارد.
5. Axios این Cookie را همراه درخواست‌های بعدی می‌فرستد.
6. `AuthInterceptor` برای APIهای عادی ورود کاربر و برای `/api/admin/**` نقش `ADMIN` را کنترل می‌کند.

برای این پروژهٔ دانشگاهی Session از JWT ساده‌تر است و به سرور اجازه می‌دهد وضعیت ورود را مستقیم مدیریت کند.

## تکنولوژی‌ها

### Backend

- Java 17
- Spring Boot 3.3
- Spring MVC REST
- Spring Data JPA / Hibernate
- Jakarta Validation
- BCrypt
- Maven

### Frontend

- React 18
- React Router
- Axios
- Vite 5
- Bootstrap 5 RTL
- Bootstrap Icons

### Database

- MySQL
- H2 فقط برای تست خودکار

## موجودیت‌های دیتابیس

| موجودیت | کاربرد |
|---|---|
| `User` | کاربر، ایمیل، رمز هش‌شده و نقش |
| `Book` | اطلاعات و موجودی کتاب |
| `Author` | نویسندهٔ کتاب |
| `Category` | دسته‌بندی کتاب |
| `Reservation` | درخواست رزرو و وضعیت آن |
| `Borrow` | تاریخ امانت، سررسید و بازگشت |

روابط اصلی:

- هر `Book` به یک `Author` و یک `Category` متصل است.
- هر `Reservation` به یک `User` و یک `Book` متصل است.
- هر `Borrow` به یک `User` و یک `Book` متصل است.

## قابلیت‌های کاربر

- ثبت‌نام، ورود و خروج
- مشاهدهٔ پروفایل
- فهرست، جست‌وجو و فیلتر کتاب‌ها
- مشاهدهٔ جزئیات کتاب
- ثبت و لغو رزرو
- مشاهدهٔ وضعیت رزروها
- مشاهدهٔ امانت‌ها و تاریخ سررسید

## قابلیت‌های مدیر

- داشبورد آماری
- CRUD کتاب‌ها
- CRUD نویسندگان
- CRUD دسته‌بندی‌ها
- مشاهدهٔ کاربران
- تأیید یا رد رزرو
- ساخت خودکار امانت ۱۴روزه پس از تأیید رزرو
- ثبت دستی امانت
- ثبت بازگشت و افزایش موجودی کتاب

## REST APIهای مهم

| Method | Address | کاربرد |
|---|---|---|
| `POST` | `/api/auth/register` | ثبت‌نام |
| `POST` | `/api/auth/login` | ورود و ایجاد Session |
| `GET` | `/api/auth/me` | بررسی کاربر واردشده |
| `POST` | `/api/auth/logout` | خروج |
| `GET` | `/api/books` | فهرست و جست‌وجوی کتاب‌ها |
| `GET` | `/api/books/{id}` | جزئیات کتاب |
| `POST` | `/api/books/{id}/reservations` | ثبت رزرو |
| `GET` | `/api/me/reservations` | رزروهای کاربر |
| `GET` | `/api/me/borrows` | امانت‌های کاربر |
| `GET/POST/PUT/DELETE` | `/api/admin/books` | CRUD کتاب |
| `GET/POST/PUT/DELETE` | `/api/admin/authors` | CRUD نویسنده |
| `GET/POST/PUT/DELETE` | `/api/admin/categories` | CRUD دسته‌بندی |
| `POST` | `/api/admin/reservations/{id}/approve` | تأیید رزرو |
| `POST` | `/api/admin/borrows/{id}/return` | ثبت بازگشت |

## اجرای پروژه در حالت توسعه

### پیش‌نیازها

- Java 17
- Maven 3.9 یا جدیدتر
- Node.js 18 یا جدیدتر
- MySQL 8
- Git

### ۱. دریافت پروژه

```bash
git clone https://github.com/hamidshaikhy/university-library-management-system.git
cd university-library-management-system
```

### ۲. ساخت دیتابیس

فایل `database.sql` را اجرا کنید یا دستور زیر را در MySQL بزنید:

```sql
CREATE DATABASE university_library
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

### ۳. تنظیم نام کاربری و رمز MySQL

Windows PowerShell:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
```

Linux یا macOS:

```bash
export DB_USERNAME=root
export DB_PASSWORD=YOUR_MYSQL_PASSWORD
```

رمز دیتابیس داخل GitHub ذخیره نمی‌شود و از Environment Variable خوانده می‌شود.

### ۴. اجرای بک‌اند

در ترمینال اول:

```bash
mvn spring-boot:run
```

بک‌اند روی `http://localhost:8080` اجرا می‌شود.

### ۵. اجرای React

در ترمینال دوم:

```bash
cd frontend
npm install
npm run dev
```

سایت را در آدرس زیر باز کنید:

```text
http://localhost:5173
```

Vite تمام درخواست‌های `/api` را با Proxy به `http://localhost:8080` می‌فرستد؛ بنابراین در حالت توسعه تنظیم CORS لازم نیست.

## اجرای نسخهٔ یکپارچه روی پورت 8080

ابتدا React را build کنید و سپس Spring Boot را package کنید:

```bash
cd frontend
npm install
npm run build
cd ..
mvn clean package
java -jar target/university-library-1.0.0.jar
```

Maven پوشهٔ `frontend/dist` را داخل فایل JAR قرار می‌دهد. در این حالت هم React و هم API از آدرس `http://localhost:8080` سرو می‌شوند.

## حساب‌های آزمایشی

| نقش | ایمیل | رمز |
|---|---|---|
| مدیر | `admin@library.local` | `Admin123` |
| کاربر | `user@library.local` | `User123` |

این حساب‌ها فقط برای تست محلی و ارائه ساخته می‌شوند.

## تست

تست بک‌اند با دیتابیس موقت H2:

```bash
mvn test
```

تست build فرانت:

```bash
cd frontend
npm run build
```

تست‌های API ورود، دسترسی عادی و دسترسی مدیر در `ApiIntegrationTests.java` قرار دارند.

## ساختار پروژه

```text
university-library-management-system/
├── frontend/
│   ├── src/
│   │   ├── components/       # Layout و اجزای مشترک
│   │   ├── context/          # وضعیت ورود کاربر
│   │   ├── lib/              # Axios و مدیریت خطا
│   │   ├── pages/            # صفحات کاربر و مدیر
│   │   ├── styles/           # ظاهر اصلی سایت
│   │   ├── App.jsx           # Routeهای React
│   │   └── main.jsx          # نقطه شروع React
│   ├── package.json
│   └── vite.config.js
├── src/main/java/com/example/library/
│   ├── config/               # Session Interceptor و داده نمونه
│   ├── controller/api/       # REST Controller، DTO و Error Handler
│   ├── entity/               # شش موجودیت دیتابیس
│   ├── repository/           # Spring Data JPA
│   └── service/              # منطق اصلی برنامه
├── src/main/resources/
│   └── application.properties
├── database.sql
├── pom.xml
└── .gitignore
```

## مسیر پیشنهادی برای توضیح به استاد

1. ابتدا شش Entity و ارتباط‌های آن‌ها را نشان دهید.
2. سپس یک Repository و Service، مثلاً `BookRepository` و `BookService`، را توضیح دهید.
3. در `AdminApiController` متدهای `GET/POST/PUT/DELETE` کتاب را نشان دهید.
4. در `frontend/src/lib/api.js` نمونهٔ Axios را توضیح دهید.
5. در `AdminBooksPage.jsx` نشان دهید پاسخ JSON داخل state ذخیره و رندر می‌شود.
6. ورود را از `AuthApiController` تا `AuthContext.jsx` دنبال کنید.
7. در پایان یک عملیات CRUD و جریان رزرو تا امانت را زنده اجرا کنید.

## جریان رزرو و امانت

```text
کاربر کتاب را رزرو می‌کند
        ↓
Reservation با وضعیت PENDING ساخته می‌شود
        ↓
مدیر رزرو را تأیید می‌کند
        ↓
Borrow با سررسید ۱۴ روزه ساخته می‌شود
        ↓
موجودی کتاب یک واحد کم می‌شود
        ↓
مدیر بازگشت را ثبت می‌کند
        ↓
موجودی کتاب یک واحد زیاد می‌شود
```
