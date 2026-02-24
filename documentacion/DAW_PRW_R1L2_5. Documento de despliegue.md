# Documento de Despliegue de Duit

**Guía Completa de Implementación y Lanzamiento en Producción**

---

## Tabla de Metadatos

| Propiedad                  | Valor                                                  |
| -------------------------- | ------------------------------------------------------ |
| **Nombre del Fichero**     | DAW_PRW_R1L2_5 – Documento de Despliegue               |
| **Versión**                | 2.0                                                    |
| **Fecha de Actualización** | 24 de febrero de 2026                                  |
| **Ciclo Formativo**        | Desarrollo de Aplicaciones Web - Semipresencial (DAWN) |
| **Módulo**                 | Proyecto de Desarrollo de Aplicaciones Web (PRW)       |
| **Autores**                | Aleixo Fernández Cuevas, Cristo Manuel Navarro Martín  |

---

## Tabla de Contenidos

1. [Introducción](#introducción)
2. [Detalle de las Mejoras](#detalle-de-las-mejoras)
   - [Mejoras Técnicas](#mejoras-técnicas)
   - [Mejoras Funcionales](#mejoras-funcionales)
3. [Servidor de Aplicaciones](#servidor-de-aplicaciones)
   - [Identificación del Servidor](#identificación-del-servidor)
   - [Requisitos Mínimos](#requisitos-mínimos)
   - [Configuración Recomendada](#configuración-recomendada)
   - [Gestión del Entorno Cloud (Koyeb)](#gestión-del-entorno-cloud-koyeb)
4. [Servidor de Base de Datos](#servidor-de-base-de-datos)
   - [Identificación del Servidor (BD)](#identificación-del-servidor-bd)
   - [Requisitos Mínimos (BD)](#requisitos-mínimos-bd)
   - [Scripts](#scripts)
   - [Gestión de Servicio de Base de Datos (Neon)](#gestión-de-servicio-de-base-de-datos-neon)
5. [Ficheros o Binarios a Desplegar](#ficheros-o-binarios-a-desplegar)
   - [Ubicación](#ubicación)
6. [Operativa](#operativa)
   - [Operaciones de Base de Datos](#operaciones-de-base-de-datos)
   - [Operaciones de Despliegue](#operaciones-de-despliegue)
   - [Condiciones y Verificación de Éxito](#condiciones-y-verificación-de-éxito)
7. [Control de Versiones](#control-de-versiones)
8. [Observaciones](#observaciones)
9. [Reparto de Tareas y Responsabilidades](#reparto-de-tareas-y-responsabilidades)

---

## Tabla de Historial de Revisiones

| Fecha      | Descripción                                   | Autores                                           |
| ---------- | --------------------------------------------- | ------------------------------------------------- |
| 16/02/2026 | Primera redacción                             | Aleixo Fernández Cuevas                           |
| 22/02/2026 | Correcciones finales y revisión de estructura | Aleixo Fernández Cuevas, Cristo M. Navarro Martín |
| 24/02/2026 | Revisión de coherencia y ortografía           | Aleixo Fernández Cuevas, Cristo M. Navarro Martín |

---

## Introducción

El presente documento describe el proceso de **despliegue** de la aplicación **Duit**, una plataforma web desarrollada con **Spring Boot 3.5.10** y **Java 21 LTS** destinada a la gestión de servicios entre clientes y profesionales.

Para el entorno de producción se ha optado por utilizar tanto la aplicación como la base de datos en **servicios en la nube**. Se valoró la posibilidad de mantener la base de datos en un entorno local, pero esta opción fue descartada debido a posibles problemas de conexión, seguridad, disponibilidad y estabilidad. Por ejemplo, una base de datos en un equipo local puede dejar de estar accesible si el equipo se apaga, cambia la IP o falla la conexión a Internet.

El uso de un servicio de base de datos en la nube permite ofrecer mayor estabilidad, acceso continuo y una configuración más sencilla en un entorno real de producción.

### Objetivo

El objetivo de este documento es permitir que **cualquier persona pueda reproducir el proceso de despliegue**, tanto en entorno local como en producción, siguiendo los pasos descritos.

### Versión Desplegada

La versión desplegada corresponde a la **release 0.0.1-SNAPSHOT** e incorpora las siguientes funcionalidades principales:

| Funcionalidad               | Descripción                                                     |
| --------------------------- | --------------------------------------------------------------- |
| **Autenticación**           | Sistema de registro e inicio de sesión mediante Spring Security |
| **Gestión de Roles**        | ADMIN, USER y PROFESSIONAL                                      |
| **Solicitudes de Servicio** | Creación y gestión de solicitudes de servicio                   |
| **Postulaciones**           | Sistema de postulaciones y asignación de trabajos               |
| **Valoraciones**            | Sistema de valoraciones entre usuarios (1-5 estrellas)          |
| **Panel Administrativo**    | Gestión de categorías                                           |
| **Base de Datos**           | PostgreSQL alojada en Neon                                      |
| **Despliegue Cloud**        | Aplicación desplegada mediante Koyeb                            |

---

## Detalle de las Mejoras

### Mejoras Técnicas

| Mejora                 | Descripción                                                                    |
| ---------------------- | ------------------------------------------------------------------------------ |
| Patrón MVC             | Desarrollo siguiendo el patrón MVC con Spring Boot 3.5.10                      |
| Base de Datos Cloud    | Integración con PostgreSQL 15.x alojada en entorno cloud (Neon)                |
| Spring Security        | Configuración completa incluyendo login, remember-me y control de roles (RBAC) |
| Cifrado de contraseñas | Cifrado seguro de contraseñas utilizando BCrypt                                |
| Auditoría Automática   | Sistema de auditoría mediante clase base BaseEntity                            |
| Access Log             | Registro de accesos y acciones relevantes del sistema                          |
| Gestión de Conexiones  | Uso de HikariCP para manejo eficiente de conexiones a base de datos            |
| Despliegue Cloud       | Implementación en entorno cloud usando Koyeb                                   |

### Mejoras Funcionales

| Funcionalidad          | Descripción                                                                 |
| ---------------------- | --------------------------------------------------------------------------- |
| Registro y Login       | Sistema de registro e inicio de sesión para usuarios                        |
| Solicitudes            | Creación y gestión de solicitudes de servicio por parte de clientes         |
| Postulaciones          | Los profesionales pueden postularse a solicitudes publicadas                |
| Aceptación Única       | Regla de negocio que permite seleccionar un único profesional por solicitud |
| Generación de Trabajos | Creación automática de un trabajo al aceptar una postulación                |
| Ciclo de Vida          | Gestión del ciclo de vida del trabajo mediante distintos estados            |
| Valoraciones           | Sistema de valoraciones entre usuarios (1-5 estrellas)                      |
| Administración         | Gestión y administración de categorías desde el panel de administrador      |

---

## Servidor de Aplicaciones

### Identificación del Servidor

| Parámetro            | Valor                                               |
| -------------------- | --------------------------------------------------- |
| Nombre del Servicio  | duit-production                                     |
| Entorno de Ejecución | Cloud, mediante la plataforma Koyeb                 |
| Dominio Público      | https://duitapp.koyeb.app/                          |
| Tipo de Ejecución    | Aplicación Spring Boot con servidor Tomcat embebido |

### Requisitos Mínimos

| Componente         | Versión Requerida |
| ------------------ | ----------------- |
| Java               | 21 LTS            |
| Spring Boot        | 3.5.10            |
| Tomcat (embebido)  | 10.1.x            |
| Maven              | 3.9 o superior    |
| Puerto por Defecto | 8080              |

### Configuración Recomendada

La aplicación utiliza **variables de entorno** para establecer la conexión con la base de datos PostgreSQL. Estas variables deben definirse antes de ejecutar el servicio.

| Variable | Descripción                                     |
| -------- | ----------------------------------------------- |
| DB_URL   | Cadena de conexión JDBC a la base de datos      |
| DB_USER  | Usuario con permisos sobre la base de datos     |
| DB_PASS  | Contraseña asociada al usuario de base de datos |

#### Ubicación de Definición

- **En entorno local**: Mediante archivo `.env`, variables del sistema.
- **En entorno cloud (Koyeb)**: Desde el panel de configuración del servicio, utilizando los datos de conexión proporcionados por Neon.

Estas variables son leídas por Spring Boot mediante la configuración:
```
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
```

> **Nota**: Las credenciales NO se almacenan en el repositorio por motivos de seguridad.

#### Ejecución Manual de la Aplicación

En entorno local, una vez definidas las variables necesarias (`.env`), la aplicación puede iniciarse mediante:

```bash
java -jar target/duit-0.0.1-SNAPSHOT.jar
```

Este comando ejecuta el artefacto generado por Maven utilizando el servidor Tomcat embebido incluido en Spring Boot.

### Gestión del Entorno Cloud (Koyeb)

**Koyeb** es una plataforma **PaaS** (Platform as a Service) que permite desplegar aplicaciones sin administrar servidores físicos o máquinas virtuales.

#### Funciones Principales

| Función                      | Descripción                                    |
| ---------------------------- | ---------------------------------------------- |
| Aprovisionamiento Automático | Asignación automática de CPU y memoria         |
| Entorno Aislado              | Ejecución en contenedor separado               |
| URL Pública                  | Asignación de dominio accesible desde Internet |
| Reinicio Automático          | Reinicio del servicio en caso de fallo         |
| Logs y Monitorización        | Panel con registros y métricas                 |
| Variables de Entorno         | Configuración segura de credenciales           |

#### Modelo PaaS Utilizado

El modelo PaaS permite:
- **Delegar al proveedor** la gestión de infraestructura.
- **Mantener como responsabilidad del equipo** el artefacto generado.
- **Facilitar un desarrollo más ágil**.

> **Nota**: En ambas opciones (GitHub y JAR) debes crear primero la base de datos en Neon para que JPA se sincronice automáticamente. Se recomienda definir las credenciales en un archivo `.env` (o variables de entorno del panel) y usar `SPRING_JPA_HIBERNATE_DDL_AUTO=update`.

#### Opción 1: Despliegue mediante Conexión a GitHub (RECOMENDADO)

Este método utiliza la integración GitHub-Koyeb para compilar y desplegar automáticamente en cada push a la rama principal.

**Procedimiento detallado**: consultar la sección [Operaciones de Despliegue](#operaciones-de-despliegue).

#### Opción 2: Despliegue mediante Docker

Koyeb permite el despliegue de aplicaciones mediante imágenes Docker personalizadas. Este método requiere la creación de un archivo `Dockerfile`, la construcción de la imagen y su posterior publicación en un registro compatible.

No obstante, esta modalidad no se ha utilizado en el presente proyecto, ya que no se trabajó con contenedores Docker durante el desarrollo.

Por este motivo, el despliegue se realizó exclusivamente mediante la integración directa con GitHub, que permite la compilación automática del proyecto a partir del archivo `pom.xml`.




---

## Servidor de Base de Datos

### Identificación del Servidor (BD)

| Parámetro                  | Valor                                 |
| -------------------------- | ------------------------------------- |
| Motor de Base de Datos     | PostgreSQL                            |
| Versión Recomendada        | 15.x                                  |
| Proveedor del Servicio     | Neon (servicio gestionado en la nube) |
| Esquema Utilizado          | public                                |
| Nombre de la Base de Datos | duit_db                               |

### Requisitos Mínimos (BD)

| Parámetro                | Valor           |
| ------------------------ | --------------- |
| Puerto por defecto       | 5432            |
| Driver JDBC              | 42.7.x          |
| Codificación Recomendada | UTF-8           |
| Pool de Conexiones       | HikariCP        |
| SSL requerido            | sslmode=require |

### Scripts

Aunque la aplicación puede generar el esquema automáticamente mediante JPA (`ddl-auto=update`), se entrega adicionalmente un script SQL completo con el fin de garantizar la reproducibilidad del despliegue en cualquier entorno.

**Script principal**: `documentacion/schema.sql`

**Carga del esquema**:

1. Abrir el SQL Editor de Neon.
2. Pegar el contenido de `documentacion/schema.sql`.
3. Ejecutar el script completo.

### Gestión de Servicio de Base de Datos (Neon)

**Neon** es un servicio de base de datos **PostgreSQL** gestionado en la nube (Database as a Service – DBaaS). Permite utilizar una base de datos sin necesidad de instalar, configurar o mantener manualmente un servidor propio, ya que toda la infraestructura es administrada por el proveedor.

#### Servicios Proporcionados por Neon

| Servicio                   | Descripción                                                         |
| -------------------------- | ------------------------------------------------------------------- |
| Instancia gestionada       | Base de datos PostgreSQL mantenida automáticamente por el proveedor |
| Almacenamiento persistente | Datos almacenados en la nube con redundancia                        |
| Conexión segura            | Comunicación cifrada mediante SSL/TLS                               |
| Cadena de conexión JDBC    | Información de conexión generada automáticamente                    |
| Panel web                  | Interfaz para administración y gestión                              |
| Backups automáticos        | Copias de seguridad periódicas                                      |
| Monitorización             | Visualización del uso de recursos                                   |

#### Ventajas del Modelo DBaaS

| Ventaja                    | Descripción                                             |
| -------------------------- | ------------------------------------------------------- |
| Sin administración directa | No es necesario gestionar servidores ni actualizaciones |
| Disponibilidad             | Mayor estabilidad frente a soluciones locales           |
| Seguridad                  | Conexiones cifradas y gestión centralizada              |
| Backups automáticos        | Permite recuperación ante fallos                        |
| Facilidad de uso           | Configuración sencilla desde el panel web               |

#### Creación del Esquema

La información técnica detallada relativa a la administración, configuración y gestión operativa de la base de datos se desarrolla en la [sección Operaciones de Base de Datos](#operaciones-de-base-de-datos).

La creación del esquema puede realizarse de dos formas:

- **Mediante JPA** (modo automático), utilizando la propiedad `SPRING_JPA_HIBERNATE_DDL_AUTO=update`, lo que permite que la aplicación genere las tablas al iniciarse.
- **Mediante el script schema.sql**, incluido en el repositorio y en el paquete de entrega, que permite crear manualmente toda la estructura de la base de datos.


---

## Ficheros o Binarios a Desplegar

### Ubicación

El archivo comprimido **DAW_PRW_R1L2_5_cod.zip** contiene todos los elementos necesarios para reproducir el despliegue de la aplicación en un entorno de producción o pruebas.

Todos los documentos incluidos pueden encontrarse en el repositorio oficial del proyecto:

**https://github.com/FerCueA/Duit**

A excepción del archivo de variables de entorno `.env`, que no se incluye en el repositorio por motivos de seguridad. En su lugar, se proporciona el archivo `.env.example` como plantilla de configuración.

#### Aplicación Compilada (Binario Ejecutable)

- **Ruta**: `Duit/target/duit-0.0.1-SNAPSHOT.jar`
- Archivo generado tras la compilación del proyecto mediante Maven. Es el binario que se despliega en el servidor de aplicaciones.

#### Script de Base de Datos

- **Ruta**: `Duit/documentacion/schema.sql`
- Contiene la estructura completa de la base de datos en formato SQL.

#### Plantilla de Variables de Entorno

- **Ruta**: `Duit/.env.example`
- Archivo de ejemplo con las variables necesarias para configurar la conexión a la base de datos.

#### Configuración de Spring Boot

- **Ruta**: `Duit/src/main/resources/application.properties`
- Archivo de configuración base de la aplicación. Este fichero se empaqueta automáticamente dentro del archivo `.jar` durante la compilación.

#### Documento de Despliegue en PDF

- **Ruta**: `Duit/documentacion/DAW_PRW_R1L2_5_Despliegue.pdf`
- Documento explicativo del proceso completo de despliegue.




## Operativa

### Operaciones de Base de Datos

A continuación se describen los pasos necesarios para preparar la base de datos en **Neon** antes de desplegar la aplicación.

Accede a https://neon.com/, crea una cuenta e inicia sesión.

#### Paso 1: Crear Proyecto en Neon

1. Haz clic en **New Project**.
2. Asigna un nombre descriptivo (ej: `duit-production`).
3. Selecciona la región más cercana.
4. Neon creará automáticamente una base de datos inicial llamada `postgres`.

#### Paso 2: Obtener la Cadena de Conexión

1. En el panel del proyecto en Neon:
2. Haz clic en **Connection**.

**Ejemplo**:
```
psql 'postgresql://neondb_owner:contraseña@ep-sparkling-mud-ag8gop19-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require'
```

Esta cadena contiene toda la información necesaria para conectarse a la base de datos.

#### Paso 3: Separar los Componentes de la Cadena

A partir de ella, define las siguientes variables:

**Formato**: `postgresql://USUARIO:CONTRASEÑA@HOST/NOMBRE_BASE?PARÁMETROS`

**Usuario**: `neondb_owner`
- `DB_USER=neondb_owner`
- Es el valor que aparece después de `://` y antes de `:`

**Contraseña**: `contraseña`
- `DB_PASS=contraseña`
- Es el valor que aparece después de `:` y antes de `@`

**Host**: `ep-sparkling-mud-ag8gop19-****************`
- `DB_URL=ep-sparkling-mud-ag8gop19-****************`
- Es la dirección del servidor entre `@` y `/`

#### Configuración en Entorno Local

Crea un archivo `.env` en la raíz del proyecto:

```
DB_URL=ep-sparkling-mud-ag8gop19-****************
DB_USER=neondb_owner
DB_PASS=contraseña
```

**IMPORTANTE**: El archivo `.env` no debe subirse al repositorio, ya que contiene credenciales sensibles (usuario y contraseña de la base de datos).

Para evitar su inclusión accidental en Git, debe añadirse al archivo `.gitignore` del proyecto con la siguiente línea:
```
.env
```

De esta forma, Git ignorará automáticamente el archivo y no permitirá que sea versionado ni publicado en el repositorio remoto.

#### Enlace con application.properties

La aplicación obtiene estos valores mediante variables de entorno configuradas en el archivo:
```
src/main/resources/application.properties
```

En dicho archivo se encuentran definidas las siguientes propiedades:
```
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
```

#### Inicialización de la Base de Datos

La aplicación permite inicializar la estructura de la base de datos de dos maneras:

**Opción A: Generación Automática mediante JPA (Recomendada)**

1. Define en properties:
   ```
   SPRING_JPA_HIBERNATE_DDL_AUTO=update
   ```
2. Inicia la aplicación. Spring Boot creará automáticamente las tablas, índices y relaciones según las entidades definidas en el proyecto.

Esta opción es más sencilla y recomendable para entornos de desarrollo o pruebas.

**Opción B: Creación Manual mediante script schema.sql**

Esta opción permite crear la base de datos de forma controlada utilizando el script incluido en el repositorio y en el ZIP de entrega.

1. Accede al **SQL Editor** en Neon.
2. Selecciona la base de datos que se vaya a utilizar (por ejemplo `postgres` o una base creada previamente).
3. Copia el contenido del archivo `documentacion/schema.sql`.
4. Ejecuta el script completo.


### Operaciones de Despliegue

La aplicación puede desplegarse en Koyeb mediante dos métodos: despliegue automático desde GitHub o despliegue manual mediante archivo JAR.

#### OPCIÓN A: Despliegue Automático desde GitHub (Recomendada)

Este método vincula el repositorio de GitHub con Koyeb, permitiendo despliegues automáticos con cada actualización en la rama `main`.

Accede a https://www.koyeb.com e inicia sesión.

**Paso 1: Acceder a Koyeb**

1. Seleccionar **Create Service**.

**Paso 2: Conectar el Repositorio**

1. Seleccionar **GitHub** como fuente de código.
2. Autorizar el acceso si es la primera vez.
3. Elegir el repositorio **Duit**.
4. Continuar con **Next**.
5. Revisa la configuración necesaria para el servicio, en este caso Free.

**Paso 3: Configuración del Servicio**

Completar los siguientes parámetros:

El sistema detectará automáticamente el archivo `pom.xml` y ejecutará el proceso de compilación mediante Maven.

**Paso 4: Configuración de Variables de Entorno**

En la sección **Environment Variables**, añadir:

```
DB_URL=jdbc:postgresql://tu-host.us-east-1.aws.neon.tech:5432/duit_db?sslmode=require
DB_USER=tu_usuario
DB_PASS=tu_contraseña_segura
```

Estas variables permiten establecer la conexión con la base de datos Neon.

**Actualizaciones Posteriores**

Cualquier `git push` a la rama `main` generará automáticamente un nuevo despliegue.

#### OPCIÓN B: Alternativa de Despliegue mediante Docker

Koyeb permite una segunda modalidad de despliegue basada en imágenes Docker. Este método consiste en construir una imagen personalizada de la aplicación mediante un archivo `Dockerfile` y subirla posteriormente al servicio.

No obstante, en el presente proyecto no se ha utilizado esta opción, ya que no se ha trabajado con contenedores Docker durante el desarrollo.

Por este motivo, se ha optado por el despliegue automático mediante integración directa con GitHub, que resulta más adecuado para el alcance y los requisitos del proyecto.

### Condiciones y Verificación de Éxito

Una vez finalizado el despliegue, el operador deberá realizar las siguientes verificaciones para confirmar que la aplicación está funcionando correctamente y que no se ha producido ningún error grave:

#### 1. Verificación del Estado del Servicio

- Comprobar en el panel de Koyeb que el servicio aparece con estado **Running**.
- Confirmar que no existen reinicios continuos del contenedor.

#### 2. Acceso al Dominio Público

- Acceder a la URL pública asignada (ej: https://duitapp.koyeb.app/).
- Verificar que la página principal carga sin errores.

#### 3. Revisión de Logs del Servidor

- Acceder a la sección de logs en Koyeb.
- Confirmar que no existen errores críticos (HTTP 500, errores de conexión a base de datos o excepciones no controladas).
- Verificar que la aplicación ha iniciado correctamente (mensaje de "Started Application" en logs).

#### 4. Comprobación de Conexión con la Base de Datos

- Intentar registrar un nuevo usuario.
- Verificar que los datos se almacenan correctamente en Neon.
- Confirmar que no aparecen errores relacionados con el datasource.

#### 5. Prueba de Autenticación

- Acceder a `/login`.
- Iniciar sesión con un usuario válido.
- Verificar que la sesión se mantiene activa.

#### 5.1. Prueba de Autorización por Rol

- Intentar acceder con usuario no administrador a una ruta `/admin/**`.
- Verificar que el sistema bloquea el acceso y devuelve la pantalla de error 403.
- Confirmar que un usuario ADMIN sí puede acceder a la misma ruta.

#### 6. Prueba Básica de Funcionalidad

- Crear una solicitud de servicio.
- Realizar una postulación.
- Aceptar una postulación y comprobar que se genera el trabajo asociado.

#### 7. Verificación de Persistencia

- Reiniciar el servicio desde Koyeb.
- Comprobar que los datos previamente creados siguen existiendo.

#### 8. Verificación de Páginas de Error Personalizadas

- Forzar una URL inexistente y comprobar renderizado de la página 404.
- Validar que la aplicación utiliza plantillas personalizadas para 403/404/500.
- Confirmar que no se muestran trazas internas en el navegador.

---

## Control de Versiones

El código fuente del proyecto se gestiona mediante Git y se encuentra disponible en el repositorio oficial.

**https://github.com/FerCueA/Duit**

El historial de cambios puede consultarse en la sección **Commits** del repositorio.

Durante el desarrollo se trabajó principalmente sobre la rama `main`, utilizando en algunos momentos ramas auxiliares para implementar funcionalidades antes de integrarlas.

### Observación sobre el Historial

En una fase intermedia del proyecto se produjo la **eliminación accidental de varios commits** correspondientes a los meses de diciembre y parte de enero.

Tras detectar el problema, se continuó el desarrollo con normalidad, manteniendo una gestión más cuidadosa del repositorio.

La versión actual refleja correctamente el estado final del proyecto entregado y desplegado.

---

## Observaciones

- El despliegue se ha realizado utilizando planes gratuitos de Koyeb y Neon, por lo que pueden existir limitaciones de recursos en comparación con entornos profesionales.
- Las credenciales de acceso a la base de datos no se almacenan en el repositorio, utilizándose variables de entorno por motivos de seguridad.
- Se recomienda configurar alertas de monitorización en el proveedor cloud para detectar posibles caídas o reinicios del servicio.

## Reparto de Tareas y Responsabilidades

El proceso de despliegue de la aplicación Duit fue coordinado principalmente por **Aleixo Fernández Cuevas**, con apoyo puntual de **Cristo Manuel Navarro Martín** en tareas de verificación y validación del entorno.

### Aleixo Fernández Cuevas

**Responsable de:**

- Investigación y selección de proveedores cloud.
- Configuración del servicio en Koyeb.
- Creación y configuración del proyecto en Neon.
- Definición de variables de entorno.
- Configuración de la conexión entre aplicación y base de datos.
- Generación y validación del script `schema.sql`.
- Verificación técnica del correcto funcionamiento en producción.

### Cristo Manuel Navarro Martín

**Colaboración en:**

- Pruebas funcionales tras el despliegue.
- Validación del acceso al sistema y funcionamiento del login.
- Comprobación de persistencia de datos.
- Revisión final antes de la entrega.

---

**Última actualización:** 24 de febrero de 2026 
**Versión:** 2.0
