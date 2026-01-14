
# Duit

## Introducción
Duit es una aplicación web desarrollada con Java y Spring Boot que simula una plataforma de ofertas de trabajo. Permite a los usuarios registrarse, iniciar sesión, buscar ofertas y gestionar su perfil. El proyecto está orientado a estudiantes que quieren aprender desarrollo web moderno y buenas prácticas.

## Tecnologías utilizadas
- **Java 17+**: Lenguaje principal.
- **Spring Boot**: Framework para crear aplicaciones web seguras y robustas.
- **Thymeleaf**: Motor de plantillas para las vistas HTML.
- **Bootstrap 5**: Framework CSS para diseño responsivo y moderno.
- **MySQL**: Base de datos relacional.
- **Maven**: Gestión de dependencias y construcción del proyecto.

## Instalación y ejecución
1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/duit.git
   cd duit
   ```
2. La configuración de la base de datos se realiza directamente en el archivo Java `MySqlConnection.java`. Revisa ese archivo para modificar usuario, contraseña o nombre de la base de datos.
3. Compila el proyecto:
   ```bash
   ./mvnw clean package -DskipTests
   ```
4. Inicia la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```
5. Abre tu navegador en `http://localhost:8080`

## Estructura del proyecto
```
src/
  main/
    java/es/duit/...      # Código fuente Java
    resources/
      templates/          # Vistas HTML (Thymeleaf)
      static/             # Imágenes, CSS, JS
  test/                   # Pruebas unitarias
pom.xml                   # Configuración de Maven
README.md                 # Documentación
```

## Funcionalidades principales
- Registro y login de usuarios
- Búsqueda y visualización de ofertas
- Panel de usuario y edición de perfil
- Roles diferenciados: profesional y cliente
- Seguridad y gestión de sesiones con Spring Security


## Créditos
Desarrollado por el equipo Aleixo y Cristo.

## Próximos pasos
- Mejorar el diseño visual y la experiencia de usuario
- Añadir más funcionalidades dinámicas (por ejemplo, notificaciones)

