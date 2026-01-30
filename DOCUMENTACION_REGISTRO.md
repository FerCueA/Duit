# 📋 Documentación del Sistema de Registro y Activación por Email

## 🎯 ¿Qué hace este sistema?

El sistema permite a los usuarios **registrarse en la aplicación** y **activar su cuenta por correo electrónico** antes de poder iniciar sesión. Es un proceso de seguridad que verifica que el email proporcionado sea real y pertenezca al usuario.

---

## 📊 Flujo Completo del Proceso

```
1. Usuario rellena formulario → 2. Validaciones → 3. Email enviado → 4. Usuario activa → 5. Puede usar la app
   [Página registro]           [Backend]      [Gmail SMTP]     [Click enlace]    [Login funciona]
```

---

## 🔧 Componentes del Sistema

### 1. **DTO (Datos de Entrada)** 📥

**Archivo:** `RegistroDTO.java`

```java
// Recibe y valida los datos del formulario
- firstName: String (obligatorio, 2-100 caracteres)
- lastName: String (obligatorio, hasta 150 caracteres)  
- email: String (obligatorio, formato email válido)
- dni: String (obligatorio, formato: 8 dígitos + letra)
- phone: String (obligatorio, 9-15 dígitos, puede empezar con +)
- password: String (obligatorio, mínimo 6 caracteres)
- userType: String (USER o PROFESSIONAL)
```

### 2. **Entidad Usuario** 👤

**Archivo:** `AppUser.java`

```java
// Campos importantes para la activación:
- active: Boolean = false          // ❌ Inactivo por defecto
- activationToken: String          // 🔑 Token único UUID
- activationTokenExpires: DateTime // ⏰ Expira en 24 horas
- username: String                 // 📧 El email del usuario
```

### 3. **Repositorio** 🗄️

**Archivo:** `AppUserRepository.java`

```java
// Métodos de búsqueda especiales:
- findByUsername(email)         → Verificar si email ya existe
- findByDni(dni)               → Verificar si DNI ya existe  
- findByActivationToken(token) → Encontrar usuario por token
```

---

## ⚙️ Servicios Principales

### 🔐 **RegistroService** - Lógica de Negocio

**Método: `registrarUsuario()`**

```java
1. Validar email único      → Si existe: "Email ya registrado"
2. Validar DNI único        → Si existe: "DNI ya registrado" 
3. Obtener rol según tipo   → USER o PROFESSIONAL
4. Crear usuario:
   - Encriptar contraseña   → BCrypt
   - Generar token UUID     → Único e irrepetible
   - Marcar como inactivo   → active = false
   - Fijar expiración       → +24 horas
5. Guardar en base de datos → PostgreSQL
6. Enviar email activación  → Gmail SMTP
```

**Método: `activarCuenta(token)`**

```java
1. Buscar usuario por token  → Si no existe: false
2. Verificar token no expiró → Si expirado: false  
3. Verificar no está activo  → Si ya activo: false
4. Activar cuenta:
   - Marcar active = true    → ✅ Usuario habilitado
   - Eliminar token          → null (token usado)
   - Guardar cambios         → Base de datos
```

### 📧 **EmailService** - Envío de Correos

**Configuración SMTP:**

```properties
Gmail SMTP Server: smtp.gmail.com
Puerto: 587 (STARTTLS)
Usuario: aleixonoventa@gmail.com  
Contraseña: tkqvrjshypscsqlfm (App Password)
```

**Plantilla de Email:**

```
Asunto: "Activa tu cuenta en Duit"

Hola [NOMBRE],

¡Gracias por registrarte en Duit!

Para activar tu cuenta, haz clic en el siguiente enlace:
https://duitapp.koyeb.app/activate?token=[TOKEN_UUID]

Este enlace expirará en 24 horas.

Si no te registraste en Duit, ignora este mensaje.

Saludos,
El equipo de Duit
```

---

## 🌐 Controladores Web

### **PublicController** - Endpoints Públicos

**📝 Registro: `GET /registro`**

```java
→ Muestra formulario de registro
→ Incluye validaciones JavaScript/Bootstrap
```

**📮 Procesar: `POST /registro`**

```java
→ Recibe datos del formulario
→ Valida con @Valid y Bean Validation
→ Si error: regresa con mensajes de error
→ Si éxito: "¡Registro exitoso! Revisa tu correo electrónico"
```

**✅ Activar: `GET /activate?token=UUID`**

```java
→ Recibe token desde el email
→ Llama a registroService.activarCuenta()
→ Si éxito: "¡Cuenta activada! Ya puedes iniciar sesión"
→ Si error: "Enlace inválido o expirado"
```

---

## 🎨 Frontend - Validaciones en HTML

### **Archivo:** `registro.html`

**Validaciones en tiempo real:**

```html
- Campos obligatorios: required + mensaje personalizado
- Email: type="email" + patrón Bootstrap
- DNI: pattern="[0-9]{8}[A-Z]" + validación
- Teléfono: pattern="[+]?[0-9]{9,15}"
- Contraseña: minlength="6"
```

