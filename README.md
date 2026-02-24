# Duit - Plataforma de Servicios y Trabajo

Duit es una aplicación web que conecta personas que buscan servicios con profesionales que los ofrecen. La plataforma permite gestionar ofertas de trabajo, postulaciones, perfiles profesionales y valoraciones.

## Índice

- [Duit - Plataforma de Servicios y Trabajo](#duit---plataforma-de-servicios-y-trabajo)
  - [Índice](#índice)
  - [Descripción general](#descripción-general)
  - [Accesos rápidos](#accesos-rápidos)
  - [Documentación propia](#documentación-propia)
  - [Funcionalidades](#funcionalidades)
    - [Usuarios (clientes)](#usuarios-clientes)
    - [Profesionales](#profesionales)
    - [Administración](#administración)
  - [Instalación y ejecución](#instalación-y-ejecución)
    - [Requisitos](#requisitos)
    - [Clonar](#clonar)
    - [Base de datos](#base-de-datos)
    - [Variables de entorno](#variables-de-entorno)
    - [Ejecución local](#ejecución-local)
  - [Tecnologías](#tecnologías)
  - [Seguridad y gestión de errores](#seguridad-y-gestión-de-errores)
  - [Fuera de scope actual](#fuera-de-scope-actual)
  - [Configuración rápida](#configuración-rápida)
  - [Soporte](#soporte)
  - [README actualizado 24/02/2026](#readme-actualizado-24022026)

## Descripción general

- Buscar profesionales por categoría y ubicación.
- Publicar y gestionar servicios y solicitudes.
- Postular y gestionar acuerdos entre usuarios.
- Valorar experiencias y reputación.
- Administrar categorías y consultar vistas base de usuarios/estadísticas.

## Accesos rápidos

- Aplicación: [https://duitapp.koyeb.app/](https://duitapp.koyeb.app/)
- Base de datos: PostgreSQL en [Neon](https://neon.tech/)
- Trello: [Tablero del proyecto](https://trello.com/b/WuKam2k1/prw)
- Diseño: [Figma](https://www.figma.com/design/y0dsqYgpwDFsx5Hy0YNVtL/PRW?t=IcXPDrcEpCyz3U7G-0)
- Documentación externa: [Duit Docs](https://e.pcloud.link/publink/show?code=kZnwjaZ15u7S4qnaebz8Iq21LSKvRrC4nGX)

> **Aviso de vigencia (24/02/2026):** El diseño en Figma y la documentación externa pueden estar desactualizados (referencia de estado al 01/02/2026). La fuente de verdad del proyecto es la documentación interna en archivos `.md` dentro de la carpeta `documentacion/` de este repositorio.

## Documentación propia

Requisitos y diseño:

- [Plan de proyecto](documentacion/DAW_PRW_R1L2_UT01%20Plan%20de%20proyecto.md)
- [Documento de alcance](documentacion/DAW_PRW_R1L2_UT01.1.%20Documento%20de%20alcance.md)
- [Diagrama de casos de uso](documentacion/DAW_PRW_R1L2_UT01.2.%20Diagrama%20de%20casos%20de%20uso.md)
- [Diagrama ER](documentacion/DAW_PRW_R1L2_UT01.3.%20Diagrama_ER.md)
- [Diseño técnico](documentacion/DAW_PRW_R1L2_UT01.4.%20Diseno%20Tecnico.md)


Despliegue:

- [Documento de despliegue](documentacion/DAW_PRW_R1L2_5.%20Documento%20de%20despliegue.md)

## Funcionalidades

### Usuarios (clientes)

- Buscar profesionales por categoría y ubicación.
- Ver perfiles y valoraciones.
- Crear solicitudes de trabajo.
- Gestionar historial de servicios contratados.

### Profesionales

- Crear y gestionar perfil profesional.
- Publicar ofertas de servicios.
- Ver y responder a postulaciones.
- Gestionar historial de trabajos realizados.

### Administración

- Gestión de categorías (CRUD).
- Vistas base de usuarios y estadísticas (sin CRUD ni métricas reales).

## Instalación y ejecución

### Requisitos

- Java 21 o superior.
- Maven (incluido con el proyecto como `mvnw`).
- PostgreSQL (local o remoto).

### Clonar

```bash
git clone <url-del-repositorio>
cd Duit
```

### Base de datos

Crea una base de datos PostgreSQL y anota las credenciales.

### Variables de entorno

Crea un archivo `.env` en la raíz del proyecto:

```env
DB_URL=jdbc:postgresql://localhost:5432/duit
DB_USER=tu_usuario
DB_PASS=tu_contraseña
```

### Ejecución local

```bash
# En Linux/Mac
./mvnw spring-boot:run

# En Windows
mvnw.cmd spring-boot:run
```

Abre tu navegador en: [http://localhost:8080](http://localhost:8080)

## Tecnologías

- Java 21
- Spring Boot 3.5.10
- Spring Security
- Spring Data JPA
- PostgreSQL
- Thymeleaf
- Bootswatch (tema Flatly)
- HTML5 + CSS3
- JavaScript
- Maven
- Lombok
- Spring DotEnv

## Seguridad y gestión de errores

- Autenticación y autorización con Spring Security.
- Control de acceso por roles en rutas `/admin/**`, `/user/**` y `/professional/**`.
- Cifrado de contraseñas con BCrypt.
- Páginas de error personalizadas en `403`, `404` y `500`.
- Configuración para evitar exposición de detalles internos de error en producción.

## Fuera de scope actual

- Gestión completa de usuarios desde administración (altas, bajas, bloqueos y roles).
- Métricas y analítica administrativas completas.
- Sistema de pagos integrado (Stripe/PayPal).
- Chat en tiempo real o mensajería instantánea.
- Aplicación móvil nativa (iOS/Android).
- Notificaciones push y verificación de cuenta por email.

## Configuración rápida

Las variables principales están en `application.properties` y, si usas entorno local, puedes definir:

```env
DB_URL=jdbc:postgresql://localhost:5432/duit
DB_USER=usuario_postgres
DB_PASS=contraseña_postgres
```

Para el detalle completo de configuración y estructura, consulta la documentación propia.

## Soporte

¿Problemas o dudas?

- Crea un issue en el repositorio.

## README actualizado 24/02/2026
