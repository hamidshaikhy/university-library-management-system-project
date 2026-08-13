# 📚 سامانه مدیریت کتابخانه دانشگاه

پروژهٔ درس **مهندسی اینترنت** — مدیریت کتاب‌ها، کاربران، رزرو، امانت و بازگشت، با Spring Boot، React و MySQL.

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen?logo=springboot)
![React](https://img.shields.io/badge/React-18-149eca?logo=react)
![Vite](https://img.shields.io/badge/Vite-5-646cff?logo=vite)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue?logo=mysql)

## اعضای گروه

- امیر حسین بیات
- محمدرضا صادقی
- مهدی اکبری

## ✨ معرفی

کاربران می‌توانند ثبت‌نام و وارد سامانه شوند، کتاب‌ها را جست‌وجو و فیلتر کنند، درخواست رزرو بدهند و وضعیت رزروها و امانت‌های خود را ببینند.

مدیر سامانه می‌تواند کتاب‌ها، نویسندگان و دسته‌بندی‌ها را با CRUD کامل مدیریت کند، رزروها را تأیید یا رد کند، امانت جدید ثبت کند و بازگشت کتاب را ثبت کند.

بک‌اند به‌صورت REST API با Spring Boot نوشته شده و فرانت‌اند به‌صورت جدا با React و Vite ساخته شده است.

## 🧩 ماژول‌ها

| ماژول | کاربرد |
|---|---|
| 🔐 احراز هویت | ثبت‌نام، ورود، خروج، هش رمز با BCrypt و احراز هویت مبتنی بر Session |
| 👤 کاربران | پروفایل، نقش و مدیریت دسترسی کاربر/مدیر |
| 📚 کتاب‌ها | فهرست، جست‌وجو، فیلتر، جزئیات، موجودی و CRUD کامل |
| ✍️ نویسندگان | ایجاد، مشاهده، ویرایش و حذف |
| 🗂️ دسته‌بندی‌ها | ایجاد، مشاهده، ویرایش و حذف |
| 📝 رزرو | ثبت رزرو توسط کاربر و تأیید/رد توسط مدیر |
| 📖 امانت | ساخت خودکار امانت پس از تأیید رزرو و کاهش موجودی |
| ✅ بازگشت | ثبت بازگشت توسط مدیر و افزایش موجودی |
| 🛡️ کنترل دسترسی | Interceptor برای مسیرهای نیازمند ورود و مسیرهای مخصوص مدیر |
| 🖥️ داشبورد مدیر | آمار کاربران، کتاب‌ها، امانت‌های فعال و رزروهای در انتظار |

## 🔄 جریان رزرو و امانت

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

## 🛠️ تکنولوژی‌ها

**Backend:** Java 17، Spring Boot 3.3، Spring MVC REST، Spring Data JPA، Hibernate، Jakarta Validation، BCrypt، Maven

**Frontend:** React 18، React Router، Axios، Vite 5، Bootstrap 5 RTL، Bootstrap Icons

**Database:** MySQL (و H2 فقط برای تست خودکار)

## 🏗️ معماری

```text
React Component
      ↓ Axios (REST + JSON)
Spring Boot Controller  (/api/...)
      ↓
Service
      ↓
Repository (Spring Data JPA)
      ↓
MySQL
```

احراز هویت مبتنی بر Session است: ورود با `POST /api/auth/login` بررسی می‌شود، شناسه و نقش کاربر در `HttpSession` ذخیره می‌شود و مرورگر کوکی `JSESSIONID` را برای درخواست‌های بعدی نگه می‌دارد. فایل اتصال فرانت به بک‌اند: `frontend/src/lib/api.js`.

## 🗃️ موجودیت‌های اصلی

`User`، `Book`، `Author`، `Category`، `Reservation`، `Borrow`

- هر `Book` به یک `Author` و یک `Category` متصل است.
- هر `Reservation` و هر `Borrow` به یک `User` و یک `Book` متصل است.

## 🚀 اجرای پروژه

### پیش‌نیازها

- Java 17
- Maven 3.9+
- Node.js 18+
- MySQL 8
- Git

### ۱. دریافت پروژه

```bash
git clone https://github.com/hamidshaikhy/university-library-management-system-project.git
cd university-library-management-system-project
```

### ۲. ساخت دیتابیس

فایل `database.sql` را اجرا کنید یا دستور زیر را در MySQL بزنید:

```sql
CREATE DATABASE university_library
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

### ۳. تنظیم نام کاربری و رمز MySQL

پروژه رمز دیتابیس را از Environment Variable می‌خواند تا داخل GitHub ذخیره نشود.

Windows PowerShell:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
```

Linux / macOS:

```bash
export DB_USERNAME=root
export DB_PASSWORD=YOUR_MYSQL_PASSWORD
```

### ۴. اجرای بک‌اند

```bash
mvn spring-boot:run
```

بک‌اند روی `http://localhost:8080` بالا می‌آید.

### ۵. اجرای فرانت‌اند

در ترمینال دوم:

```bash
cd frontend
npm install
npm run dev
```

سایت را باز کنید:

```text
http://localhost:5173
```

در حالت توسعه، Vite درخواست‌های `/api` را با Proxy به `http://localhost:8080` می‌فرستد، پس نیازی به تنظیم CORS نیست.

## 📦 اجرای نسخهٔ یکپارچه (Build)

```bash
cd frontend
npm install
npm run build
cd ..
mvn clean package
java -jar target/university-library-1.0.0.jar
```

Maven پوشهٔ `frontend/dist` را داخل فایل JAR قرار می‌دهد و در این حالت React و API هر دو از `http://localhost:8080` سرو می‌شوند.

## 🔑 حساب‌های آزمایشی

| نقش | ایمیل | رمز |
|---|---|---|
| مدیر | `admin@library.local` | `Admin123` |
| کاربر | `user@library.local` | `User123` |

> این حساب‌ها فقط برای تست محلی و ارائه ساخته می‌شوند.

## 📁 ساختار پروژه

```text
university-library-management-system-project/
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
│   ├── entity/               # موجودیت‌های دیتابیس
│   ├── repository/           # Spring Data JPA
│   └── service/              # منطق اصلی برنامه
├── src/main/resources/
│   └── application.properties
├── database.sql
├── pom.xml
└── .gitignore
```

## 👨‍💼 قابلیت‌های مدیر

- داشبورد آماری
- CRUD کتاب‌ها، نویسندگان و دسته‌بندی‌ها
- مشاهدهٔ کاربران
- تأیید یا رد رزرو
- ساخت خودکار امانت پس از تأیید رزرو
- ثبت دستی امانت
- ثبت بازگشت و افزایش موجودی کتاب

## 👤 قابلیت‌های کاربر

- ثبت‌نام، ورود و خروج
- مشاهدهٔ پروفایل
- فهرست، جست‌وجو و فیلتر کتاب‌ها
- مشاهدهٔ جزئیات کتاب
- ثبت و لغو رزرو
- مشاهدهٔ وضعیت رزروها
- مشاهدهٔ امانت‌ها و تاریخ سررسید

## 🧪 تست

```bash
mvn test
```

تست‌های API ورود، دسترسی عادی و دسترسی مدیر در `ApiIntegrationTests.java` قرار دارند و از دیتابیس موقت H2 استفاده می‌کنند.

تست build فرانت:

```bash
cd frontend
npm run build
```

## ⚠️ نکات

- تمام کاربران، کتاب‌ها و حساب‌های نمونه صرفاً داده‌های آزمایشی هستند.
- رمز واقعی دیتابیس نباید داخل GitHub commit شود.
- پوشهٔ `target/` و `frontend/node_modules` باید همیشه در `.gitignore` باقی بمانند.
