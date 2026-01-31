# 💼 Duit - Plataforma de Servicios y Trabajo

Duit es una aplicación web completa que conecta personas que buscan servicios con profesionales que los ofrecen. La plataforma permite gestionar ofertas de trabajo, postulaciones, perfiles profesionales y un sistema completo de valoraciones entre usuarios.

## ✨ ¿Qué hace esta aplicación?

- 🔍 **Buscar servicios**: Los usuarios pueden encontrar profesionales para trabajos específicos
- 💼 **Ofertar trabajos**: Los profesionales pueden publicar sus servicios disponibles
- 🤝 **Sistema de postulaciones**: Conexión directa entre clientes y profesionales
- ⭐ **Valoraciones**: Sistema de reputación basado en experiencias previas
- 👤 **Perfiles profesionales**: Gestión completa de información profesional
- 🛡️ **Panel de administración**: Control de usuarios, categorías y estadísticas

---

## 🚀 Demo en producción

- **Aplicación:** [https://duitapp.koyeb.app/](https://duitapp.koyeb.app/)
- **Base de datos:** PostgreSQL en [Neon](https://neon.tech/)

---

## 📚 Enlaces útiles

- **Documentación:** [Duit Docs](https://e.pcloud.link/publink/show?code=kZnwjaZ15u7S4qnaebz8Iq21LSKvRrC4nGX)
- **Trello:** [Tablero del proyecto](https://trello.com/b/WuKam2k1/prw)
- **Diseño:** [Figma](https://www.figma.com/design/y0dsqYgpwDFsx5Hy0YNVtL/PRW?t=IcXPDrcEpCyz3U7G-0)

---

## 🗺️ Funcionalidades principales

### 👥 Para usuarios (clientes)

- Buscar profesionales por categoría y ubicación
- Ver perfiles y valoraciones de profesionales
- Crear solicitudes de trabajo
- Gestionar historial de servicios contratados

### 🔧 Para profesionales

- Crear y gestionar perfil profesional
- Publicar ofertas de servicios
- Ver y responder a postulaciones
- Gestionar historial de trabajos realizados

### 🛡️ Para administradores

- Panel de control con estadísticas
- Gestión de usuarios y categorías
- Moderación de contenido
- Análisis de actividad de la plataforma

---

## 🗂️ Rutas del sistema

### Páginas públicas

| Ruta           | Descripción                    |
|----------------|--------------------------------|
| `/`            | Página de inicio               |
| `/login`       | Iniciar sesión                 |
| `/registro`    | Registro de usuarios           |
| `/terminos`    | Términos y condiciones         |
| `/privacidad`  | Política de privacidad         |
| `/ayuda`       | Centro de ayuda                |

### Área de usuario autenticado

| Ruta                    | Descripción                           |
|-------------------------|---------------------------------------|
| `/home`                 | Dashboard principal                   |
| `/jobs/buscar`          | Buscar trabajos disponibles         |
| `/jobs/crear`           | Publicar nuevo trabajo               |
| `/jobs/mis-solicitudes` | Mis solicitudes de trabajo           |
| `/jobs/postular`        | Ver trabajos para postular          |
| `/profile/editar`       | Editar perfil personal              |
| `/profile/profesional`  | Gestionar perfil profesional        |
| `/valoraciones`         | Ver y gestionar valoraciones         |
| `/historial`           | Historial de actividades            |

### Panel de administración

| Ruta                  | Descripción                    |
|-----------------------|--------------------------------|
| `/admin/usuarios`     | Gestión de usuarios            |
| `/admin/categorias`   | Gestión de categorías          |
| `/admin/estadisticas` | Panel de estadísticas          |

---

## 📁 Estructura del proyecto

```
📦 Duit/
├── 📁 src/main/
│   ├── 📁 java/es/duit/app/
│   │   ├── 📁 controller/           # Controladores (lógica de endpoints)
│   │   │   ├── PublicController     # Páginas públicas
│   │   │   ├── DashboardController  # Panel principal
│   │   │   ├── RequestController    # Gestión de trabajos
│   │   │   ├── ProfileController    # Perfiles de usuario
│   │   │   ├── AdminController      # Panel de administración
│   │   │   └── ...
│   │   ├── 📁 entity/              # Modelos de datos (base de datos)
│   │   │   ├── AppUser             # Usuarios del sistema
│   │   │   ├── ServiceRequest      # Solicitudes de trabajo
│   │   │   ├── ProfessionalProfile # Perfiles profesionales
│   │   │   ├── Rating              # Sistema de valoraciones
│   │   │   └── ...
│   │   ├── 📁 service/             # Lógica de negocio
│   │   ├── 📁 repository/          # Acceso a datos
│   │   ├── 📁 security/            # Configuración de seguridad
│   │   └── 📁 config/              # Configuración general
│   └── 📁 resources/
│       ├── 📁 templates/           # Plantillas HTML (Thymeleaf)
│       │   ├── 📁 public/          # Vistas públicas
│       │   ├── 📁 dashboard/       # Panel de usuario
│       │   ├── 📁 jobs/            # Gestión de trabajos
│       │   ├── 📁 profile/         # Perfiles
│       │   ├── 📁 admin/           # Panel de administración
│       │   └── 📁 components/      # Componentes reutilizables
│       ├── 📁 static/
│       │   ├── 📁 css/             # Estilos CSS
│       │   ├── 📁 js/              # Scripts JavaScript
│       │   └── 📁 img/             # Imágenes
│       └── application.properties   # Configuración de la app
├── pom.xml                         # Dependencias Maven
└── README.md                       # Este archivo
```

---

## 🚀 Instalación paso a paso

### Prerrequisitos

- Java 21 o superior
- Maven (incluido con el proyecto como `mvnw`)
- PostgreSQL (local o remoto)

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd Duit
```

### 2. Configurar la base de datos

Crea una base de datos PostgreSQL y anota las credenciales.

### 3. Configurar variables de entorno

Crea un archivo `.env` en la raíz del proyecto:

```env
DB_URL=jdbc:postgresql://localhost:5432/duit
DB_USER=tu_usuario
DB_PASS=tu_contraseña
```

### 4. Ejecutar la aplicación

```bash
# En Linux/Mac
./mvnw spring-boot:run

# En Windows
mvnw.cmd spring-boot:run
```

### 5. Acceder a la aplicación

Abre tu navegador en: [http://localhost:8080](http://localhost:8080)

---

## ⚙️ Tecnologías utilizadas

### Backend

- **Java 21** - Lenguaje de programación
- **Spring Boot 3.5.10** - Framework principal
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Acceso a datos
- **PostgreSQL** - Base de datos

### Frontend

- **Thymeleaf** - Motor de plantillas
- **HTML5 + CSS3** - Estructura y estilos
- **JavaScript** - Interactividad

### Herramientas

- **Maven** - Gestión de dependencias
- **Lombok** - Reducir código repetitivo
- **Spring DotEnv** - Gestión de variables de entorno

---

## 📦 Dependencias principales

```xml
<!-- Núcleo de Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Base de datos -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- Seguridad -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Plantillas web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Utilidades -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

---

## 🔧 Configuración para desarrollo

### Variables de entorno requeridas

```env
# Base de datos
DB_URL=jdbc:postgresql://localhost:5432/duit
DB_USER=usuario_postgres
DB_PASS=contraseña_postgres

# Opcional: Configuración de correo (si se usa)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-contraseña-app
```

### Configuración de desarrollo local

El archivo `application.properties` ya está configurado para desarrollo:

- Hot reload de Thymeleaf activado
- SQL logging habilitado
- Modo debug para Spring Security

---

## 🤝 Contribuir al proyecto

1. Haz fork del repositorio
2. Crea una rama para tu feature: `git checkout -b feature/nueva-funcionalidad`
3. Realiza tus cambios y commitea: `git commit -m 'Añadir nueva funcionalidad'`
4. Push a la rama: `git push origin feature/nueva-funcionalidad`
5. Abre un Pull Request

---

## 📧 Soporte

¿Problemas o dudas?

- Crea un issue en el repositorio

---

*README actualizado 01/02/2026*
