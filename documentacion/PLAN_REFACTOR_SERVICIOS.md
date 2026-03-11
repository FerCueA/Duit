# Plan de Refactor de Servicios

Fecha: 2026-03-07
Estado: En progreso
Objetivo: simplificar arquitectura, reducir duplicacion y aclarar responsabilidades en la capa service.

## Como usar este documento

- Ejecutar los PRs en orden.
- No mezclar fases para reducir riesgo.
- Al cerrar cada PR, marcar checklist y anotar incidencias.

## PR1 - Delimitar RequestService

Objetivo: dejar `RequestService` solo para ciclo de vida de solicitudes.

### Cambios

- `src/main/java/es/duit/app/service/RequestService.java`
  - Eliminar `getApplicationsForMyRequest(...)`
  - Eliminar `acceptRequest(...)`
  - Eliminar `rejectRequest(...)`
  - Eliminar `validateApplicationBelongsToRequest(...)`
- `src/main/java/es/duit/app/controller/MyRequestsController.java`
  - `viewApplications(...)` debe usar `JobApplicationService.getApplicationsForUserRequest(...)`
  - `acceptApplicationWithParams(...)` y `rejectApplicationWithParams(...)` deben usar `JobService`

### Checklist

- [x] Compila (`./mvnw -q -DskipTests compile`)
- [x] Flujos de ver/aceptar/rechazar postulaciones siguen funcionando
- [x] `RequestService` ya no contiene logica de postulaciones

### Estado PR1

- Completado el 2026-03-07.
- `RequestService` ya no contiene metodos de postulaciones (`getApplicationsForMyRequest`, `acceptRequest`, `rejectRequest`, `validateApplicationBelongsToRequest`).
- `MyRequestsController` ahora usa `JobApplicationService` para listar postulaciones y `JobService` para aceptar/rechazar.

---

## PR2 - Extraer validaciones repetidas

Objetivo: quitar duplicacion de ownership/precondiciones.

### Nuevos componentes

- `src/main/java/es/duit/app/service/validation/RequestAccessValidator.java`
  - `getOwnedRequestOrThrow(requestId, user)`
  - `getEditableOwnedRequestOrThrow(requestId, user)`
- `src/main/java/es/duit/app/service/validation/UserPreconditionsValidator.java`
  - `requireAddress(user)`
  - `requireProfessionalProfile(user)`

### Cambios

- `RequestService` usa `RequestAccessValidator` y elimina duplicados privados.
- `JobApplicationService` reutiliza precondiciones y ownership de request.
- `JobService` reutiliza reglas de acceso donde aplique.

### Checklist

- [x] Menos metodos privados duplicados entre servicios
- [x] Mensajes de error coherentes
- [x] No hay regresion funcional en rutas actuales

### Estado PR2

- Completado el 2026-03-07.
- Nuevos componentes compartidos:
  - `src/main/java/es/duit/app/service/validation/RequestAccessValidator.java`
  - `src/main/java/es/duit/app/service/validation/UserPreconditionsValidator.java`
- `RequestService` y `JobApplicationService` migrados para reutilizar validaciones de acceso y precondiciones.

---

## PR3 - Consolidar transiciones duplicadas en JobService

Objetivo: reducir pares cliente/profesional con misma logica.

### Cambios

- `src/main/java/es/duit/app/service/JobService.java`
  - Consolidar:
    - `completeJob(...)` + `completeJobAsClient(...)`
    - `pauseJob(...)` + `pauseJobAsClient(...)`
    - `cancelJob(...)` + `cancelJobAsClient(...)`
  - Mantener un unico flujo de cambio de estado con validacion por actor.

### Checklist

- [x] Menos duplicacion en `JobService`
- [x] Reglas de autorizacion intactas
- [x] Ciclo de vida de `ServiceJob` coherente

### Estado PR3

- Completado el 2026-03-07.
- `JobService` consolidado para `completeJob(...)` y `pauseJob(...)` con validacion por actor (cliente o profesional) en un unico flujo.
- Eliminadas variantes duplicadas `completeJobAsClient(...)`, `pauseJobAsClient(...)` y `cancelJobAsClient(...)`.
- `MyRequestsController` actualizado para usar los metodos consolidados (`completeJob`, `pauseJob`, `cancelJob`).
- Compila correctamente con `./mvnw -q -DskipTests compile`.

---

## PR4 - Separar SearchService de capa web

Objetivo: quitar dependencia de `Model` en servicios.

### Nuevos componentes

- `src/main/java/es/duit/app/dto/SearchPageDataDTO.java`

### Cambios

- `src/main/java/es/duit/app/service/SearchService.java`
  - Reemplazar `prepareSearchPageData(...)` por `buildSearchPageData(filters, user)`
- `src/main/java/es/duit/app/controller/ProfessionalController.java`
  - El controlador vuelca el DTO al `Model`

### Checklist

- [x] `SearchService` no importa `org.springframework.ui.Model`
- [x] Pantalla de busqueda mantiene mismo comportamiento

### Estado PR4

- Completado el 2026-03-07.
- Nuevo DTO: `src/main/java/es/duit/app/dto/SearchPageDataDTO.java`.
- `SearchService` reemplaza `prepareSearchPageData(...)` por `buildSearchPageData(filters, user)` y deja de depender de la capa web.
- `ProfessionalController` ahora vuelca el DTO al `Model` en el controlador.
- Compila correctamente con `./mvnw -q -DskipTests compile`.

---

## PR5 - Centralizar categorias activas y normalizacion

Objetivo: evitar duplicacion transversal.

### Nuevos componentes

- `src/main/java/es/duit/app/service/util/IdentityNormalizer.java`
  - `normalizeEmail(String)`
  - `normalizeDni(String)` (si aplica)

