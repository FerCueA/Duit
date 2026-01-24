
# Duit

Aplicación web para la gestión de servicios y ofertas de trabajo. Permite a los usuarios registrarse, iniciar sesión y acceder a funcionalidades según su rol.

---


## 🚀 Demo en producción

- **Aplicacion:** desplegado en Koyeb
	[https://overall-kippie-8fercuea8-dbe9588c.koyeb.app/](https://overall-kippie-8fercuea8-dbe9588c.koyeb.app/)

- **Base de datos:** PostgreSQL en Neon
	[https://neon.tech/](https://neon.tech/)

---


## 📚 Documentación oficial

Consulta la documentación oficial del proyecto:

[Documentación Duit](https://e.pcloud.link/publink/show?code=kZnwjaZ15u7S4qnaebz8Iq21LSKvRrC4nGX)


---


## Enlaces útiles
- **Trello**: [Tablero PRW](https://trello.com/b/WuKam2k1/prw)
- **Figma**: [Diseño PRW](https://www.figma.com/design/y0dsqYgpwDFsx5Hy0YNVtL/PRW?t=IcXPDrcEpCyz3U7G-0)

---


## 🗺️ Rutas principales

| Ruta           | Descripción                          | Acceso           |
|----------------|--------------------------------------|------------------|
| `/index`       | Página de inicio                     | Público          |
| `/login`       | Formulario de inicio de sesión       | Público          |
| `/registro`    | Registro de nuevos usuarios          | Público          |
| `/terminos`    | Términos y condiciones               | Público          |
| `/privacidad`  | Política de privacidad               | Público          |
| `/ayuda`       | Página de ayuda                      | Público          |
| `/home`        | Panel principal (dashboard)          | Solo logueados   |

Al iniciar sesión, el usuario accede a `/home`, donde se muestran sus datos y opciones según su rol. El header muestra el username y el rol autenticado.

---

## 📁 Estructura del proyecto

```
src/main/resources/
	templates/
		public/         # Vistas públicas: index, login, registro, terminos, privacidad, ayuda
		dashboard/      # Vistas privadas: home de usuario autenticado
		fragments/      # Fragmentos Thymeleaf reutilizables (header, footer, etc.)
	static/
		css/            # Hojas de estilo
		js/             # Scripts
		img/            # Imágenes
src/main/java/es/duit/app/
	controller/       # Controladores Spring Boot
	entity/           # Entidades JPA
	repository/       # Repositorios JPA
	security/         # Seguridad y autenticación
	config/           # Configuración de la app
```

---

## 🛠️ Instalación rápida

1. Clona el repositorio:
	```bash
	git clone <url-del-repo>
	```
2. Configura tu base de datos PostgreSQL (local o en la nube).
3. Crea el archivo `.env` en la raíz con tus credenciales:
	```env
	DB_URL=jdbc:postgresql://<host>:<port>/<database>
	DB_USER=usuario
	DB_PASS=contraseña
	```
4. Instala dependencias y ejecuta:
	```bash
	./mvnw spring-boot:run
	```

---


## ⚙️ Tecnologías principales

- **Java 21**
- **Spring Boot 3.5.10**
- **Maven**
- **PostgreSQL**
- **Thymeleaf**
- **Lombok**



## 📦 Dependencias destacadas

- spring-boot-starter-data-jpa
- spring-boot-starter-mail
- spring-boot-starter-security
- spring-boot-starter-thymeleaf
- spring-boot-starter-validation
- spring-boot-starter-web
- thymeleaf-extras-springsecurity6
- postgresql
- lombok
- spring-dotenv

---

### Variables de entorno
El proyecto utiliza [`spring-dotenv`](https://github.com/paulschwarz/spring-dotenv) para cargar automáticamente las variables definidas en un archivo `.env` en la raíz del proyecto.

Ejemplo de `.env`:
```env
DB_URL=jdbc:postgresql://<host>:<port>/<database>
DB_USER=usuario
DB_PASS=contraseña
```

En `application.properties`:
```
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
```

---


