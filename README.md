# 💼 Duit - Plataforma de Servicios y Trabajo

Duit es una aplicacion web que conecta personas que buscan servicios con profesionales que los ofrecen. La plataforma permite gestionar ofertas de trabajo, postulaciones, perfiles profesionales y valoraciones.

## Indice

- [💼 Duit - Plataforma de Servicios y Trabajo](#-duit---plataforma-de-servicios-y-trabajo)
  - [Indice](#indice)
  - [Descripcion general](#descripcion-general)
  - [Accesos rapidos](#accesos-rapidos)
  - [Documentacion propia](#documentacion-propia)
  - [Funcionalidades](#funcionalidades)
    - [Usuarios (clientes)](#usuarios-clientes)
    - [Profesionales](#profesionales)
    - [Administracion](#administracion)
  - [Instalacion y ejecucion](#instalacion-y-ejecucion)
    - [Requisitos](#requisitos)
    - [Clonar](#clonar)
    - [Base de datos](#base-de-datos)
    - [Variables de entorno](#variables-de-entorno)
    - [Run local](#run-local)
  - [Tecnologias](#tecnologias)
  - [Configuracion rapida](#configuracion-rapida)
  - [Soporte](#soporte)
  - [README actualizado 16/02/2026](#readme-actualizado-16022026)

## Descripcion general

- Buscar profesionales por categoria y ubicacion.
- Publicar y gestionar servicios y solicitudes.
- Postular y gestionar acuerdos entre usuarios.
- Valorar experiencias y reputacion.
- Administrar usuarios, categorias y estadisticas.

## Accesos rapidos

- Aplicacion: [https://duitapp.koyeb.app/](https://duitapp.koyeb.app/)
- Base de datos: PostgreSQL en [Neon](https://neon.tech/)
- Trello: [Tablero del proyecto](https://trello.com/b/WuKam2k1/prw)
- Diseno: [Figma](https://www.figma.com/design/y0dsqYgpwDFsx5Hy0YNVtL/PRW?t=IcXPDrcEpCyz3U7G-0)
- Documentacion externa: [Duit Docs](https://e.pcloud.link/publink/show?code=kZnwjaZ15u7S4qnaebz8Iq21LSKvRrC4nGX)

## Documentacion propia

Requisitos y diseño:

- [Plan de proyecto](documentacion/DAW_PRW_R1L2_UT01%20Plan%20de%20proyecto.md)
- [Documento de alcance](documentacion/DAW_PRW_R1L2_UT01.1.%20Documento%20de%20alcance.md)
- [Diagrama de casos de uso](documentacion/DAW_PRW_R1L2_UT01.2.%20Diagrama%20de%20casos%20de%20uso.md)
- [Diagrama ER](documentacion/DAW_PRW_R1L2_UT01.3.%20Diagrama_ER.md)
- [Diseno tecnico](documentacion/DAW_PRW_R1L2_UT01.4.%20Diseno%20Tecnico.md)

Desarrollo:

- [Guia de API y Estructura del Proyecto](documentacion/DAW_PRW_R1L2_UT02.0.%20Guia%20de%20API%20y%20Estructura%20del%20Proyecto.md)

Despliegue:

- [Documento de despliegue](documentacion/DAW_PRW_R1L2_5.%20Documento%20de%20despliegue.md)

## Funcionalidades

### Usuarios (clientes)

- Buscar profesionales por categoria y ubicacion.
- Ver perfiles y valoraciones.
- Crear solicitudes de trabajo.
- Gestionar historial de servicios contratados.

### Profesionales

- Crear y gestionar perfil profesional.
- Publicar ofertas de servicios.
- Ver y responder a postulaciones.
- Gestionar historial de trabajos realizados.

### Administracion

- Panel de control con estadisticas.
- Gestion de usuarios y categorias.
- Analisis de actividad de la plataforma.

## Instalacion y ejecucion

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

Crea un archivo `.env` en la raiz del proyecto:

```env
DB_URL=jdbc:postgresql://localhost:5432/duit
DB_USER=tu_usuario
DB_PASS=tu_contrasena
```

### Run local

```bash
# En Linux/Mac
./mvnw spring-boot:run

# En Windows
mvnw.cmd spring-boot:run
```

Abre tu navegador en: [http://localhost:8080](http://localhost:8080)

## Tecnologias

- Java 21
- Spring Boot 3.5.10
- Spring Security
- Spring Data JPA
- PostgreSQL
- Thymeleaf
- HTML5 + CSS3
- JavaScript
- Maven
- Lombok
- Spring DotEnv

## Configuracion rapida

Las variables principales estan en `application.properties` y, si usas entorno local, puedes definir:

```env
DB_URL=jdbc:postgresql://localhost:5432/duit
DB_USER=usuario_postgres
DB_PASS=contrasena_postgres
```

Para el detalle completo de configuracion y estructura, consulta la documentacion propia.

## Soporte

¿Problemas o dudas?

- Crea un issue en el repositorio.

## README actualizado 16/02/2026
