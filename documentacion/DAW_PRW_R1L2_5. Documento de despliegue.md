# Documento de Despliegue de Duit

**Guía Completa de Implementación y Lanzamiento en Producción**

---

##  Tabla de Metadatos

| Propiedad                  | Valor                                                  |
| -------------------------- | ------------------------------------------------------ |
| **Nombre del Fichero**     | DAW_PRW_R1L2_5 – Documento de Despliegue               |
| **Versión**                | 1.0                                                    |
| **Fecha de Actualización** | 16 de febrero de 2026                                  |
| **Ciclo Formativo**        | Desarrollo de Aplicaciones Web - Semipresencial (DAWN) |
| **Módulo**                 | Proyecto de Desarrollo de Aplicaciones Web (PRW)       |
| **Autores**                | Aleixo Fernández Cuevas, Cristo Manuel Navarro Martín  |

---

##  Tabla de Contenidos

1. [Introducción](#1-introducción)
2. [Detalle de las Mejoras](#2-detalle-de-las-mejoras)
   - [Mejoras Técnicas](#21-mejoras-técnicas)
   - [Mejoras Funcionales](#22-mejoras-funcionales)
3. [Servidor de Aplicaciones](#3-servidor-de-aplicaciones)
   - [Identificación del Servidor](#31-identificación-del-servidor)
   - [Requisitos Mínimos](#32-requisitos-mínimos)
   - [Configuración Recomendada](#33-configuración-recomendada)
   - [Gestión del Entorno Cloud (Koyeb)](#34-gestión-del-entorno-cloud-koyeb)
4. [Servidor de Base de Datos](#4-servidor-de-base-de-datos)
   - [Identificación del Servidor](#41-identificación-del-servidor)
   - [Requisitos Mínimos](#42-requisitos-mínimos)
   - [Scripts](#43-scripts)
   - [Gestión del Servicio (Neon)](#44-gestión-del-servicio-de-base-de-datos-neon)
5. [Ficheros o Binarios a Desplegar](#5-ficheros-o-binarios-a-desplegar)
   - [Ubicación](#51-ubicación)
6. [Operativa](#6-operativa)
   - [Operaciones de Base de Datos](#61-operaciones-de-base-de-datos)
   - [Operaciones de Despliegue](#62-operaciones-de-despliegue)
   - [Condiciones y Verificación de Éxito](#63-condiciones-y-verificación-de-éxito)
7. [Control de Versiones](#7-control-de-versiones)
8. [Observaciones](#8-observaciones)
9. [Reparto de Tareas y Responsabilidades](#9-reparto-de-tareas-y-responsabilidades)

---

## 📝 Tabla de Historial de Revisiones


| Fecha      | Descripción                  | Autores                             |
| ---------- | ---------------------------- | ----------------------------------- |
| 21/12/2025 | Primera entrega. Versión 1.0 | Aleixo F. Cuevas / Cristo N. Martín |


---

## 1. Introducción

El presente documento describe el proceso de **despliegue** de la aplicación **Duit**, una plataforma web desarrollada con **Spring Boot 3.5.10** y **Java 21 LTS** destinada a la gestión de servicios entre clientes y profesionales.

### Versión Desplegada

La versión desplegada corresponde a la **release 0.0.1-SNAPSHOT** e incorpora las siguientes funcionalidades principales:

| Funcionalidad               | Descripción                                                     |
| --------------------------- | --------------------------------------------------------------- |
| **Autenticación**           | Sistema de registro e inicio de sesión mediante Spring Security |
| **Gestión de Roles**        | ADMIN, USER y PROFESSIONAL                                      |
| **Solicitudes de Servicio** | Creación y gestión de solicitudes de servicio                   |
| **Postulaciones**           | Sistema de postulaciones y asignación de trabajos               |
| **Valoraciones**            | Sistema de valoraciones entre usuarios (1-5 estrellas)          |
| **Panel Administrativo**    | Gestión de usuarios y categorías                                |
| **Base de Datos**           | PostgreSQL alojada en Neon                                      |
| **Despliegue Cloud**        | Aplicación desplegada mediante Koyeb                            |

### Objetivo

El objetivo de este documento es permitir que **cualquier persona pueda reproducir el proceso de despliegue**, tanto en entorno local como en producción, siguiendo los pasos descritos.

---

## 2. Detalle de las Mejoras

### 2.1. Mejoras Técnicas

| Mejora                    | Descripción                                                     |
| ------------------------- | --------------------------------------------------------------- |
| **Patrón MVC**            | Desarrollo siguiendo el patrón MVC con Spring Boot 3.5.10       |
| **Base de Datos Cloud**   | Integración con PostgreSQL 15.x alojada en entorno cloud (Neon) |
| **Spring Security**       | Configuración completa incluyendo login, remember-me y RBAC     |
| **Encriptación**          | Cifrado seguro de contraseñas utilizando BCrypt                 |
| **Auditoría Automática**  | Sistema de auditoría mediante clase base BaseEntity             |
| **Access Log**            | Registro de accesos y acciones relevantes                       |
| **Gestión de Conexiones** | HikariCP para manejo eficiente de conexiones a BD               |
| **Despliegue Cloud**      | Implementación en entorno cloud usando Koyeb                    |

### 2.2. Mejoras Funcionales

| Funcionalidad              | Descripción                                                      |
| -------------------------- | ---------------------------------------------------------------- |
| **Registro y Login**       | Sistema de registro e inicio de sesión para usuarios             |
| **Solicitudes**            | Creación y gestión de solicitudes de servicio por clientes       |
| **Postulaciones**          | Profesionales se postulan a solicitudes publicadas               |
| **Aceptación Única**       | Regla que permite seleccionar un único profesional por solicitud |
| **Generación de Trabajos** | Creación automática de un trabajo al aceptar postulación         |
| **Ciclo de Vida**          | Gestión del ciclo de vida del trabajo mediante distintos estados |
| **Valoraciones**           | Sistema de valoraciones entre usuarios (1-5 estrellas)           |
| **Administración**         | Gestión y administración de categorías desde panel admin         |

---

## 3. Servidor de Aplicaciones

### 3.1. Identificación del Servidor

| Parámetro                | Valor                                                    |
| ------------------------ | -------------------------------------------------------- |
| **Nombre del Servicio**  | duit-production                                          |
| **Entorno de Ejecución** | Cloud, mediante la plataforma Koyeb                      |
| **Dominio Público**      | [https://duitapp.koyeb.app/](https://duitapp.koyeb.app/) |
| **Tipo de Ejecución**    | Aplicación Spring Boot con servidor Tomcat embebido      |

### 3.2. Requisitos Mínimos

| Componente         | Versión Requerida |
| ------------------ | ----------------- |
| Java               | 21 LTS            |
| Spring Boot        | 3.5.10            |
| Tomcat (embebido)  | 10.1.x            |
| Maven              | 3.9 o superior    |
| Puerto por Defecto | 8080              |

### 3.3. Configuración Recomendada

La aplicación utiliza **variables de entorno** para establecer la conexión con la base de datos PostgreSQL. Estas variables deben definirse antes de ejecutar el servicio.

#### Variables Requeridas

| Variable              | Descripción                                     |
| --------------------- | ----------------------------------------------- |
| **DB_URL**            | Cadena de conexión JDBC a la base de datos      |
| **DB_USER**           | Usuario con permisos sobre la base de datos     |
| **DB_PASS**           | Contraseña asociada al usuario de base de datos |
| **JAVA_TOOL_OPTIONS** | Parámetros mínimos de la JVM (memoria)          |

**Formato Esperado:**

```
DB_URL=jdbc:postgresql://host:5432/duit_db
DB_USER=duit_user
DB_PASS=********
JAVA_TOOL_OPTIONS=-Xmx512m -Xms256m
```

#### Ubicación de Definición

- **En entorno local**: Mediante exportación en la terminal
- **En entorno cloud**: Desde el panel de configuración del proveedor (Koyeb)

> **Nota**: Las credenciales NO se almacenan en el repositorio por motivos de seguridad.

#### Configuración Mínima Recomendada

| Parámetro                   | Valor                                   |
| --------------------------- | --------------------------------------- |
| **Memoria disponible**      | 512 MB de RAM como mínimo               |
| **Tiempo máximo de sesión** | 30 minutos                              |
| **Puerto de ejecución**     | 8080 (valor por defecto de Spring Boot) |

#### Ejecución Manual de la Aplicación

En entorno local, una vez definidas las variables de entorno necesarias, la aplicación puede iniciarse mediante:

```bash
java -jar target/duit-0.0.1-SNAPSHOT.jar
```

Este comando ejecuta el artefacto generado por Maven utilizando el servidor Tomcat embebido incluido en Spring Boot.

### 3.4. Gestión del Entorno Cloud (Koyeb)

#### ¿Qué es Koyeb?

**Koyeb** es una plataforma **PaaS** (Platform as a Service) que permite desplegar aplicaciones sin necesidad de administrar directamente servidores físicos o máquinas virtuales.

#### Funciones Principales

| Función                          | Descripción                                    |
| -------------------------------- | ---------------------------------------------- |
| **Aprovisionamiento Automático** | Asignación automática de CPU y memoria         |
| **Entorno Aislado**              | Ejecución en contenedor separado               |
| **URL Pública**                  | Asignación de dominio accesible desde Internet |
| **Reinicio Automático**          | Reinicio del servicio en caso de fallo         |
| **Logs y Monitorización**        | Panel de control con registros y métricas      |
| **Variables de Entorno**         | Configuración de variables necesarias          |

#### Modelo PaaS Utilizado

El modelo PaaS permite:

- **Delegar al proveedor**: Gestión de infraestructura (SO, red, disponibilidad)
- **Responsabilidad del equipo**: Artefacto generado y su configuración
- **Resultado**: Desarrollo más rápido, operaciones simplificadas


> **Nota**: En ambas opciones (GitHub y JAR) debes crear primero la base de datos en Neon para que JPA se sincronice automáticamente. Se recomienda definir las credenciales en un archivo `.env` (o variables de entorno del panel) y usar `SPRING_JPA_HIBERNATE_DDL_AUTO=update`.

#### 3.4.1. Tipos de Despliegue en Koyeb

El despliegue puede realizarse mediante dos métodos:

| Método                        | Características                                                                         | Uso Recomendado                  |
| ----------------------------- | --------------------------------------------------------------------------------------- | -------------------------------- |
| **Despliegue desde GitHub**   | Conexión directa al repositorio, compilación automática en Koyeb, despliegues continuos | Ciclos de desarrollo activos     |
| **Despliegue Manual con JAR** | Subida directa del JAR compilado, menos automatizado, más control                       | Versiones específicas congeladas |

#### 3.4.2. Opción 1: Despliegue mediante Conexión a GitHub (RECOMENDADO)




##### Paso 1: Preparar el Repositorio GitHub

1. Asegúrate de que el código se encuentra actualizado en la rama main:

```bash
git status
git add .
git commit -m "Preparar para despliegue a Koyeb"
git push origin main
```

1. Verifica que el repositorio sea **public** o que **Koyeb tenga permisos** de acceso

2. Confirma que el archivo **pom.xml** está presente en la raíz del proyecto con:
   - GroupId: `es.duit`
   - ArtifactId: `duit`
   - Version: `0.0.1-SNAPSHOT`
   - Java version: `21`
   - Spring Boot parent: `3.5.10`

3. En el panel de Koyeb, añade las variables de entorno:

```
DB_URL=jdbc:postgresql://<HOST_NEON>:5432/duit_db
DB_USER=<USUARIO_NEON>
DB_PASS=<CONTRASEÑA_SEGURA_NEON>
```


##### Paso 2: Iniciar el Despliegue

1. Revisa la configuración y haz clic en **"Deploy"**

2. Koyeb comenzará el proceso de construcción:
   - Clona el repositorio desde GitHub
   - Ejecuta Maven para compilar la aplicación
   - Genera el archivo JAR
   - Inicia la aplicación en un contenedor
   - Expone la aplicación mediante una URL pública

3. Esto puede tardar **3-5 minutos** la primera vez. Monitoriza el progreso en el panel de Koyeb.

##### Paso 3: Verificar el Despliegue

1. Una vez completado, Koyeb mostrará:
   - Un indicador de estado **"Running"** (verde)
   - Una URL pública en formato: `https://duitapp.koyeb.app/` o similar

2. Accede a la URL pública desde tu navegador para verificar que la aplicación está funcionando

3. Prueba las funcionalidades críticas:
   - **Página de login**: `https://tuapp.koyeb.app/login`
   - **Registro de usuario**: Debe funcionar sin errores
   - **Acceso a dashboard**: Verifica que la sesión se mantiene
  

#### 3.4.2.1. Despliegues Continuos

Una vez configurada la integración GitHub-Koyeb, cualquier push a la rama main disparará un nuevo despliegue automático:

1. Realiza cambios en local

2. Haz un commit y push a la rama main:

```bash
git add .
git commit -m "Cambios para producción"
git push origin main
```

1. Koyeb detectará automáticamente el cambio y ejecutará:
   - Clonación del repositorio
   - Compilación con Maven
   - Inicio de la nueva versión

2. El despliegue anterior se sustituye sin tiempo de downtime (en la mayoría de casos)

**Para deshabilitar despliegues automáticos:**

- Accede a la configuración del servicio en Koyeb
- Desactiva "Auto-deploy on git push"

#### 3.4.3. Opción 2: Despliegue Manual con Archivo JAR

##### Paso 1: Compilar la Aplicación Localmente

1. En tu entorno local, compila la aplicación:

```bash
cd /home/qiu/Proyectos/Duit
./mvnw clean package -DskipTests
```

1. Verifica que el archivo JAR se ha generado en:

```
target/duit-0.0.1-SNAPSHOT.jar
```

##### Paso 2: Preparar para Carga en Koyeb

1. Sube el JAR `target/duit-0.0.1-SNAPSHOT.jar` a Koyeb desde el panel (opción de carga directa de archivo).

##### Paso 3: Crear Servicio con JAR

1. En el dashboard de Koyeb:
   - Selecciona la opción de carga de **JAR**
   - Configura las variables de entorno (DB_URL, DB_USER, DB_PASS)
   - Completa la configuración tal como se describe en los Pasos 3-4 de la Opción 1

#### 3.4.4. Configuración de Despliegue 

| Parámetro                 | Valor                                       |
| ------------------------- | ------------------------------------------- |
| **Runtime**               | Java                                        |
| **Java Version**          | 21 LTS                                      |
| **Build Tool**            | Maven (automático)                          |
| **Build Command**         | `mvn clean package -DskipTests`             |
| **Port**                  | 8080                                        |
| **Memory**                | 512 MB                                      |
| **CPU**                   | 1 vCPU                                      |
| **Environment Variables** | DB_URL, DB_USER, DB_PASS, JAVA_TOOL_OPTIONS |




---

## 4. Servidor de Base de Datos

### 4.1. Identificación del Servidor

| Parámetro                      | Valor                                 |
| ------------------------------ | ------------------------------------- |
| **Motor de Base de Datos**     | PostgreSQL                            |
| **Versión Recomendada**        | 15.x                                  |
| **Proveedor del Servicio**     | Neon (servicio gestionado en la nube) |
| **Esquema Utilizado**          | public                                |
| **Nombre de la Base de Datos** | duit_db                               |

### 4.2. Requisitos Mínimos

| Parámetro                    | Valor           |
| ---------------------------- | --------------- |
| **Puerto por Defecto**       | 5432            |
| **Driver JDBC**              | 42.7.x          |
| **Codificación Recomendada** | UTF-8           |
| **Pool de Conexiones**       | HikariCP        |
| **SSL requerido**            | sslmode=require |

### 4.3. Scripts

Aunque la aplicación puede generar el esquema automáticamente mediante JPA (`ddl-auto=update`), se entrega adicionalmente un script SQL completo con el fin de garantizar la reproducibilidad del despliegue en cualquier entorno.

**Script principal**: `documentacion/schema.sql`

**Comandos mínimos en Neon (SQL Editor):**

```sql
CREATE DATABASE duit_db;
CREATE USER duit_user WITH PASSWORD 'TU_PASSWORD_SEGURA';
GRANT ALL PRIVILEGES ON DATABASE duit_db TO duit_user;
```

**Carga del esquema:**

1. Abrir el SQL Editor de Neon conectado a `duit_db`.
2. Pegar el contenido de `documentacion/schema.sql`.
3. Ejecutar el script completo.


#### Generación del Archivo schema.sql mediante DBeaver

El archivo `schema.sql` fue generado mediante **DBeaver Community** conectado a la base de datos PostgreSQL en **Neon**. Este método permite obtener un script SQL completo con toda la estructura de la base de datos.


**Ventajas de este enfoque:**

- **Reproducibilidad**: El schema.sql permite recrear la base de datos en cualquier entorno PostgreSQL
- **Documentación**: Sirve como referencia de la estructura de datos del proyecto
- **Despliegue sin JPA**: Permite desplegar en entornos donde no se desea usar `ddl-auto=update`
- **Control de versiones**: El archivo SQL puede versionarse en Git para rastrear cambios en la estructura

> **Recomendación**: Regenera el `schema.sql` cada vez que se realicen cambios significativos en las entidades JPA (nuevas tablas, columnas, restricciones).



### 4.4. Gestión del Servicio de Base de Datos (Neon)

#### ¿Qué es Neon?

**Neon** es un servicio de base de datos **PostgreSQL** gestionado en la nube (Database as a Service – DBaaS). Este modelo elimina la necesidad de instalar, configurar y mantener manualmente un servidor de base de datos.

#### Servicios Proporcionados por Neon

| Servicio                       | Descripción                               |
| ------------------------------ | ----------------------------------------- |
| **Instancia gestionada**       | PostgreSQL 15.x automáticamente mantenido |
| **Almacenamiento persistente** | En la nube con redundancia                |
| **Conexión segura**            | SSL/TLS automatizado                      |
| **Cadena de conexión JDBC**    | Generada automáticamente                  |
| **Panel web**                  | Interfaz intuitiva para administración    |
| **Backups automáticos**        | Con opción de extensión                   |
| **Monitorización**             | Uso de recursos y logs integrados         |

#### Ventajas del Modelo DBaaS con Neon

| Ventaja                      | Descripción                                            |
| ---------------------------- | ------------------------------------------------------ |
| **Sin administración**       | Neon gestiona servidores, almacenamiento y replicación |
| **Escalabilidad automática** | Aumenta recursos sin intervención manual               |
| **Seguridad empresarial**    | Encriptación en tránsito (SSL/TLS) y en reposo         |
| **Backups automáticos**      | Retención de 7 días con opción a extensión             |
| **Acceso web**               | Gestor SQL integrado sin herramientas adicionales      |
| **Facturación por uso**      | Solo pagas por lo que consumes                         |

#### 4.4.1 Despliegue en Neon desde el proyecto

Si ya tienes el proyecto creado en Neon, estos son los pasos para dejarlo listo y conectar la app:

1. Entra al proyecto en Neon y abre **Connection String**.
2. Selecciona la base de datos `duit_db` (o crea una nueva con ese nombre).
3. Crea o selecciona el usuario `duit_user` con una contraseña segura.
4. Copia la cadena de conexión y extrae estos valores:
   - **DB_URL**: `jdbc:postgresql://ep-xxxxx-xxx.us-east-1.aws.neon.tech:5432/duit_db`
   - **DB_USER**: `duit_user`
   - **DB_PASS**: `tu_contraseña_segura_aqui`
5. Configura las variables de entorno en Koyeb o en local.
6. Arranca la aplicación con `SPRING_JPA_HIBERNATE_DDL_AUTO=update` si quieres que JPA genere el esquema automáticamente.



#### 4.4.2 Límites y Limitaciones de Neon (plan gratuito)

| Característica             | Límite                                          |
| -------------------------- | ----------------------------------------------- |
| **Almacenamiento**         | 0.5 GB de crédito gratuito (~2.5 GB de datos)   |
| **Conexiones simultáneas** | Ilimitadas (máximo 20 recomendado con HikariCP) |
| **Backups**                | 7 días de retención                             |
| **CPU**                    | Compartida, suficiente para desarrollo          |
| **Memoria**                | 1 GB compartida                                 |


---

## 5. Ficheros o Binarios a Desplegar

### 5.1. Ubicación

El artefacto generado tras la compilación del proyecto es:

```
target/duit-0.0.1-SNAPSHOT.jar
```

Este archivo debe incluirse dentro del paquete comprimido:

```
DAW_PRW_R1L2_5_cod.zip
```

#### Contenido del Paquete de Entrega

El archivo comprimido deberá contener:

- Archivo `.jar` compilado de la aplicación
- Script de base de datos (`schema.sql`)
- Archivo de configuración de ejemplo (`.env`)
- Documento de despliegue en formato PDF

**Verificación previa**:

- Comprobar que el `.jar` y `schema.sql` no están vacíos ni corruptos
- Verificar que el `.env` de ejemplo contiene `DB_URL`, `DB_USER`, `DB_PASS`

#### Repositorio Remoto

El código fuente completo del proyecto se encuentra disponible en:

**[https://github.com/FerCueA/Duit](https://github.com/FerCueA/Duit)**

---

## 6. Operativa

### 6.1. Operaciones de Base de Datos

Para preparar el entorno de base de datos en Neon, deben realizarse los siguientes pasos:

#### Modo rápido con JPA (sin scripts manuales)

Si quieres gestionarlo todo desde JPA, basta con configurar la conexión y arrancar la aplicación. JPA generará el esquema automáticamente.

1. Obtén `DB_URL`, `DB_USER` y `DB_PASS` desde el proyecto en Neon (ver [4.4.1](#441-despliegue-en-neon-desde-el-proyecto)).
   - Estos valores son parametrizables y deben adaptarse a tu entorno.
2. Define variables de entorno:

```
DB_URL=jdbc:postgresql://<HOST_NEON>:5432/duit_db
DB_USER=duit_user
DB_PASS=TU_PASSWORD_SEGURA
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

3. Inicia la aplicación. JPA creará tablas, índices y restricciones sin pasos adicionales.


### 6.2. Operaciones de Despliegue

El despliegue de la aplicación en Koyeb puede realizarse mediante dos métodos.
En este se describe el método utilizando la integración directa con GitHub, que es el recomendado para facilitar el desarrollo continuo y la gestión de versiones.

#### Método 1: Despliegue Automático desde GitHub (RECOMENDADO)


#### Paso 1: Accede a Koyeb

- Dirígete a [https://www.koyeb.com/](https://www.koyeb.com/) e inicia sesión
- Haz clic en "Create Service"

#### Paso 2: Conecta tu Repositorio GitHub

- Selecciona GitHub como fuente
- Autoriza el acceso (si es la primera vez)
- Elige el repositorio "Duit" y la rama "main"

#### Paso 3: Configura el Entorno

| Parámetro   | Valor       |
| ----------- | ----------- |
| **Runtime** | Java 21 LTS |
| **Port**    | 8080        |
| **Memory**  | 512 MB      |
| **CPU**     | 1 vCPU      |

#### Paso 4: Añade Variables de Entorno

Copia estas tres líneas y reemplaza los valores con tus credenciales de Neon:

> **Parametrización**: usa valores distintos para entornos de desarrollo y producción.

```
DB_URL=jdbc:postgresql://ep-xxx.us-east-1.aws.neon.tech:5432/duit_db
DB_USER=duit_user
DB_PASS=tu_contraseña_aqui
```

#### Paso 5: Despliega

- Haz clic en "Deploy"
- Espera 3-5 minutos
- Koyeb te mostrará una URL pública
- Accede a ella y verifica que todo funciona

**Actualizaciones futuras** - Simplemente haz push a la rama main:

```bash
git add .
git commit -m "Nueva feature"
git push origin main
```

Koyeb detectará el cambio y desplegará automáticamente.

---
#### Verificación Post-Despliegue

Después de completar cualquier método:

1. Accede a la URL pública proporcionada por Koyeb (ej: [https://duitapp.koyeb.app/](https://duitapp.koyeb.app/))
2. Verifica que la página de login carga sin errores
3. Comprueba los logs en Koyeb para detectar posibles errores de BD
4. Intenta registrar un usuario y verificar que los datos se guardan correctamente

### 6.3. Condiciones y Verificación de Éxito

El despliegue se considerará satisfactorio cuando se cumplan las siguientes condiciones:

#### 1. Accesibilidad

- La aplicación responde correctamente desde el dominio público asignado
- No hay errores de timeout o conexión rechazada

#### 2. Funcionalidad de Autenticación

- Es posible acceder a `/login` sin errores
- Se permite el registro de un nuevo usuario
- El usuario puede iniciar sesión correctamente con sus credenciales

#### 3. Funcionalidad de Negocio

- Se pueden crear solicitudes de servicio
- Los profesionales pueden postularse a solicitudes existentes
- Es posible aceptar una postulación y crear un trabajo asociado
- Se pueden asignar valoraciones entre usuarios

#### 4. Integridad Técnica

- No se registran errores HTTP 500 en los logs del servidor
- No hay excepciones de conexión a la BD en los logs
- La aplicación responde con tiempos de carga aceptables (< 3 segundos por página)
- No hay errores de JavaScript en la consola del navegador

#### 5. Persistencia de Datos

- Los datos ingresados persisten correctamente en Neon
- Las sesiones de usuario se mantienen activas durante el tiempo configurado
- No hay pérdida de datos tras un reinicio de la aplicación

---

## 7. Control de Versiones

El código fuente del proyecto se encuentra en el repositorio oficial:

**[https://github.com/FerCueA/Duit](https://github.com/FerCueA/Duit)**

Historial de commits disponible en la pestaña **Commits** del repositorio.



**Verificacion**:

- Enlace accesible desde navegador.
- Mensajes de commit descriptivos y distribuidos por hitos.

---

## 8. Observaciones

- El entorno actual utiliza **planes gratuitos** de Koyeb y Neon, por lo que pueden existir limitaciones de recursos comparado con entornos empresariales
- Se recomienda realizar **copias de seguridad periódicas** de la BD para garantizar integridad
- Se emplea **DBeaver** como herramienta de administración y generación de backups
- Las copias de seguridad se exportan en formato `.sql`, permitiendo restauración en cualquier PostgreSQL compatible
- **Credenciales y datos sensibles NO se almacenan** en el repositorio – se usan variables de entorno
- La arquitectura permite futuras ampliaciones:
  - Adopción de microservicios
  - Integración de caché (Redis)
  - Incorporación de pasarela de pago
- Para cambios en dominio o certificado SSL, contacta con el equipo de Koyeb a través del panel de soporte
- Se recomienda **configurar alertas de monitorización** en Koyeb para detectar caídas o uso excesivo de recursos
- Despliegue probado en un entorno limpio antes de la entrega (VM/PC alterno)
- Revision final: ortografia, formato PDF, nombre del archivo y contenido del ZIP verificados

---

## 9. Reparto de Tareas y Responsabilidades

El desarrollo del despliegue y de la versión actual de la aplicación Duit se ha realizado de forma colaborativa entre ambos integrantes del equipo. Las responsabilidades se han distribuido atendiendo a las áreas de especialización y a la carga de trabajo asumida por cada miembro.

### Aleixo Fernández Cuevas

**Responsable principal del desarrollo global de la aplicación y de la coordinación técnica del proyecto.**

**Tareas realizadas:**

| Área              | Tareas                                                                                                                                                     |
| ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Frontend**      | Diseño y desarrollo completo del frontend con HTML5, CSS3 y Bootstrap; Maquetación de vistas; Implementación de formularios y validaciones visuales        |
| **Backend**       | Implementación de la mayor parte del backend; Desarrollo de controladores MVC; Implementación de servicios y lógica de negocio; Integración con PostgreSQL |
| **Despliegue**    | Configuración y ejecución de despliegue en Koyeb y Neon; Configuración de variables de entorno; Establecimiento de conexión segura con BD                  |
| **Gestión**       | Gestión del repositorio Git; Control de versiones; Coordinación general del proyecto                                                                       |
| **Documentación** | Redacción principal; Revisión final antes de despliegue en producción                                                                                      |

### Cristo Manuel Navarro Martín

**Responsable de áreas específicas relacionadas con la seguridad y módulos concretos del backend.**

**Tareas realizadas:**

| Área          | Tareas                                                                                                                                                                              |
| ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Seguridad** | Implementación y configuración de Spring Security; Sistema de autenticación; Control de acceso basado en roles; Configuración de login, logout y remember-me; Integración de BCrypt |
| **Backend**   | Desarrollo del módulo de postulaciones; Implementación de edición de perfil; Desarrollo de guía de estilos                                                                          |
| **Frontend**  | Maquetación e implementación de header y footer globales                                                                                                                            |
| **Testing**   | Apoyo en pruebas funcionales; Validación del sistema antes del despliegue                                                                                                           |

### Coordinación y Trabajo en Equipo

Ambos integrantes han participado conjuntamente en:

- Definición de los requisitos tecnológicos y funcionales
- Desarrollo parcial del backend con Spring Boot y Java 21
- Creación y adaptación de vistas dinámicas mediante Thymeleaf
- Toma de decisiones tecnológicas
- Validación de reglas de negocio
- Revisión final previa al despliegue en producción

---

**Última actualización:** 16 de febrero de 2026 
**Versión:** 1.0
