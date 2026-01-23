# Duit

Aplicación web orientada a la gestión de servicios y ofertas de trabajo.

## Estado inicial
- Proyecto Spring Boot generado y configurado.
- Estructura básica de carpetas y archivos creada.
- Dependencias principales añadidas en el pom.xml.

## Tecnologías y versiones
- **Java**: 21
- **Spring Boot**: 3.5.10
- **Maven**
- **PostgreSQL** 
- **Thymeleaf**
- **Lombok** 


## Dependencias principales
- spring-boot-starter-data-jpa
- spring-boot-starter-mail
- spring-boot-starter-security
- spring-boot-starter-thymeleaf
- spring-boot-starter-validation
- spring-boot-starter-web
- thymeleaf-extras-springsecurity6
- postgresql
- lombok
- spring-boot-starter-test (test)
- spring-security-test (test)
- spring-dotenv (para cargar variables de entorno desde un archivo `.env`)

### Uso de variables de entorno con `.env`
El proyecto utiliza la dependencia [`spring-dotenv`](https://github.com/paulschwarz/spring-dotenv) para cargar automáticamente las variables definidas en un archivo `.env` en la raíz del proyecto. Esto permite definir credenciales y configuraciones sensibles fuera del código fuente.

Ejemplo de archivo `.env`:

```env
DB_URL=jdbc:postgresql://<host>:<port>/<database>
DB_USER=usuario
DB_PASS=contraseña
```



## Enlaces útiles
- **Trello**: [Tablero PRW](https://trello.com/b/WuKam2k1/prw)
- **Figma**: [Diseño PRW](https://www.figma.com/design/y0dsqYgpwDFsx5Hy0YNVtL/PRW?t=IcXPDrcEpCyz3U7G-0)
- **Documentación**: _pendiente_


