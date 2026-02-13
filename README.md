# 💼 Duit - Plataforma de Servicios y Trabajo

Duit es una aplicacion web completa que conecta personas que buscan servicios con profesionales que los ofrecen. La plataforma permite gestionar ofertas de trabajo, postulaciones, perfiles profesionales y un sistema completo de valoraciones entre usuarios.

## 📌 Indice

- [Que hace esta aplicacion?](#que-hace-esta-aplicacion)
- [Demo en produccion](#demo-en-produccion)
- [Enlaces utiles](#enlaces-utiles)
- [Documentacion interna](#documentacion-interna)
- [Funcionalidades principales](#funcionalidades-principales)
- [Instalacion paso a paso](#instalacion-paso-a-paso)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Configuracion rapida](#configuracion-rapida)
- [Soporte](#soporte)

## Que hace esta aplicacion?

- 🔍 **Buscar servicios**: los usuarios pueden encontrar profesionales para trabajos especificos.
- 💼 **Ofertar trabajos**: los profesionales pueden publicar sus servicios disponibles.
- 🤝 **Sistema de postulaciones**: conexion directa entre clientes y profesionales.
- ⭐ **Valoraciones**: sistema de reputacion basado en experiencias previas.
- 👤 **Perfiles profesionales**: gestion completa de informacion profesional.
- 🛡️ **Panel de administracion**: control de usuarios, categorias y estadisticas.

---

## Demo en produccion

- **Aplicacion:** [https://duitapp.koyeb.app/](https://duitapp.koyeb.app/)
- **Base de datos:** PostgreSQL en [Neon](https://neon.tech/)

---

## Enlaces utiles

- **Documentacion externa:** [Duit Docs](https://e.pcloud.link/publink/show?code=kZnwjaZ15u7S4qnaebz8Iq21LSKvRrC4nGX)
- **Trello:** [Tablero del proyecto](https://trello.com/b/WuKam2k1/prw)
- **Diseno:** [Figma](https://www.figma.com/design/y0dsqYgpwDFsx5Hy0YNVtL/PRW?t=IcXPDrcEpCyz3U7G-0)

---

## Documentacion interna

- [Plan de proyecto](documentacion/DAW_PRW_R1L2_UT01.0.%20Documento%20de%20plan%20de%20proyecto.md)
- [Documento de alcance](documentacion/DAW_PRW_R1L2_UT01.1.%20Documento%20de%20alcance.md)
- [Diagrama de casos de uso](documentacion/DAW_PRW_R1L2_UT01.2.%20Diagrama%20de%20casos%20de%20uso.md)
- [Diagrama ER](documentacion/DAW_PRW_R1L2_UT01.3.%20Diagrama_ER.md)
- [Diseno tecnico](documentacion/DAW_PRW_R1L2_UT01.4.%20Diseno%20Tecnico.md)
- [Justificacion tecnica](documentacion/DAW_PRW_R1L2_UT00.0.%20Justificacion%20tecnica.md)
- [Documento de despliegue](documentacion/DAW_PRW_R1L2_5.%20Documento%20de%20despliegue.md)

---

## Funcionalidades principales

### Para usuarios (clientes)

- Buscar profesionales por categoria y ubicacion.
- Ver perfiles y valoraciones de profesionales.
- Crear solicitudes de trabajo.
- Gestionar historial de servicios contratados.

### Para profesionales

- Crear y gestionar perfil profesional.
- Publicar ofertas de servicios.
- Ver y responder a postulaciones.
- Gestionar historial de trabajos realizados.

### Para administradores

- Panel de control con estadisticas.
- Gestion de usuarios y categorias.
- Moderacion de contenido.
- Analisis de actividad de la plataforma.

---

## Instalacion paso a paso

### Prerrequisitos

- Java 21 o superior.
- Maven (incluido con el proyecto como `mvnw`).
- PostgreSQL (local o remoto).

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd Duit
```

### 2. Configurar la base de datos

Crea una base de datos PostgreSQL y anota las credenciales.

### 3. Configurar variables de entorno

Crea un archivo `.env` en la raiz del proyecto:

```env
DB_URL=jdbc:postgresql://localhost:5432/duit
DB_USER=tu_usuario
DB_PASS=tu_contrasena
```

### 4. Ejecutar la aplicacion

```bash
# En Linux/Mac
./mvnw spring-boot:run

# En Windows
mvnw.cmd spring-boot:run
```

### 5. Acceder a la aplicacion

Abre tu navegador en: [http://localhost:8080](http://localhost:8080)

---

## Tecnologias utilizadas

### Backend

- **Java 21** - Lenguaje de programacion.
- **Spring Boot 3.5.10** - Framework principal.
- **Spring Security** - Autenticacion y autorizacion.
- **Spring Data JPA** - Acceso a datos.
- **PostgreSQL** - Base de datos.

### Frontend

- **Thymeleaf** - Motor de plantillas.
- **HTML5 + CSS3** - Estructura y estilos.
- **JavaScript** - Interactividad.

### Herramientas

- **Maven** - Gestion de dependencias.
- **Lombok** - Reducir codigo repetitivo.
- **Spring DotEnv** - Gestion de variables de entorno.

---

## Configuracion rapida

Las variables principales estan en el archivo `application.properties` y, si usas entorno local, puedes definir:

```env
DB_URL=jdbc:postgresql://localhost:5432/duit
DB_USER=usuario_postgres
DB_PASS=contrasena_postgres
```

Para el detalle completo de configuracion y estructura, consulta la documentacion interna.

---

## Soporte

¿Problemas o dudas?

- Crea un issue en el repositorio.

---

## README actualizado 13/02/2026