### Cambios

- `src/main/java/es/duit/app/service/CategoryService.java`
  - Agregar `getActiveCategories()`
- `src/main/java/es/duit/app/service/RequestService.java`
  - Eliminar `getActiveCategories()`
- `src/main/java/es/duit/app/controller/RequestFormController.java`
  - Usar `CategoryService.getActiveCategories()`
- `src/main/java/es/duit/app/service/SearchService.java`
  - Usar `CategoryService.getActiveCategories()`
- `src/main/java/es/duit/app/service/RegistroService.java`
  - Reusar `IdentityNormalizer`
- `src/main/java/es/duit/app/service/AccessLogService.java`
  - Reusar `IdentityNormalizer`
  - Sustituir `catch` silencioso por logging

### Checklist

- [x] Una sola fuente para categorias activas
- [x] Normalizacion de identidad unificada
- [x] Menos logica repetida entre servicios

### Estado PR5

- Completado el 2026-03-07.
- Nueva utilidad compartida: `src/main/java/es/duit/app/service/util/IdentityNormalizer.java` (`normalizeEmail`, `normalizeDni`).
- `CategoryService` ahora expone `getActiveCategories()` como fuente unica.
- `RequestFormController` y `SearchService` migrados para usar `CategoryService.getActiveCategories()`.
- `RequestService` ya no contiene `getActiveCategories()` duplicado.
- `RegistroService` y `AccessLogService` reutilizan `IdentityNormalizer`.
- `AccessLogService` reemplaza `catch` silencioso por logging (`warn`).
- Compila correctamente con `./mvnw -q -DskipTests compile`.

---

## PR6 (opcional) - Limpiar endpoints posiblemente huerfanos

Objetivo: resolver rutas de cliente sin enlace claro en UI.

### Revisar

- `MyRequestsController`:
  - `/requests/complete/{jobId}`
  - `/requests/pause/{jobId}`
  - `/requests/cancel-job/{jobId}`
  - `/requests/complete-request/{id}`

### Decision

- Si el flujo existe: enlazar en templates y cubrir con pruebas.
- Si no existe: eliminar endpoints y metodos asociados.

### Checklist

- [x] No quedan rutas huerfanas
- [x] La UI refleja los flujos reales

### Estado PR6

- Completado el 2026-03-07.
- Se revisaron templates y no se encontraron enlaces de UI a estas rutas:
  - `/requests/complete/{jobId}`
  - `/requests/pause/{jobId}`
  - `/requests/cancel-job/{jobId}`
  - `/requests/complete-request/{id}`
- Se eliminaron esos endpoints de `MyRequestsController`.
- Se eliminó el método asociado sin uso `RequestService.completeRequest(...)`.
- Compila correctamente con `./mvnw -q -DskipTests compile`.

---

## Fase 3 (post-auditoria) - Separar responsabilidades de JobService

Objetivo: dividir decisiones de postulaciones y ciclo de vida de trabajos en servicios especializados.

### Nuevos componentes

- `src/main/java/es/duit/app/service/ApplicationDecisionService.java`
- `src/main/java/es/duit/app/service/JobLifecycleService.java`

### Cambios

- `src/main/java/es/duit/app/service/JobService.java`
  - Convertido a fachada delegante para compatibilidad durante la transicion.
- `src/main/java/es/duit/app/controller/MyRequestsController.java`
  - Usa `ApplicationDecisionService` para aceptar/rechazar postulaciones.
  - Usa `JobLifecycleService` para listar trabajos del cliente.
- `src/main/java/es/duit/app/controller/PostulacionesController.java`
  - Usa `JobLifecycleService` para iniciar/pausar/reanudar/finalizar/cancelar.
- `src/main/java/es/duit/app/controller/ProfessionalController.java`
  - Usa `JobLifecycleService` para listar trabajos del profesional.

### Checklist

- [x] `JobService` ya no concentra logica de ambos dominios
- [x] Controladores cableados al servicio especializado
- [x] Compila (`./mvnw -q -DskipTests compile`)
- [x] Tests existentes en verde (`./mvnw -q test`)

### Estado Fase 3

- Completado el 2026-03-07.
- Se realizo separacion de responsabilidades sin romper compatibilidad gracias a la fachada `JobService`.

---

## Fase 4 (post-auditoria) - Retirar fachada JobService

Objetivo: completar la separacion eliminando la fachada de compatibilidad ya sin consumidores.

### Cambios

- `src/main/java/es/duit/app/service/JobService.java`
  - Eliminado al no tener referencias activas en controladores ni servicios.

### Checklist

- [x] No quedan referencias a `JobService` en `src/main/java`
- [x] Compila (`./mvnw -q -DskipTests compile`)
- [x] Tests existentes en verde (`./mvnw -q test`)

### Estado Fase 4

- Completado el 2026-03-07.

---

## Orden recomendado

1. PR1
2. PR2
3. PR3
4. PR4
5. PR5
6. PR6 (opcional)

## Comandos de control por PR

```bash
./mvnw -q -DskipTests compile
./mvnw -q test
```

## Notas de seguimiento

- Ultimo estado registrado: PR1 completado y compilando correctamente.
- Ultimo estado registrado: PR2 completado y compilando correctamente.
- Ultimo estado registrado: PR3 completado y compilando correctamente.
- Ultimo estado registrado: PR4 completado y compilando correctamente.
- Ultimo estado registrado: PR5 completado y compilando correctamente.
- Ultimo estado registrado: PR6 completado y compilando correctamente.
- Ultimo estado registrado: Fase 3 (post-auditoria) completada con tests en verde.
- Ultimo estado registrado: Fase 4 (post-auditoria) completada con eliminacion de fachada `JobService`.
- Plan de refactor ejecutado completo segun fases definidas.
