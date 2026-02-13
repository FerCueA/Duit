# Documentacion tecnica justificativa

## 1. Introduccion general

El proyecto sigue una arquitectura MVC (Model-View-Controller) para separar claramente la representacion de datos, la logica de negocio y la gestion de la interfaz. Esta eleccion favorece la mantenibilidad, facilita el trabajo en equipo y reduce el acoplamiento entre capas. La separacion en capas permite:

- Escalar la aplicacion sin reescrituras masivas.
- Sustituir tecnologia en una capa con minimo impacto.
- Centralizar la logica de negocio y reutilizarla desde varios controladores.

Los objetivos principales del diseno son la claridad estructural, la trazabilidad de decisiones y la facilidad para evolucionar el sistema.

## 2. Capa Entity

Las entidades representan el dominio del problema y definen el modelo persistente. Cada entidad se diseno para reflejar conceptos del negocio: solicitudes de servicio, usuarios, roles, postulaciones, trabajos, etc.

### 2.1 Anotaciones JPA principales

- `@Entity`: indica que la clase es persistente y debe mapearse a una tabla.
- `@Table`: define el nombre de la tabla e indices, optimizando consultas.
- `@Id`: marca la clave primaria.
- `@GeneratedValue`: genera IDs automaticamente para garantizar unicidad.
- `@Column`: controla nombre, longitud, nullabilidad y restricciones.

### 2.2 Relaciones y justificacion

- `@ManyToOne`: modela dependencias donde muchas filas apuntan a una (por ejemplo, solicitudes y usuario cliente).
- `@OneToMany`: define colecciones relacionadas (por ejemplo, solicitud con varias postulaciones).
- `@OneToOne`: se usa cuando la cardinalidad es estricta (si aplica en el dominio).

### 2.3 Estrategia de carga

Se prioriza `LAZY` para evitar cargas innecesarias y mejorar rendimiento. `EAGER` se reservaria solo para relaciones criticas y de tamano reducido. Alternativa: usar `EAGER` y resolver N+1 con fetch joins, pero se eligio `LAZY` para controlar explicitamente las cargas en servicios.

### 2.4 Alternativas y decisiones

- Entidades anemicas con servicios ricos vs entidades con logica: se adopta un punto medio, manteniendo metodos utilitarios simples en las entidades.
- Embeddables para direccion o valores compuestos: alternativa valida si se quisiera evitar tablas separadas.

## 3. Capa Repository

Se utiliza `JpaRepository` para reducir codigo repetitivo y aprovechar las convenciones de Spring Data JPA.

### 3.1 Ventajas

- CRUD automatico sin implementaciones manuales.
- Consultas derivadas por nombre de metodo.
- Integracion directa con transacciones y el contexto de persistencia.

### 3.2 Metodos derivados vs consultas personalizadas

- Metodos derivados se usan para consultas simples.
- Consultas personalizadas (JPQL) se usan cuando se requiere un fetch especifico o join controlado.

### 3.3 Por que no se uso DAO manual

Implementar DAO manual incrementa el mantenimiento, duplica codigo y reduce la consistencia con el resto del ecosistema Spring. Se elige JPA por productividad y estandarizacion.

## 4. Capa Service

Los servicios encapsulan la logica de negocio y separan la capa web de la persistencia.

### 4.1 Responsabilidad principal

- Orquestar operaciones complejas.
- Validar reglas de negocio.
- Coordinar repositorios sin exponer detalles a los controladores.

### 4.2 Principio de responsabilidad unica

Cada servicio agrupa operaciones de un dominio concreto (solicitudes, historial, seguridad, etc.). Esto evita clases monoliticas y facilita pruebas y mantenimiento.

### 4.3 Validaciones y excepciones

Las validaciones se realizan en el servicio para centralizar reglas (estado valido, usuario propietario, etc.). Se lanzan excepciones controladas para garantizar consistencia y feedback claro a la capa web.

## 5. Capa Controller

El controlador actua como puente entre la vista y la logica de negocio.

### 5.1 Separacion entre API y logica

El controlador no contiene reglas de negocio; delega en servicios. Esto mantiene el codigo limpio y la reusabilidad alta.

### 5.2 Uso de REST y ResponseEntity

Cuando se expone API, `ResponseEntity` permite devolver estados HTTP coherentes y cuerpos estructurados.

### 5.3 Validacion de entrada

Se valida la entrada mediante anotaciones en DTOs y comprobaciones adicionales en servicios.

## 6. DTOs

Los DTOs se usan para:

- Evitar exponer entidades directamente.
- Controlar exactamente que datos se envian a vistas o APIs.
- Reducir acoplamiento con la capa persistente.

**Alternativa:** exponer entidades directamente. **Riesgos:** filtrado de datos sensibles, problemas con lazy loading en vistas, dependencia fuerte del modelo interno.

## 7. Seguridad

Se aplica seguridad basada en autenticacion y autorizacion, usando un enfoque compatible con Spring Security.

- Autenticacion: validar identidad del usuario.
- Autorizacion: comprobar permisos antes de operaciones sensibles.

Justificacion: proporciona un marco robusto y estandar con integracion directa en controladores y servicios.

## 8. Decisiones tecnicas relevantes

### 8.1 Base de datos

Se elige una base de datos relacional por consistencia, integridad referencial y consultas estructuradas.

### 8.2 Transacciones

Se usa el manejo transaccional de Spring cuando es necesario garantizar atomicidad en operaciones criticas. Alternativa: gestion manual de transacciones, menos segura y mas propensa a errores.

### 8.3 Gestion de errores

Se utilizan excepciones controladas para comunicar errores de negocio y evitar estados inconsistentes.

### 8.4 Concurrencia

El diseno evita operaciones concurrentes peligrosas al centralizar cambios de estado en servicios.

## 9. Conclusion tecnica

El sistema presenta una arquitectura robusta, escalable y coherente con buenas practicas de desarrollo.

Fortalezas principales:

- Separacion clara de capas.
- Logica de negocio centralizada.
- Uso correcto de JPA y Spring Data.
- DTOs para seguridad y desacoplamiento.

Posibles mejoras:

- Implementar pruebas unitarias y de integracion mas extensas.
- Refactorizar validaciones repetidas con componentes reutilizables.
- Mejorar la gestion de errores con un controlador global.

El sistema es escalable y puede evolucionar a medida que se amplien requisitos.
