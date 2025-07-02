# 🧠 Heikoh Backend - API de Finanzas Personales

Bienvenido al backend de **Heikoh** — una aplicación web de finanzas personales que te ayuda a tomar el control total de tus ingresos, gastos y deudas.  
Este microservicio está construido con **Java + Spring Boot WebFlux**, emplea **arquitectura hexagonal** y expone una **API RESTful reactiva, segura y moderna**. 🚀

---

## ✨ Características Principales

- 🔐 **Autenticación con JWT** (registro, login, protección de endpoints)
- 💰 **CRUD de transacciones financieras** (gastos, ingresos)
- 💳 **Gestión de deudas y pagos parciales o totales**
- 📊 **Dashboard financiero** (balance actual, por período, gráfico por categorías)
- ⚙️ **Categorías personalizadas y configuración de moneda**
- 🧪 **Pruebas con JUnit, Mockito y Testcontainers**
- ✅ Validaciones robustas y gestión de errores centralizada

---

## 🧱 Arquitectura del Proyecto

📦 Basado en una **arquitectura hexagonal** clara y modular:
```bash
src/main/java/io/github/cristhianm30/heikoh/

├── application/ # Casos de uso, servicios, DTOs, mappers
├── domain/ # Lógica de negocio pura, modelos, puertos, validaciones
├── infrastructure/ # Adaptadores REST, persistencia R2DBC, seguridad
└── HeikohApplication.java
```

---

## 🛠️ Tecnologías Utilizadas

| Categoría            | Tecnología                                 |
|---------------------|---------------------------------------------|
| 🟨 Lenguaje          | Java 21                                     |
| ⚛ Framework         | Spring Boot (WebFlux)                       |
| 🐬 Base de Datos     | MySQL + Spring Data R2DBC                   |
| 🔐 Seguridad         | JWT (JSON Web Tokens)                       |
| ↔️ Mapeo de Objetos | MapStruct                                   |
| 🧪 Testing           | JUnit, Mockito, Testcontainers              |
| 📦 Build Tool        | Gradle (Groovy DSL)                         |
| 💻 IDE Recomendado   | IntelliJ IDEA / VS Code                     |
| 🧩 Arquitectura      | Hexagonal (Ports & Adapters)               |


## ⚙️ Cómo Ejecutar Localmente

### 1. Clona el repositorio

```bash
git clone https://github.com/cristhianm30/heikoh.git
cd heikoh
```
### 2. Configura la base de datos MySQL
Crea una base de datos local.

Asegúrate de tener un usuario y contraseña válidos.

### 3. Configura application-local.yml
Recuerda registar tus variables de entorno antes de correr el proyecto.

```yaml
server:
  port: ${SERVER_PORT}
  servlet:
    context-path: /api

spring:
  config:
    activate:
      on-profile: local
  r2dbc:
    url: ${DB_LOCAL_R2DBC_URL}
    username: ${DB_LOCAL_USERNAME}
    password: ${DB_LOCAL_PASSWORD}
  sql:
    init:
      mode: always

security:
  jwt:
    secret: ${JWT_PASSWORD}
    expiration:
      minutes: ${JWT_EXPIRATION_MINUTES}

```
4. Ejecuta la aplicación
```bash
./gradlew bootRun
```
