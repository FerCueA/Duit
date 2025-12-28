# Proyecto Duit

## Descripción
Duit es una aplicación web diseñada para gestionar sesiones de usuario, registro y búsqueda de ofertas. En los últimos días, hemos realizado varias mejoras importantes en el diseño y funcionalidad de la aplicación.

## Funcionalidades Implementadas

### 1. Página de Inicio de Sesión y Registro
- **Diseño:**
  - Ambas secciones están organizadas en un diseño de dos columnas.
  - Se utilizan estilos personalizados definidos en `styles.css`.
  - Los colores y tamaños están adaptados para una experiencia visual agradable.
- **Funcionalidad:**
  - Formulario de inicio de sesión con campos para correo electrónico y contraseña.
  - Formulario de registro con campos para nombre completo, correo electrónico y contraseña.
  - Botones alineados y funcionales para enviar los datos.

### 2. Mejoras en el Header
- **Logo:**
  - Se añadió un logo en formato SVG directamente en el header.
  - El tamaño del logo fue ajustado para que sea más visible.
- **Estilo:**
  - Se redujo el tamaño del header para hacerlo más compacto.
  - Se aplicaron gradientes y ajustes visuales para mejorar la apariencia.

### 3. Footer
- **Ubicación:**
  - Se ajustó el footer para que se mantenga en la parte inferior de la página.
- **Contenido:**
  - Información sobre la empresa, enlaces útiles y datos de contacto.

### 4. Funcionalidad de Búsqueda
- **Formulario:**
  - Se añadió un formulario para buscar ofertas.
- **Resultados Dinámicos:**
  - Los resultados se cargan dinámicamente en la sección "Selecciona una opción".

### 5. Estilos Personalizados
- **Archivo:** `styles.css`
- **Colores:**
  - Se añadieron gradientes y ajustes visuales para mejorar la experiencia del usuario.

### 6. Base de Datos y Autenticación
- **Implementación de la base de datos MySQL:**
  - Se crearon las tablas `usuario` y `rol` según los nuevos modelos.
  - Se conectó la aplicación a la base de datos para gestión de usuarios y roles.
- **Login con autenticación de usuarios desde la base de datos:**
  - El sistema de login ahora valida las credenciales contra los datos almacenados en la base de datos.
  - Se utiliza Spring Security para la autenticación y gestión de sesiones.




## Próximos Pasos
- Continuar mejorando el diseño de las páginas.
- Implementar más funcionalidades dinámicas.


