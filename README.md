🚀 Heikoh Backend: API de Finanzas Personales
"El motor reactivo y seguro para tus finanzas."

✨ ¿Qué es?
Este es el microservicio de backend para la aplicación Heikoh. Construido con Java y Spring Boot WebFlux, es el cerebro que gestiona la lógica de negocio, la seguridad (JWT) y la persistencia de tus datos financieros en MySQL de forma reactiva. Expone una API RESTful para que el frontend interactúe con tus ingresos, gastos y deudas.

🛠️ Stack Tecnológico
Java ☕

Spring Boot WebFlux: API reactivas y no bloqueantes.

Project Reactor: Programación reactiva.

MySQL & Spring Data R2DBC: Persistencia reactiva de datos.

JWT: Autenticación y seguridad de la API.

MapStruct: Mapeo eficiente de objetos.

Gradle: Sistema de construcción.

Arquitectura Hexagonal: Diseño limpio y desacoplado.

JUnit, Mockito, Testcontainers: Testing robusto.

🏗️ Estructura del Proyecto
El backend sigue una Arquitectura Hexagonal para una clara separación de responsabilidades:

src/main/java/io/github/cristhianm30/heikoh/backend/
├── application/     # Casos de uso, DTOs, mappers.
├── domain/          # Modelos de negocio, puertos, casos de uso, excepciones.
├── infrastructure/  # Adaptadores (WebFlux REST, persistencia R2DBC, configuraciones).
└── HeikohBackendApplication.java # Clase principal.
