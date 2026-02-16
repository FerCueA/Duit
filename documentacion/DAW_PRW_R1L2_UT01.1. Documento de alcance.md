# Documento de Alcance - DUIT

**Descripción del Alcance, Objetivos y Requisitos del Proyecto**

---

## 📋 Metadatos del Documento

| Dato                  | Valor                                                  |
| --------------------- | ------------------------------------------------------ |
| **Autores**           | Aleixo Fernández Cuevas, Cristo Manuel Navarro Martín  |
| **Nombre de archivo** | DAW_PRW_R1L2_UT01.1 Documento de alcance               |
| **Fecha de versión**  | 16/02/2026                                             |
| **Ciclo Formativo**   | Desarrollo de Aplicaciones Web - Semipresencial (DAWN) |
| **Módulo**            | Proyecto de Desarrollo de Aplicaciones Web (PRW)       |
| **Versión**           | 3.0                                                    |

---

## 📖 Tabla de Contenidos

1. [Introducción](#1-introducción)
2. [Justificación y Necesidades del Negocio](#2-justificación-y-necesidades-del-negocio)
3. [Objetivos del Proyecto](#3-objetivos-del-proyecto)
4. [Roles de Usuario](#4-roles-de-usuario)
5. [Descripción General del Sistema](#5-descripción-general-del-sistema)
6. [Requisitos Principales](#6-requisitos-principales)
7. [Criterios de Aceptación](#7-criterios-de-aceptación)
8. [Reparto de Tareas y Responsabilidades](#8-reparto-de-tareas-y-responsabilidades)

---

## 📝 Historial de Revisiones

| Fecha      | Descripción                              | Autores                             |
| ---------- | ---------------------------------------- | ----------------------------------- |
| 21/12/2025 | Fase de análisis. Versión inicial        | Aleixo F. Cuevas / Cristo N. Martín |
| 10/01/2026 | Primera corrección                       | Aleixo F. Cuevas / Cristo N. Martín |
| 01/02/2026 | Segunda corrección                       | Aleixo F. Cuevas / Cristo N. Martín |
| 16/02/2026 | Tercera corrección y reformateo completo | Aleixo F. Cuevas                    |

---

## 1. Introducción

El presente documento describe el alcance del proyecto DUIT, una plataforma web para la gestión de servicios entre clientes y profesionales. Este documento define los límites del proyecto, sus objetivos, los requisitos funcionales y no funcionales, así como los criterios para validar que el proyecto cumple con los objetivos establecidos.

---

## 2. Justificación y Necesidades del Negocio

### 2.1 Análisis del Mercado Actual

El mercado de servicios profesionales locales presenta una fragmentación significativa que impide la eficiencia en la conexión entre oferta y demanda. Las plataformas existentes suelen ser:

- **Generalistas y poco especializadas:** No atienden las necesidades específicas de servicios técnicos locales.
- **Costosas para los profesionales:** Con comisiones elevadas que reducen la rentabilidad.
- **Complejas en su uso:** Requieren procesos largos de verificación y gestión.

### 2.2 Necesidades Identificadas

#### Para los Clientes (Solicitantes de Servicios)

- Acceso inmediato a profesionales verificados en su área geográfica
- Sistema de valoraciones y referencias que garantice la calidad del servicio
- Gestión centralizada de todas sus solicitudes de servicio
- Transparencia en el proceso desde la solicitud hasta la finalización
- Interfaz intuitiva que no requiera conocimientos técnicos avanzados

#### Para los Profesionales (Prestadores de Servicios)

- Plataforma sin comisiones elevadas que maximice sus ingresos
- Control total sobre su disponibilidad y tipos de trabajo que aceptan
- Herramientas de gestión profesional para administrar clientes y trabajos
- Sistema de construcción de reputación basado en valoraciones reales
- Acceso móvil para gestionar solicitudes desde cualquier ubicación

### 2.3 Propuesta de Valor

DUIT se plantea como una plataforma que facilita la conexión entre clientes y profesionales de forma sencilla, segura y organizada. Su principal valor reside en ofrecer un entorno único donde se puede gestionar todo el ciclo del servicio, desde la publicación de una necesidad hasta la finalización del trabajo y la valoración final.

La aplicación aporta valor mediante:

- **Centralización del proceso:** Clientes y profesionales gestionan solicitudes, postulaciones y trabajos en un único sistema.
- **Control y transparencia:** El seguimiento del estado de los trabajos y el sistema de valoraciones generan confianza.
- **Acceso seguro y controlado:** La autenticación es obligatoria.
- **Facilidad de uso:** La interfaz está pensada para que cualquier usuario pueda utilizarla sin conocimientos tecnológicos avanzados.
- **Base sólida para crecimiento:** El sistema está diseñado de forma modular, permitiendo la incorporación de nuevas funcionalidades.

---

## 3. Objetivos del Proyecto

### 3.1 Objetivo General

Desarrollar e implementar una plataforma web integral que facilite la conexión eficiente entre clientes que requieren servicios específicos y profesionales cualificados, proporcionando un ecosistema digital completo para la gestión del ciclo completo de servicios profesionales locales.

### 3.2 Objetivos Específicos

#### Objetivos Funcionales

**Gestión Avanzada de Usuarios:**

- Implementar sistema de registro con validaciones básicas de datos
- Desarrollar autenticación segura con Spring Security
- Crear perfiles diferenciados (Cliente, Profesional, Administrador)
- Permitir el uso de direcciones asociadas a las solicitudes

**Sistema de Solicitudes de Servicios:**

- Permitir creación de solicitudes con categorización específica
- Implementar sistema de estados (DRAFT, PUBLISHED, IN_PROGRESS, COMPLETED, CANCELLED, EXPIRED)
- Permitir definir fecha límite de la solicitud
- Proporcionar herramientas de búsqueda y filtrado avanzado

**Gestión de Postulaciones y Asignaciones:**

- Permitir a profesionales postularse a solicitudes relevantes
- Implementar sistema de selección de profesionales por parte de clientes
- Gestionar el seguimiento del progreso de trabajos asignados

**Sistema de Valoraciones y Reputación:**

- Implementar valoraciones bidireccionales (cliente-profesional)
- Generar perfiles de reputación basados en historial de trabajos
- Preparar el modelo para métricas de reputación (plan futuro)

#### Objetivos Técnicos

**Arquitectura Robusta:**

- Implementar arquitectura MVC robusta basada en tecnologías del ecosistema Java
- Desarrollar capa de persistencia con JPA/Hibernate

**Seguridad y Rendimiento:**

- Implementar autenticación basada en sesiones con Spring Security
- Optimizar consultas de base de datos con índices estratégicos
- Garantizar validación de datos en backend

**Experiencia de Usuario:**

- Desarrollar interfaces responsive con Thymeleaf y Bootstrap
- Proporcionar feedback inmediato en todas las operaciones

#### Objetivos de Negocio

- Facilitar el crecimiento del ecosistema de servicios locales
- Establecer las bases para futuras monetizaciones sostenibles

---

## 4. Roles de Usuario

El sistema modela distintos roles de usuario. El control de acceso granular por rol está parcialmente implementado.

### 4.1 Administrador

#### Funcionalidades Incluidas en la Versión Actual

**Gestión de Categorías de Servicios:**

- Crear nuevas categorías de servicios
- Editar categorías existentes
- Eliminar categorías
- Activar o desactivar categorías según su estado operativo

**Acceso a Vistas Administrativas:**

- Acceder a la pantalla de usuarios (vista base, sin operaciones CRUD).
- Acceder a la pantalla de estadísticas (vista base, sin métricas cargadas)

#### Funcionalidades Previstas para Futuras Versiones

- Gestión completa de usuarios (altas, bajas, bloqueos y suspensiones)
- Asignación y revocación de roles y permisos
- Configuración de áreas geográficas y parámetros avanzados del sistema
- Implementación de políticas de moderación
- Sistema de analítica y generación de reportes
- Supervisión y gestión de incidencias
- Panel de auditoría, logs del sistema y gestión de copias de seguridad

### 4.2 Usuario Profesional

#### Funcionalidades Incluidas en la Versión Actual

**Gestión de Perfil Profesional:**

- Crear y mantener su perfil profesional con datos personales
- Describir el servicio ofrecido
- Establecer tarifa orientativa y ubicación

**Búsqueda y Participación en Solicitudes:**

- Consultar solicitudes publicadas mediante filtros (texto libre, categoría,código postal)
- Postularse a una solicitud enviando mensaje y presupuesto
- Editar o retirar postulaciones mientras estén en estado pendiente

**Gestión del Trabajo Asignado:**

- Actualizar el estado del trabajo una vez seleccionado:
  - Iniciar, pausar, reanudar, completar o cancelar
- Emitir valoraciones sobre trabajos completados
- Consultar historial de trabajos y valoraciones asociadas

#### Funcionalidades Excluidas del Alcance en Esta Fase

- Selección personalizada de categorías y áreas de cobertura
- Portfolio profesional o certificaciones
- Configuración avanzada de tarifas diferenciadas
- Gestión de calendario o agenda
- Sistema de notificaciones automáticas
- Panel de métricas y estadísticas de rendimiento

### 4.3 Usuario Cliente

#### Funcionalidades Incluidas en la Versión Actual

**Gestión de Solicitudes:**

- Crear y editar solicitudes con descripción, categoría, fecha límite y dirección
- Publicar, despublicar, cancelar, reactivar o eliminar solicitudes según estado

**Gestión de Postulaciones:**

- Recibir postulaciones de profesionales interesados
- Aceptar o rechazar candidatos de forma manual

**Seguimiento del Trabajo:**

- Gestionar el estado del trabajo asignado (pausar, completar, cancelar)
- Emitir valoración al profesional tras finalización
- Consultar historial de trabajos realizados

#### Funcionalidades Excluidas del Alcance en Esta Fase

- Inclusión de presupuesto orientativo en la solicitud
- Subida de fotografías o documentos adjuntos
- Aplicación de criterios automatizados de selección
- Sistema de comunicación directa o mensajería
- Gestión de lista de profesionales de confianza
- Gestión persistente de múltiples direcciones

### 4.4 Permisos y Restricciones de Acceso

**Sistema de Autenticación:**

- Todos los roles requieren autenticación mediante Spring Security
- Remember-me habilitado para sesiones persistentes

**Control de Acceso:**

- La versión actual protege rutas por autenticación
- Control de acceso basado en roles planificado a corto plazo
- Auditoría de accesos disponible mediante registro de logs de login

**Nota sobre Roles:** 
El modelo incluye el rol MODERATOR para futuras funciones, pero no se utiliza en la versión actual.

---

## 5. Descripción General del Sistema

DUIT es una aplicación web orientada a la gestión integral de trabajos y servicios, que permite la interacción de distintos tipos de usuarios dentro de un entorno controlado y seguro. El acceso a las funcionalidades del sistema está condicionado por el rol asignado a cada usuario.

### Componentes Funcionales de Alto Nivel

- **Capa Pública de Acceso (Index):** Punto de entrada accesible sin autenticación donde se presenta la plataforma y se permite acceso a registro e inicio de sesión.

- **Sistema de Autenticación:** Gestiona el registro de usuarios e inicio de sesión. Control de acceso basado en roles planificado a corto plazo.

- **Área Privada del Sistema (Home):** Entorno restringido a usuarios autenticados con funcionalidades dinámicas según perfil.

- **Gestión de Trabajos y Servicios:** Permite creación, visualización, seguimiento y control de solicitudes, postulaciones y trabajos.

- **Control de Accesos y Permisos:** Autenticación global implementada; segmentación por rol planificada a corto plazo.

### 5.1 Flujos de Trabajo Principales

#### Flujo del Cliente

1. Registro/Login → Acceso al área privada
2. Creación de solicitud → Definición de requisitos específicos
3. Publicación → Exposición a profesionales relevantes
4. Evaluación de propuestas → Análisis de candidatos
5. Selección de profesional → Creación del trabajo
6. Seguimiento del trabajo → Monitorización del progreso
7. Finalización y valoración → Cierre del ciclo

#### Flujo del Profesional

1. Registro → Configuración de perfil profesional
2. Configuración de servicios → Definición de especialidades
3. Búsqueda de oportunidades → Exploración de solicitudes relevantes
4. Postulación → Envío de propuestas personalizadas
5. Espera de respuesta → Aceptación o rechazo
6. Ejecución del trabajo → Realización del servicio
7. Finalización y valoración → Recepción de feedback

---

## 6. Requisitos Principales

### 6.1 Requisitos Funcionales

#### Gestión de Usuarios y Autenticación

**Registro de Usuarios:**

- El sistema debe permitir el registro de nuevos usuarios con email
- Debe validar unicidad de username, email y DNI

**Autenticación y Autorización:**

- Debe implementar login seguro con Spring Security
- Remember-me disponible para sesiones persistentes
- Diferenciación de acceso según roles planificada a corto plazo

**Gestión de Perfiles:**

- Debe permitir edición completa de datos personales
- No se mantiene historial de cambios en datos sensibles

#### Gestión de Servicios y Categorías

**Administración de Categorías:**

- El administrador puede crear, editar y eliminar categorías
- Permite asociación de múltiples categorías por profesional
- No mantiene histórico de cambios en categorías

#### Gestión de Solicitudes de Servicio

**Creación y Gestión de Solicitudes:**

- Los clientes pueden crear solicitudes con descripción detallada
- Implementa sistema de estados (DRAFT, PUBLISHED, IN_PROGRESS, COMPLETED, CANCELLED, EXPIRED)
- Permite asignar fecha límite a la solicitud

**Sistema de Postulaciones:**

- Los profesionales pueden postularse a solicitudes relevantes
- Permite envío de propuestas personalizadas con presupuesto obligatorio

**Asignación y Seguimiento:**

- Los clientes pueden seleccionar profesional de entre los postulados
- Actualiza automáticamente estados de solicitudes
- Permite seguimiento del progreso del trabajo

#### Sistema de Valoraciones y Reputación

**Valoraciones Bidireccionales:**

- Permite valoraciones entre clientes y profesionales
- Incluye puntuación numérica y comentarios textuales
- Evita valoraciones duplicadas por trabajo y tipo

### 6.2 Requisitos No Funcionales

#### Rendimiento

**Tiempos de Respuesta:**

- Las páginas principales deben cargar en menos de 3 segundos bajo condiciones normales
- La base de datos debe estar optimizada con índices apropiados

#### Seguridad

**Protección de Datos:**

- Debe cumplir con RGPD para protección de datos personales
- Debe encriptar contraseñas con algoritmo BCrypt
- Mantiene logs de accesos de login (éxito o fallo)

**Autenticación:**

- Debe implementar políticas de contraseñas seguras
- Debe proporcionar logout seguro con limpieza de sesión

#### Compatibilidad

**Navegadores:**

- Compatible con Chrome 80+, Firefox 75+, Safari 13+, Edge 80+
- Implementa diseño responsive para dispositivos móviles
- Funciona en tablets y pantallas de escritorio

**Accesibilidad:**

- Cumple con estándares WCAG 2.1 nivel AA
- Proporciona navegación por teclado donde es posible

#### Mantenibilidad

**Código y Documentación:**

- El código sigue convenciones de Java y Spring Boot
- Incluye documentación técnica actualizada

---

## 7. Criterios de Aceptación

Los criterios de aceptación definen las condiciones que debe cumplir DUIT para considerarse correcta y válida desde el punto de vista funcional, técnico y de calidad.

### 7.1 Criterios Funcionales

-  Un usuario debe poder registrarse e iniciar sesión correctamente
-  Un cliente debe poder crear y publicar una solicitud
-  Un profesional debe poder postularse a una solicitud
-  El cliente debe poder seleccionar un profesional
-  El profesional debe poder actualizar el estado del trabajo
-  Ambas partes deben poder emitir una valoración tras la finalización

### 7.2 Criterios de Calidad

Desde el punto de vista de la usabilidad, la aplicación debe ser intuitiva y fácil de usar, permitiendo que un usuario nuevo complete las principales acciones sin necesidad de formación previa. Se tienen en cuenta aspectos de accesibilidad y mantenibilidad del código, asegurando buenas prácticas de desarrollo y una base sólida para futuras mejoras.

### 7.3 Criterios de Negocio

La plataforma debe:

- Facilitar y agilizar la conexión entre clientes y profesionales
- Generar confianza mediante el sistema de valoraciones
- Mejorar la eficiencia frente a métodos tradicionales
- Mantenerse estable con alta disponibilidad sin pérdida de datos

### 7.4 Proceso de Validación

El proyecto se considerará aceptado cuando se cumplan los criterios funcionales, de calidad y de negocio. La validación se realizará mediante:

- Pruebas funcionales
- Revisión del código
- Verificación del correcto funcionamiento del sistema
- Entrega de documentación correspondiente

---

## 8. Reparto de Tareas y Responsabilidades

La elaboración del presente Documento de Alcance se ha realizado de forma colaborativa, siguiendo un flujo de trabajo iterativo.

**Aleixo Fernández Cuevas:**

- Responsable de la redacción inicial del documento
- Definición de estructura general, alcance, objetivos
- Descripción funcional del sistema
- Revisión final incorporando correcciones y mejoras

**Cristo Manuel Navarro Martín:**

- Implementación de apartados técnicos
- Especialmente seguridad, arquitectura del sistema y requisitos técnicos
- Colaboración en revisión de secciones funcionales
- Validación del contenido

---

**Última actualización:** 16 de febrero de 2026 
**Versión:** 3.0
