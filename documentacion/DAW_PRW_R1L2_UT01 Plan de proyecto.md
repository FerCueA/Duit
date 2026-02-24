# Plan de Proyecto - DUIT

**Plataforma Web de Conexión entre Clientes y Profesionales de Servicios**

---

## Metadatos del Documento

| Dato                  | Valor                                                  |
| --------------------- | ------------------------------------------------------ |
| **Autores**           | Aleixo Fernández Cuevas, Cristo Manuel Navarro Martín  |
| **Nombre de archivo** | DAW_PRW_R1L2_UT01 Plan de proyecto                     |
| **Fecha de versión**  | 24/02/2026                                             |
| **Ciclo Formativo**   | Desarrollo de Aplicaciones Web - Semipresencial (DAWN) |
| **Módulo**            | Proyecto de Desarrollo de Aplicaciones Web (PRW)       |
| **Versión**           | 3.0                                                    |

---

## Tabla de Contenidos

- [Plan de Proyecto - DUIT](#plan-de-proyecto---duit)
  - [Metadatos del Documento](#metadatos-del-documento)
  - [Tabla de Contenidos](#tabla-de-contenidos)
  - [Historial de Revisiones](#historial-de-revisiones)
  - [1. Resumen Ejecutivo](#1-resumen-ejecutivo)
  - [2. Alcance del Proyecto](#2-alcance-del-proyecto)
    - [2.1 Objetivo Principal](#21-objetivo-principal)
    - [2.2 Objetivos Específicos](#22-objetivos-específicos)
    - [2.3 Alcance Incluido](#23-alcance-incluido)
      - [Módulo de Usuarios](#módulo-de-usuarios)
      - [Módulo de Solicitudes](#módulo-de-solicitudes)
      - [Módulo de Postulaciones](#módulo-de-postulaciones)
      - [Módulo de Trabajos](#módulo-de-trabajos)
      - [Módulo de Valoraciones](#módulo-de-valoraciones)
      - [Panel de Administración](#panel-de-administración)
      - [Seguridad y Auditoría](#seguridad-y-auditoría)
    - [2.4 Alcance Excluido](#24-alcance-excluido)
  - [3. Requisitos del Proyecto](#3-requisitos-del-proyecto)
    - [3.1 Requisitos Funcionales](#31-requisitos-funcionales)
    - [3.2 Requisitos No Funcionales](#32-requisitos-no-funcionales)
    - [3.3 Requisitos Técnicos](#33-requisitos-técnicos)
  - [4. Estructura de Trabajo y Cronograma](#4-estructura-de-trabajo-y-cronograma)
    - [4.1 Fases del Proyecto](#41-fases-del-proyecto)
      - [Futuras Ampliaciones (No en Scope Actual)](#futuras-ampliaciones-no-en-scope-actual)
  - [5. Recursos y Roles del Equipo](#5-recursos-y-roles-del-equipo)
    - [5.1 Recursos Humanos](#51-recursos-humanos)
    - [5.2 Recursos Técnicos](#52-recursos-técnicos)
      - [Herramientas de Desarrollo](#herramientas-de-desarrollo)
      - [Infraestructura Cloud](#infraestructura-cloud)
  - [6. Gestión de Riesgos](#6-gestión-de-riesgos)
    - [6.1 Matriz de Riesgos](#61-matriz-de-riesgos)
      - [Riesgos Técnicos](#riesgos-técnicos)
      - [Riesgos de Proyecto](#riesgos-de-proyecto)
      - [Riesgos Externos](#riesgos-externos)
    - [6.2 Plan de Mitigación](#62-plan-de-mitigación)
      - [RT-01: Problemas de Compatibilidad](#rt-01-problemas-de-compatibilidad)
      - [RT-02: Bugs Críticos en Producción](#rt-02-bugs-críticos-en-producción)
      - [RT-03: Caída de Servicios Cloud](#rt-03-caída-de-servicios-cloud)
      - [RT-04: Vulnerabilidades de Seguridad](#rt-04-vulnerabilidades-de-seguridad)
      - [RT-05: Pérdida de Datos](#rt-05-pérdida-de-datos)
      - [RP-02: Scope Creep](#rp-02-scope-creep)
      - [RP-03: Falta de Tiempo para Testing](#rp-03-falta-de-tiempo-para-testing)
  - [7. Conclusión](#7-conclusión)
  - [8. Reparto de Tareas y Responsabilidades](#8-reparto-de-tareas-y-responsabilidades)

---

## Historial de Revisiones

| Fecha      | Descripción                                 | Autores                             |
| ---------- | ------------------------------------------- | ----------------------------------- |
| 21/12/2025 | Fase de análisis. Versión inicial           | Aleixo F. Cuevas / Cristo N. Martín |
| 10/01/2026 | Primera corrección                          | Aleixo F. Cuevas / Cristo N. Martín |
| 01/02/2026 | Segunda corrección                          | Aleixo F. Cuevas / Cristo N. Martín |
| 16/02/2026 | Tercera corrección y reformateo estructural | Aleixo F. Cuevas                    |
| 24/02/2026 | Cuarta corrección                           | Aleixo F. Cuevas / Cristo N. Martín |

---

## 1. Resumen Ejecutivo

**DUIT** es una plataforma web que conecta a usuarios que necesitan servicios profesionales con profesionales cualificados que pueden ofrecerlos. La aplicación gestiona el ciclo completo del servicio: desde la publicación de solicitudes, postulaciones de profesionales, contratación, ejecución del trabajo hasta la valoración final bidireccional.

El proyecto aplica conocimientos del ciclo formativo de Desarrollo de Aplicaciones Web en un caso real, utilizando tecnologías modernas como Spring Boot 3, Java 21, PostgreSQL y desplegando en infraestructura cloud.

---

## 2. Alcance del Proyecto

### 2.1 Objetivo Principal

Desarrollar una plataforma web funcional, segura y escalable que facilite la conexión entre clientes y profesionales de servicios, implementando el ciclo completo de gestión de servicios con sistema de valoraciones y transparencia.

### 2.2 Objetivos Específicos

1. **Objetivo Técnico:** Implementar una arquitectura MVC robusta con Spring Boot, JPA/Hibernate y PostgreSQL, siguiendo buenas prácticas de desarrollo.
2. **Objetivo Funcional:** Desarrollar las funcionalidades principales del sistema (registro y autenticación de usuarios, solicitudes, postulaciones, trabajos y valoraciones).
3. **Objetivo de Seguridad:** Garantizar la protección de datos mediante Spring Security, cifrado de contraseñas con BCrypt y control de acceso basado en roles.
4. **Objetivo de Usabilidad:** Crear una interfaz adaptable e intuitiva con Thymeleaf y Bootstrap que funcione en todos los dispositivos.
5. **Objetivo Académico:** Aplicar los conocimientos adquiridos en el ciclo formativo en un proyecto integral y profesional.
6. **Objetivo de Despliegue:** Publicar la aplicación en producción con infraestructura cloud (Koyeb + Neon) accesible públicamente.

### 2.3 Alcance Incluido

#### Módulo de Usuarios

- Registro y autenticación
- Perfiles de usuario y profesional
- Sistema de roles (USER/cliente, PROFESSIONAL/profesional, ADMIN/administrador)
- Rol MODERATOR reservado para evolución futura (no operativo en rutas actuales)
- Gestión de direcciones

#### Módulo de Solicitudes

- Creación y publicación de solicitudes
- Categorización de servicios
- Estados del ciclo de vida (Borrador, Publicada, En progreso, Completada, Cancelada, Expirada)
- Gestión de edición, publicación, despublicación, cancelación, reactivación y eliminación

#### Módulo de Postulaciones

- Búsqueda de solicitudes por profesionales
- Postulación con precio y mensaje
- Aceptación/rechazo por parte del cliente
- Gestión de estados (Pendiente, Aceptada, Rechazada, Retirada)
- Edición y retirada de postulaciones propias

#### Módulo de Trabajos

- Creación automática al aceptar postulación
- Seguimiento de trabajos activos
- Actualización de estados
- Vinculación con solicitud y postulación
- Actualización de estado (pausar, reanudar, completar, cancelar)

#### Módulo de Valoraciones

- Sistema bidireccional (cliente ↔ profesional)
- Puntuación de 1 a 5 estrellas
- Comentarios opcionales
- Estados (Pendiente, Publicada, Oculta)
- Creación y consulta de valoraciones

#### Panel de Administración

- Gestión de categorías (CRUD)
- Acceso a vistas base de usuarios y estadísticas (sin CRUD ni métricas cargadas)

#### Seguridad y Auditoría

- Control de acceso basado en roles (RBAC)
- Auditoría automática de cambios (created_by, created_at, updated_by, updated_at)
- Registro de accesos al sistema
- Cifrado de contraseñas con BCrypt
- Gestión de errores con páginas personalizadas (403, 404 y 500)

### 2.4 Alcance Excluido

- Sistema de pagos integrado (Stripe, PayPal)
- Chat en tiempo real o mensajería instantánea
- Aplicación móvil nativa (iOS/Android)
- Notificaciones push
- Monetización (panel de administrador)
- Panel de administración avanzado (CRUD de usuarios, bloqueos, roles y métricas reales)
- Integración de APIs externas (Google Maps, servicios de terceros)
- Revisión de valoraciones de los clientes/profesionales en las ofertas de servicios
- Sistema de verificación de cuentas por email
- Sistema de premium o suscripciones

---

## 3. Requisitos del Proyecto

### 3.1 Requisitos Funcionales

- Registro y autenticación de usuarios
- Creación y gestión de solicitudes
- Postulación de profesionales
- Gestión del estado del trabajo
- Valoración tras la finalización del servicio

### 3.2 Requisitos No Funcionales

- Seguridad de los datos
- Accesibilidad desde distintos dispositivos
- Rendimiento adecuado
- Código mantenible y documentado

### 3.3 Requisitos Técnicos

- Backend en Java 21 LTS
- Spring Boot 3.5.10
- Base de datos PostgreSQL 15.x
- Arquitectura MVC
- Despliegue en infraestructura cloud

---

## 4. Estructura de Trabajo y Cronograma

### 4.1 Fases del Proyecto

El proyecto DUIT se ha desarrollado siguiendo la metodología Kanban, basada en la gestión visual del trabajo y en un flujo continuo de tareas.

| Fase | Nombre                     | Descripción                                 | Herramientas     | Período             |
| ---- | -------------------------- | ------------------------------------------- | ---------------- | ------------------- |
| 1    | Análisis y Planificación   | Definición de alcance, requisitos y riesgos | Trello           | Dic 2025            |
| 2    | Diseño Funcional y Técnico | Prototipos y estructura del sistema         | Figma            | Dic 2025 - Ene 2026 |
| 3    | Desarrollo Backend         | Lógica de negocio y datos                   | VSC + Git        | Ene 2026            |
| 4    | Desarrollo Frontend        | Vistas y experiencia de usuario             | VSC + Git        | Ene 2026 - Feb 2026 |
| 5    | Pruebas y Validación       | Verificación funcional                      | Pruebas manuales | Feb 2026            |
| 6    | Documentación y Entrega    | Memoria y entrega final                     | Markdown         | Feb 2026            |



---

#### Futuras Ampliaciones (No en Scope Actual)

- Integración de pagos (Stripe/PayPal)
- Aplicación móvil (React Native)
- Chat en tiempo real (WebSocket)
- Notificaciones push
- Mapas y geolocalización avanzada
- Analytics predictivo
- API REST pública
- Sistemas de recomendación (ML)
- Sistema de verificación de cuentas por email
- Panel de control para profesionales (estadísticas, gestión de usuarios)
---

## 5. Recursos y Roles del Equipo

### 5.1 Recursos Humanos

El proyecto es desarrollado por un equipo de dos desarrolladores con perfil Full Stack, que participan activamente en todas las fases del proyecto.

| Recurso                      | Rol                  | Responsabilidades                                     | Dedicación | Período             |
| ---------------------------- | -------------------- | ----------------------------------------------------- | ---------- | ------------------- |
| Aleixo Fernández Cuevas      | Full Stack Developer | Arquitectura, backend, despliegue y documentación     | 100%       | Sep 2025 - Mar 2026 |
| Cristo Manuel Navarro Martín | Full Stack Developer | Frontend, integración, backend y pruebas   ,seguridad | 100%       | Sep 2025 - Mar 2026 |

### 5.2 Recursos Técnicos

#### Herramientas de Desarrollo

- Visual Studio Code (VSC)
- Git y GitHub
- PostgreSQL
- DBeaver
- Figma
- Trello
- Obsidian

#### Infraestructura Cloud

- **Hosting:** Koyeb (Free Tier)
- **Base de Datos:** Neon (Free Tier)

---

## 6. Gestión de Riesgos

### 6.1 Matriz de Riesgos

#### Riesgos Técnicos

| ID    | Riesgo                                                   | Probabilidad | Impacto | Severidad |
| ----- | -------------------------------------------------------- | ------------ | ------- | --------- |
| RT-01 | Problemas de compatibilidad entre versiones de librerías | Media        | Medio   | MEDIA     |
| RT-02 | Bugs críticos en producción                              | Media        | Alto    | ALTA      |
| RT-03 | Caída de servicios cloud gratuitos                       | Media        | Alto    | ALTA      |
| RT-04 | Vulnerabilidades de seguridad no detectadas              | Baja         | Alto    | MEDIA     |
| RT-05 | Pérdida de datos por fallo en BD                         | Baja         | Crítico | MEDIA     |

#### Riesgos de Proyecto

| ID    | Riesgo                                                | Probabilidad | Impacto | Severidad |
| ----- | ----------------------------------------------------- | ------------ | ------- | --------- |
| RP-01 | Retraso en el cronograma                              | Media        | Medio   | MEDIA     |
| RP-02 | Scope creep (ampliación incontrolada)                 | Alta         | Medio   | ALTA      |
| RP-03 | Falta de tiempo para testing exhaustivo               | Alta         | Medio   | ALTA      |
| RP-04 | Cambios en requisitos a mitad de desarrollo           | Media        | Medio   | MEDIA     |
| RP-05 | Problemas de salud o disponibilidad del desarrollador | Baja         | Alto    | MEDIA     |

#### Riesgos Externos

| ID    | Riesgo                                  | Probabilidad | Impacto | Severidad |
| ----- | --------------------------------------- | ------------ | ------- | --------- |
| RE-01 | Cambios en políticas de servicios cloud | Baja         | Medio   | BAJA      |
| RE-02 | Deprecación de tecnologías utilizadas   | Baja         | Medio   | BAJA      |
| RE-03 | Limitaciones de tier gratuito superadas | Media        | Medio   | MEDIA     |

### 6.2 Plan de Mitigación

#### RT-01: Problemas de Compatibilidad

- **Prevención:** Usar Spring Initializr para garantizar compatibilidad inicial
- **Mitigación:** Mantener dependencias actualizadas, revisar breaking changes
- **Contingencia:** Tener versiones de respaldo documentadas

#### RT-02: Bugs Críticos en Producción

- **Prevención:** Testing exhaustivo antes de despliegue
- **Mitigación:** Sistema de rollback rápido, logs detallados
- **Contingencia:** Hotfix inmediato, comunicación a usuarios

#### RT-03: Caída de Servicios Cloud

- **Prevención:** Backups regulares de BD
- **Mitigación:** Monitoreo de uptime, plan de migración documentado
- **Contingencia:** Servicios alternativos identificados (Render, Railway)

#### RT-04: Vulnerabilidades de Seguridad

- **Prevención:** Usar frameworks actualizados, validaciones en servidor
- **Mitigación:** Revisión de seguridad, dependencias actualizadas
- **Contingencia:** Parche de seguridad urgente

#### RT-05: Pérdida de Datos

- **Prevención:** Backups automáticos de Neon
- **Mitigación:** Backups manuales periódicos
- **Contingencia:** Restauración desde último backup

#### RP-02: Scope Creep

- **Prevención:** Documento de alcance claro, priorización estricta
- **Mitigación:** Revisión semanal de scope, registro de cambios
- **Contingencia:** Funcionalidades adicionales movidas a roadmap futuro

#### RP-03: Falta de Tiempo para Testing

- **Prevención:** Testing continuo durante desarrollo
- **Mitigación:** Priorización de tests críticos
- **Contingencia:** Testing post-entrega, versión 1.1

---

## 7. Conclusión

Este documento de plan de proyecto describe los objetivos, el alcance, los requisitos y la planificación del desarrollo de la aplicación web DUIT. Su objetivo es servir como guía durante el desarrollo del proyecto y ayudar a organizar el trabajo a lo largo del módulo de Proyecto Integrado del ciclo formativo de Desarrollo de Aplicaciones Web.

Definir desde el inicio los roles, las funcionalidades y los límites del sistema permite evitar errores y problemas durante el desarrollo, además de facilitar una mejor organización de las tareas. El uso de herramientas de planificación y diseño (Trello, Figma, Git) ha ayudado a que el proyecto avance de forma más ordenada, aunque no formen parte directa de las funcionalidades de la aplicación.

---

## 8. Reparto de Tareas y Responsabilidades

La elaboración del presente Documento de Plan de Proyecto se ha realizado de forma colaborativa por los dos miembros del equipo, participando ambos en la definición, redacción y revisión de su contenido.

**Aleixo Fernández Cuevas:**

- Elaboración del primer borrador del documento
- Definición de la estructura general
- Redacción de los apartados de alcance, planificación y descripción del sistema
- Revisión final del contenido e incorporación de correcciones y mejoras

**Cristo Manuel Navarro Martín:**

- Colaboración en la redacción de los apartados técnicos
- Especialmente los relacionados con seguridad, requisitos técnicos y gestión de riesgos
- Revisión del documento y aportación de mejoras sobre el contenido inicial

---

**Última actualización:** 24 de febrero de 2026 
**Versión:** 3.0