**Estilos Bootstrap:**

```html
- Campo válido: is-valid (borde verde)
- Campo inválido: is-invalid (borde rojo)  
- Mensajes de error: invalid-feedback
- Mensajes de éxito: alert alert-success
```

---

## ⚙️ Configuración del Sistema

### **Variables de Entorno** (`.env`)

```bash
# Base de datos PostgreSQL (Neon)
DB_URL=jdbc:postgresql://ep-summer-hall-aggrronu-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require&channelBinding=require
DB_USER=neondb_owner  
DB_PASS=npg_fUq8e1lphOxg

# Email SMTP (Gmail)
EMAIL_USER=aleixonoventa@gmail.com
EMAIL_PASS=tkqvrjshypscsqlfm

# URL de la aplicación  
BASE_URL=https://duitapp.koyeb.app
```

### **Propiedades Spring** (`application.properties`)

```properties
# Email SMTP
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USER:}
spring.mail.password=${EMAIL_PASS:}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# URL base para emails
app.base-url=${BASE_URL:https://duitapp.koyeb.app}
```

---

## 🚦 Estados del Usuario

| Estado | active | activationToken | Puede loguearse |
|--------|--------|----------------|-----------------|
| **Recién registrado** | `false` | UUID válido | ❌ NO |
| **Email activado** | `true` | `null` | ✅ SÍ |
| **Token expirado** | `false` | UUID expirado | ❌ NO |

---

## 🔍 Validaciones Implementadas

### **Nivel 1: Frontend (HTML/JavaScript)**

- ✅ Campos obligatorios
- ✅ Formatos básicos (email, teléfono, DNI)
- ✅ Longitud mínima/máxima
- ✅ Feedback visual inmediato

### **Nivel 2: DTO (Bean Validation)**  

- ✅ `@NotBlank` - Campos no vacíos
- ✅ `@Email` - Formato email válido
- ✅ `@Pattern` - Expresiones regulares (DNI, teléfono)
- ✅ `@Size` - Longitud de contraseña

### **Nivel 3: Service (Lógica de Negocio)**

- ✅ Email único en base de datos
- ✅ DNI único en base de datos  
- ✅ Roles válidos en base de datos
- ✅ Validación final de entidad

### **Nivel 4: Base de Datos (Constraints)**

- ✅ `UNIQUE` constraints (email, DNI)
- ✅ `NOT NULL` constraints  
- ✅ Índices para performance

---

## 📋 Casos de Uso Principales

### **✅ Registro Exitoso**

```
1. Usuario completa formulario correctamente
2. Email y DNI no existen en sistema
3. Usuario creado con active=false
4. Email enviado con token de 24h
5. Mensaje: "¡Registro exitoso! Revisa tu correo"
```

### **❌ Registro con Errores**

```
1. Email ya registrado → "Este correo ya está registrado"
2. DNI ya registrado → "Este DNI ya está registrado"  
3. Campos inválidos → Mensajes específicos por campo
4. Error de BD → "Error inesperado al guardar usuario"
```

### **✅ Activación Exitosa**  

```
1. Usuario hace click en enlace del email
2. Token existe y no ha expirado
3. Usuario marcado como active=true
4. Token eliminado de BD
5. Mensaje: "¡Cuenta activada! Ya puedes iniciar sesión"
```

### **❌ Activación Fallida**

```
1. Token no existe → "Enlace inválido o expirado"
2. Token expirado → "Enlace inválido o expirado"
3. Usuario ya activo → "Enlace inválido o expirado"
```

---

## 🛡️ Medidas de Seguridad

1. **Contraseñas encriptadas** con BCrypt
2. **Tokens UUID únicos** e irrepetibles  
3. **Expiración de 24 horas** para tokens
4. **Validación multi-capa** (Frontend + Backend + BD)
5. **Email desde cuenta verificada** (Gmail con App Password)
6. **Conexión SMTP segura** (STARTTLS)
7. **Variables de entorno** para credenciales sensibles

---

## 🚀 Despliegue en Koyeb

**Variables de entorno a configurar:**

```bash
BASE_URL=https://duitapp.koyeb.app
EMAIL_USER=aleixonoventa@gmail.com
EMAIL_PASS=tkqvrjshypscsqlfm  
DB_URL=jdbc:postgresql://...
DB_USER=neondb_owner
DB_PASS=npg_fUq8e1lphOxg
```

**El sistema está listo para producción** ✅

---

## 🔧 Para Desarrolladores

### **Testing Local**

```bash
1. Configurar .env con variables locales
2. BASE_URL=http://localhost:8080  
3. mvn spring-boot:run
4. Acceder a http://localhost:8080/registro
```

### **Logs Importantes**

```bash
- Registro exitoso: "Nuevo usuario registrado: email@example.com"
- Activación exitosa: Usuario activado en BD
- Errores: Logs detallados en nivel DEBUG
```

**¡Sistema completo de registro con activación por email implementado!** 🎉
