# Guia de API y Estructura del Proyecto - DUIT

**Endpoints MVC, arquitectura y estructura de carpetas**

---

## 📋 Metadatos del Documento

| Dato                   | Valor                                                     |
| ---------------------- | --------------------------------------------------------- |
| **Nombre del fichero** | DAW_PRW_R1L2_UT02.0 Guia de API y Estructura del Proyecto |
| **Fecha de version**   | 16/02/2026                                                |
| **Autor**              | Aleixo Fdez Cuevas                                        |
| **Version**            | 1.1                                                       |

---

## 📖 Tabla de Contenidos

1. [Introduccion](#1-introduccion)
2. [Estructura del Proyecto](#2-estructura-del-proyecto)
3. [Arquitectura MVC](#3-arquitectura-mvc)
4. [Lista de Endpoints y Controladores](#4-lista-de-endpoints-y-controladores)
5. [Detalle de Controladores](#5-detalle-de-controladores)
6. [Modelos de Datos (DTO)](#6-modelos-de-datos-dto)
7. [Flujo de Autenticacion](#7-flujo-de-autenticacion)
8. [Codigos de Respuesta HTTP](#8-codigos-de-respuesta-http)
9. [Consideraciones de Seguridad](#9-consideraciones-de-seguridad)

---

## 📝 Historial de Revisiones

| Fecha      | Descripcion                                                    | Autor              |
| ---------- | -------------------------------------------------------------- | ------------------ |
| 16/02/2026 | Documento inicial. Guia de endpoints y estructura del proyecto | Aleixo Fdez Cuevas |
| 16/02/2026 | Reformateo con indice interactivo y jerarquia de titulos       | Aleixo Fdez Cuevas |

---

## 1. Introduccion

Este documento proporciona una guia completa sobre:

- **Estructura fisica** del proyecto DUIT
- **Endpoints disponibles** (rutas/controladores web)
- **Modelos de datos** utilizados en transferencias (DTO)
- **Estados de respuesta** HTTP
- **Flujos de autenticacion** y autorizacion
- **Consideraciones de seguridad** implementadas

**Nota**: DUIT es una aplicacion web **MVC con Thymeleaf**, no una API REST. Los endpoints retornan vistas HTML, no JSON.

---

## 2. Estructura del Proyecto

### 2.1 Arbol de directorios

```
Duit/
├── src/
│   ├── main/
│   │   ├── java/es/duit/app/
│   │   │   ├── DuitApplication.java          # Clase principal Spring Boot
│   │   │   ├── config/                       # Configuraciones (Seguridad, BD, Auditoria)
│   │   │   │   ├── SecurityConfig.java       # Configuracion de Spring Security
│   │   │   │   ├── JpaAuditingConfig.java    # Auditoria automatica de BD
│   │   │   │   ├── DataLoader.java           # Carga inicial de datos
│   │   │   │   ├── LoginSuccessHandler.java  # Manejo de login exitoso
│   │   │   │   └── LoginFailureHandler.java  # Manejo de login fallido
│   │   │   ├── controller/                   # Controladores MVC (12 archivos)
│   │   │   │   ├── PublicController.java     # Paginas publicas (login, home)
│   │   │   │   ├── DashboardController.java  # Panel de inicio autenticado
│   │   │   │   ├── AdminController.java      # Panel de administracion
│   │   │   │   ├── RequestFormController.java# Gestion de solicitudes
│   │   │   │   ├── PostulacionesController.java # Gestion de postulaciones
│   │   │   │   ├── ProfileController.java    # Edicion de perfiles
│   │   │   │   ├── ProfessionalController.java # Busqueda de profesionales
│   │   │   │   ├── MyRequestsController.java # Mis solicitudes
│   │   │   │   ├── RatingsController.java    # Sistema de valoraciones
│   │   │   │   ├── SharedController.java     # Vista de historial y seguimiento
│   │   │   │   ├── CategoryController.java   # Gestion de categorias (admin)
│   │   │   │   └── UserControllerAdvice.java # Manejo global de excepciones
│   │   │   ├── service/                      # Logica de negocio (11 servicios)
│   │   │   │   ├── AuthService.java          # Servicios de autenticacion
│   │   │   │   ├── AppUserService.java       # Gestion de usuarios
│   │   │   │   ├── RequestService.java       # Gestion de solicitudes
│   │   │   │   ├── JobService.java           # Gestion de trabajos
│   │   │   │   ├── JobApplicationService.java # Gestion de postulaciones
│   │   │   │   ├── RatingService.java        # Sistema de valoraciones
│   │   │   │   ├── CategoryService.java      # Gestion de categorias
│   │   │   │   ├── SearchService.java        # Busqueda de solicitudes
│   │   │   │   ├── HistoryService.java       # Historial de trabajos
│   │   │   │   ├── AccessLogService.java     # Registro de accesos
│   │   │   │   └── RegistroService.java      # Registro de usuarios
│   │   │   ├── entity/                       # Modelos JPA (11 entidades)
│   │   │   │   ├── BaseEntity.java           # Clase base (auditoria)
│   │   │   │   ├── AppUser.java              # Usuarios del sistema
│   │   │   │   ├── ServiceRequest.java       # Solicitudes de servicio
│   │   │   │   ├── ServiceJob.java           # Trabajos asignados
│   │   │   │   ├── JobApplication.java       # Postulaciones
│   │   │   │   ├── Rating.java               # Valoraciones
│   │   │   │   ├── UserRole.java             # Rol del usuario (ENUM)
│   │   │   │   ├── Category.java             # Categorias de servicio
│   │   │   │   ├── Address.java              # Direcciones de usuario
│   │   │   │   ├── ProfessionalProfile.java  # Perfil profesional
│   │   │   │   └── AccessLog.java            # Registro de accesos
│   │   │   ├── repository/                   # Data Access Layer (11 repositorios)
│   │   │   │   ├── AppUserRepository.java
│   │   │   │   ├── ServiceRequestRepository.java
│   │   │   │   ├── ServiceJobRepository.java
│   │   │   │   ├── JobApplicationRepository.java
│   │   │   │   ├── RatingRepository.java
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── AddressRepository.java
│   │   │   │   ├── ProfessionalProfileRepository.java
│   │   │   │   ├── AccessLogRepository.java
│   │   │   │   ├── RoleRepository.java
│   │   │   │   └── [Mas repositorios especificos]
│   │   │   ├── dto/                          # Data Transfer Objects (9 DTOs)
│   │   │   │   ├── RegistroDTO.java          # Registro de usuario
│   │   │   │   ├── RequestDTO.java           # Solicitud de servicio
│   │   │   │   ├── EditProfileDTO.java       # Edicion de perfil
│   │   │   │   ├── CategoryDTO.java          # Categoria
│   │   │   │   ├── RatingDTO.java            # Valoracion
│   │   │   │   ├── SearchRequestDTO.java     # Busqueda de solicitud
│   │   │   │   ├── HistoryDTO.java           # Historial
│   │   │   │   ├── MyRequestDTO.java         # Mis solicitudes
│   │   │   │   └── [Mas DTOs segun necesidad]
│   │   │   ├── security/                     # Configuracion de seguridad
│   │   │   │   └── CustomUserDetailsService.java # Servicio de autenticacion
│   │   │   └── [Mas paquetes]
│   │   ├── resources/
│   │   │   ├── application.properties         # Configuracion de Spring
│   │   │   ├── static/                       # Archivos estaticos (CSS, JS, imagenes)
│   │   │   │   ├── css/
│   │   │   │   ├── js/
│   │   │   │   │   └── filters.js             # Filtros de busqueda
│   │   │   │   └── img/                       # Imagenes de la aplicacion
│   │   │   └── templates/                    # Vistas Thymeleaf HTML
│   │   │       ├── public/                    # Paginas publicas
│   │   │       │   ├── index.html             # Pagina principal
│   │   │       │   ├── login.html             # Pagina de login
│   │   │       │   ├── signup.html            # Registro de usuario
│   │   │       │   ├── help.html              # Pagina de ayuda
│   │   │       │   ├── terms.html             # Terminos de servicio
│   │   │       │   └── privacy.html           # Politica de privacidad
│   │   │       ├── dashboard/                 # Panel de usuario
│   │   │       │   └── home.html              # Pagina de inicio (autenticado)
│   │   │       ├── jobs/                      # Solicitudes y trabajos
│   │   │       │   ├── request.html           # Crear/editar solicitud
│   │   │       │   ├── search.html            # Busqueda de solicitudes
│   │   │       │   ├── applications.html      # Ver postulaciones
│   │   │       │   ├── myrequest.html         # Detalles de mi solicitud
│   │   │       │   ├── myaplication.html      # Mi postulacion
│   │   │       ├── profile/                   # Perfiles de usuario
│   │   │       │   ├── profileUser.html       # Perfil de usuario
│   │   │       │   └── profileProfessional.html # Perfil profesional
│   │   │       ├── admin/                     # Panel de administracion
│   │   │       │   ├── users.html             # Gestion de usuarios
│   │   │       │   ├── stats.html             # Estadisticas
│   │   │       │   └── categories.html        # Gestion de categorias
│   │   │       ├── shared/                    # Componentes compartidos
│   │   │       │   ├── history.html           # Historial de trabajos
│   │   │       │   └── ratings.html           # Valoraciones
│   │   │       └── components/                # Componentes reutilizables
│   │   │           ├── headerPublic.html      # Header (publico)
│   │   │           ├── headerUser.html        # Header (autenticado)
│   │   │           ├── footerPublic.html      # Footer
│   │   │           ├── cardPublic.html        # Tarjeta de solicitud
│   │   │           └── [Mas componentes]
│   └── test/                                  # Pruebas unitarias e integracion
│       └── java/es/duit/app/
├── pom.xml                                    # Configuracion Maven
├── mvnw                                       # Maven Wrapper (Linux/Mac)
├── mvnw.cmd                                   # Maven Wrapper (Windows)
└── target/                                    # Artefactos compilados

```

### 2.2 Descripcion de capas

| Capa             | Descripcion                                       | Archivos                       |
| ---------------- | ------------------------------------------------- | ------------------------------ |
| **Presentacion** | Vistas HTML con Thymeleaf                         | `templates/**/*.html`          |
| **Controller**   | Recepcion de requests HTTP y redireccion a vistas | `controller/**Controller.java` |
| **Service**      | Logica de negocio central                         | `service/**Service.java`       |
| **Repository**   | Acceso a datos (JPA/Hibernate)                    | `repository/**Repository.java` |
| **Entity**       | Modelos JPA que mapean a tablas                   | `entity/*.java`                |
| **DTO**          | Transferencia de datos entre capas                | `dto/*DTO.java`                |
| **Config**       | Configuracion de seguridad, BD, auditoria         | `config/**Config.java`         |

---

## 3. Arquitectura MVC

DUIT sigue el patron **Model-View-Controller (MVC)**:

```text
REQUEST HTTP → CONTROLLER → SERVICE → REPOSITORY → DATABASE
                    ↓           ↓           ↓
                  MODEL       DTO       ENTITY
                    ↓
                  RENDER → THYMELEAF TEMPLATE → HTML RESPONSE
```

### 3.1 Ejemplo de flujo: Crear una solicitud de servicio

```text
1. Usuario accede a GET /requests/request
   ↓
2. RequestFormController.showRequestForm() (Controller)
   ↓
3. Obtiene usuario autenticado via AuthService (Service)
   ↓
4. Carga datos de BD via ServiceRequestRepository (Repository)
   ↓
5. Renderiza template "jobs/request.html" con Thymeleaf (View)
   ↓
6. Usuario rellena formulario y hace POST /requests/request
   ↓
7. RequestFormController.submitRequestForm() valida y procesa
   ↓
8. RequestService.createRequest() persiste en BD
   ↓
9. Redirect a /requests/my-requests con mensaje de exito
```

---

## 4. Lista de Endpoints y Controladores

| #   | Controlador                 | Base URL                    | Metodos   | Descripcion                                                  |
| --- | --------------------------- | --------------------------- | --------- | ------------------------------------------------------------ |
| 1   | **PublicController**        | `/`, `/login`, `/signup`    | GET, POST | Paginas publicas (login, registro, home)                     |
| 2   | **DashboardController**     | `/home`                     | GET       | Panel de inicio para usuarios autenticados                   |
| 3   | **AdminController**         | `/admin`                    | GET       | Panel de administracion (usuarios, estadisticas, categorias) |
| 4   | **RequestFormController**   | `/requests`                 | GET, POST | Crear y editar solicitudes de servicio                       |
| 5   | **PostulacionesController** | `/jobs/applications`        | GET, POST | Ver y gestionar postulaciones                                |
| 6   | **ProfessionalController**  | `/professionals`, `/search` | GET       | Busqueda de profesionales y solicitudes                      |
| 7   | **MyRequestsController**    | `/requests/my-requests`     | GET       | Visualizar mis solicitudes                                   |
| 8   | **ProfileController**       | `/profile`                  | GET, POST | Edicion de perfil de usuario                                 |
| 9   | **RatingsController**       | `/ratings`                  | GET, POST | Sistema de valoraciones                                      |
| 10  | **SharedController**        | `/shared`                   | GET       | Historial y seguimiento de trabajos                          |
| 11  | **CategoryController**      | `/categories`               | GET, POST | Gestion de categorias (admin)                                |
| 12  | **UserControllerAdvice**    | —                           | —         | Manejo global de excepciones                                 |

---

## 5. Detalle de Controladores

### 5.1 PublicController

**Base URL**: `/`

| Metodo | Ruta                | Vista                         | Descripcion                       |
| ------ | ------------------- | ----------------------------- | --------------------------------- |
| GET    | `/`                 | `public/index`                | Pagina principal de la aplicacion |
| GET    | `/index`            | `public/index`                | Alias de `/`                      |
| GET    | `/login`            | `public/login`                | Formulario de login               |
| GET    | `/login?error=...`  | `public/login`                | Login con mensaje de error        |
| GET    | `/login?logout=...` | `public/login`                | Confirmacion de logout            |
| POST   | `/login`            | Procesado por Spring Security | Autentica usuario via POST        |
| GET    | `/signup`           | `public/signup`               | Formulario de registro            |
| POST   | `/signup`           | Redirige a `/login`           | Registra nuevo usuario            |
| GET    | `/logout`           | Redirige a `/`                | Cierra sesion                     |
| GET    | `/help`             | `public/help`                 | Pagina de ayuda                   |
| GET    | `/terms`            | `public/terms`                | Terminos de servicio              |
| GET    | `/privacy`          | `public/privacy`              | Politica de privacidad            |

**Autenticacion requerida**: No

---

### 5.2 DashboardController

**Base URL**: `/home`

| Metodo | Ruta    | Vista            | Descripcion                                |
| ------ | ------- | ---------------- | ------------------------------------------ |
| GET    | `/home` | `dashboard/home` | Panel de inicio para usuarios autenticados |

**Autenticacion requerida**: Si (ROLE_USER, ROLE_PROFESSIONAL, ROLE_ADMIN)

---

### 5.3 AdminController

**Base URL**: `/admin`

| Metodo | Ruta                | Vista              | Descripcion                              |
| ------ | ------------------- | ------------------ | ---------------------------------------- |
| GET    | `/admin/users`      | `admin/users`      | Gestion y listado de usuarios            |
| GET    | `/admin/stats`      | `admin/stats`      | Estadisticas y metricas de la plataforma |
| GET    | `/admin/categories` | `admin/categories` | Gestion de categorias                    |

**Autenticacion requerida**: Si (ROLE_ADMIN unicamente)

---

### 5.4 RequestFormController

**Base URL**: `/requests`

| Metodo | Ruta                          | Vista                              | Descripcion                                |
| ------ | ----------------------------- | ---------------------------------- | ------------------------------------------ |
| GET    | `/requests/request`           | `jobs/request`                     | Formulario para crear nueva solicitud      |
| GET    | `/requests/request?edit={id}` | `jobs/request`                     | Formulario para editar solicitud existente |
| POST   | `/requests/request`           | Redirige a `/requests/my-requests` | Guardar nueva solicitud o cambios          |
| GET    | `/requests/my-requests`       | `jobs/myrequest`                   | Listado de mis solicitudes                 |
| GET    | `/requests/{id}`              | `jobs/myrequest`                   | Detalles de una solicitud especifica       |
| POST   | `/requests/{id}/delete`       | Redirige a `/requests/my-requests` | Eliminar solicitud                         |

**Autenticacion requerida**: Si (ROLE_USER, ROLE_PROFESSIONAL)

**Notas**:

- Solo el propietario de la solicitud puede editarla o eliminarla.
- Se requiere direccion configurada para crear solicitud.
- Validaciones: titulo, descripcion, categoria, ubicacion.

---

### 5.5 PostulacionesController

**Base URL**: `/jobs/applications`

| Metodo | Ruta                                     | Vista               | Descripcion                        |
| ------ | ---------------------------------------- | ------------------- | ---------------------------------- |
| GET    | `/jobs/applications/{id}`                | `jobs/applications` | Ver postulaciones de una solicitud |
| POST   | `/jobs/applications/aceptar/{id}`        | Redirige            | Aceptar una postulacion            |
| POST   | `/jobs/applications/rechazar/{id}`       | Redirige            | Rechazar una postulacion           |
| POST   | `/jobs/applications/crear/{id}`          | Redirige            | Crear nueva postulacion            |
| GET    | `/jobs/applications/mi-postulacion/{id}` | `jobs/myaplication` | Ver detalle de mi postulacion      |

**Autenticacion requerida**: Si (ROLE_USER, ROLE_PROFESSIONAL)

**Notas**:

- Solo profesionales pueden postularse.
- Solo el propietario de la solicitud puede aceptar o rechazar.
- Regla: una solicitud solo puede tener **una postulacion aceptada**.
- Al aceptar, se genera automaticamente un `ServiceJob`.

---

### 5.6 ProfessionalController

**Base URL**: `/professionals`, `/search`

| Metodo | Ruta                                | Vista                         | Descripcion                                       |
| ------ | ----------------------------------- | ----------------------------- | ------------------------------------------------- |
| GET    | `/professionals`                    | `profile/profileProfessional` | Perfil publico del profesional                    |
| GET    | `/search`                           | `jobs/search`                 | Busqueda de solicitudes por categoria o ubicacion |
| GET    | `/search?category={id}&city={name}` | `jobs/search`                 | Busqueda filtrada                                 |
| POST   | `/search`                           | `jobs/search`                 | Enviar filtros de busqueda                        |

**Autenticacion requerida**: Parcial (busqueda: no; perfil: si)

---

### 5.7 MyRequestsController

**Base URL**: `/requests/my-requests`

| Metodo | Ruta                               | Vista            | Descripcion                |
| ------ | ---------------------------------- | ---------------- | -------------------------- |
| GET    | `/requests/my-requests`            | `jobs/myrequest` | Listado de mis solicitudes |
| GET    | `/requests/my-requests?status={s}` | `jobs/myrequest` | Filtrar por estado         |

**Autenticacion requerida**: Si

---

### 5.8 ProfileController

**Base URL**: `/profile`

| Metodo | Ruta                | Vista                      | Descripcion                     |
| ------ | ------------------- | -------------------------- | ------------------------------- |
| GET    | `/profile/edit`     | `profile/profileUser`      | Formulario de edicion de perfil |
| POST   | `/profile/edit`     | Redirige a `/profile/edit` | Guardar cambios de perfil       |
| GET    | `/profile/{userId}` | `profile/profileUser`      | Ver perfil de otro usuario      |

**Autenticacion requerida**: Si

---

### 5.9 RatingsController

**Base URL**: `/ratings`

| Metodo | Ruta                   | Vista            | Descripcion                 |
| ------ | ---------------------- | ---------------- | --------------------------- |
| GET    | `/ratings`             | `shared/ratings` | Ver valoraciones recibidas  |
| POST   | `/ratings/crear`       | Redirige         | Crear nueva valoracion      |
| POST   | `/ratings/{id}/editar` | Redirige         | Editar valoracion existente |

**Autenticacion requerida**: Si

**Notas**:

- Rango: 1-5 estrellas.
- Solo se puede valorar si has trabajado con la persona.
- Influye en el ranking del profesional.

---

### 5.10 SharedController

**Base URL**: `/shared`

| Metodo | Ruta              | Vista            | Descripcion                       |
| ------ | ----------------- | ---------------- | --------------------------------- |
| GET    | `/shared/history` | `shared/history` | Historial de trabajos completados |
| GET    | `/shared/ratings` | `shared/ratings` | Mis valoraciones recibidas        |

**Autenticacion requerida**: Si

---

### 5.11 CategoryController

**Base URL**: `/categories`

| Metodo | Ruta                        | Vista              | Descripcion                      |
| ------ | --------------------------- | ------------------ | -------------------------------- |
| GET    | `/categories`               | `admin/categories` | Gestion de categorias            |
| POST   | `/categories/crear`         | Redirige           | Crear nueva categoria            |
| POST   | `/categories/{id}/editar`   | Redirige           | Editar categoria                 |
| POST   | `/categories/{id}/eliminar` | Redirige           | Eliminar categoria (soft delete) |

**Autenticacion requerida**: Si (ROLE_ADMIN)


---

**Ultima actualizacion:** 16 de febrero de 2026 
**Version:** 1.1
