
## Cambios realizados

**Modificar el empaquetado en `pom.xml`:**
 - `<packaging>war</packaging>`
 - `<packaging>jar</packaging>`

**Actualizar la dependencia de Tomcat:**
   - WAR:
     ```xml
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-tomcat</artifactId>
         <scope>provided</scope>
     </dependency>
     ```
   - JAR:
     ```xml
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-tomcat</artifactId>
     </dependency>
     ```
   - Esto permite que Tomcat se incluya en el JAR como servidor embebido.

**Eliminar la clase `ServletInitializer`:**
   - Esta clase solo es necesaria para WAR. Se eliminó el archivo `src/main/java/es/duit/app/ServletInitializer.java`.


