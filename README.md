# Trackfile Backend

API REST para la gestion documental y el mantenimiento operativo de empresas de transporte. Centraliza usuarios, empresas, vehiculos, conductores, propietarios, documentos, solicitudes, mantenimientos, notificaciones y alertas.

## Tecnologias

- Java 17
- Spring Boot 3.5.7
- Spring Web
- Spring Data JPA con Hibernate
- PostgreSQL
- Spring Security y JWT (JJWT)
- Spring Validation
- Spring Mail
- Apache PDFBox
- Cloudinary
- Maven Wrapper
- Docker

## Caracteristicas

- Registro de empresas, verificacion de correo e inicio de sesion.
- Autenticacion stateless mediante tokens JWT y contrasenas protegidas con BCrypt.
- Gestion de usuarios, empresas, vehiculos, conductores y propietarios.
- Carga, consulta y control de estado de documentos asociados a usuarios y vehiculos.
- Gestion de solicitudes con historial y archivos adjuntos.
- Programacion y seguimiento de mantenimientos de vehiculos.
- Notificaciones y alertas por vigencia documental.
- Historial de conversaciones del modulo de chat.
- Aislamiento de los datos operativos por empresa.

## Arquitectura y estructura del proyecto

El proyecto sigue una arquitectura por capas:

```text
src/
|-- main/
|   |-- java/com/TrackFile/app/
|   |   |-- config/       # Seguridad, JWT y configuracion web
|   |   |-- domain/       # Entidades JPA y enumeraciones
|   |   |-- repository/   # Acceso a datos con Spring Data JPA
|   |   |-- service/      # Logica de aplicacion e integraciones
|   |   `-- web/          # Controladores REST y DTO
|   `-- resources/
|       |-- static/       # Paginas locales de prueba
|       `-- application.properties
`-- test/                 # Pruebas automatizadas
```

## Requisitos

- JDK 17
- PostgreSQL accesible desde la aplicacion
- Las credenciales SMTP necesarias para el registro y la verificacion por correo
- Maven no es obligatorio: el repositorio incluye Maven Wrapper
- Docker es opcional

## Instalacion

```bash
git clone https://github.com/MelgarejoMaycol/BackTrackfile.git
cd BackTrackfile
```

Crea la base de datos y configura las variables de entorno indicadas en `.env.example`. Spring Boot no carga archivos `.env` automaticamente; exporta las variables en tu terminal o configuralas en el entorno de ejecucion.

En PowerShell, por ejemplo:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/trackfile"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "tu_password"
$env:JWT_SECRET = "un_secreto_largo_y_seguro"
$env:MAIL_HOST = "smtp.example.com"
$env:MAIL_PORT = "587"
$env:MAIL_USERNAME = "usuario@example.com"
$env:MAIL_PASSWORD = "tu_password_smtp"
$env:MAIL_FROM = "remitente-verificado@example.com"
$env:CLOUDINARY_CLOUD_NAME = "tu_cloud_name"
$env:CLOUDINARY_API_KEY = "tu_api_key"
$env:CLOUDINARY_API_SECRET = "tu_api_secret"
$env:APP_BASE_URL = "http://localhost:8080"
```

## Variables de entorno

| Variable | Descripcion | Valor por defecto |
| --- | --- | --- |
| `PORT` | Puerto HTTP de la aplicacion | `8080` |
| `DB_URL` | URL JDBC de PostgreSQL | URL de Neon definida en la configuracion |
| `DB_USER` | Usuario de PostgreSQL | `neondb_owner` |
| `DB_PASSWORD` | Contrasena de PostgreSQL | Sin valor |
| `JWT_SECRET` | Secreto usado para firmar los JWT | Sin valor |
| `JWT_EXPIRATION_MINUTES` | Duracion del token en minutos | `120` |
| `MAIL_HOST` | Servidor SMTP | Requerido |
| `MAIL_PORT` | Puerto SMTP | Requerido |
| `MAIL_USERNAME` | Usuario SMTP | Sin valor |
| `MAIL_PASSWORD` | Contrasena SMTP | Sin valor |
| `MAIL_FROM` | Remitente verificado usado en los correos | Requerido |
| `CLOUDINARY_CLOUD_NAME` | Nombre del cloud de Cloudinary | Requerido |
| `CLOUDINARY_API_KEY` | Clave publica de Cloudinary | Requerido |
| `CLOUDINARY_API_SECRET` | Secreto de Cloudinary | Requerido |
| `APP_BASE_URL` | URL publica del backend | URL de Render definida en la configuracion |
| `APP_UPLOAD_DIR` | Directorio local de archivos | `uploads` |

No agregues credenciales a `application.properties`. Para desarrollo local tambien puede usarse un archivo `application-secrets.properties` en la raiz, que ya esta excluido por `.gitignore`.

## Ejecucion

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080` salvo que se configure otro valor en `PORT`.

## Pruebas

En Windows:

```powershell
.\mvnw.cmd test
```

En Linux o macOS:

```bash
./mvnw test
```

Las pruebas de contexto necesitan una configuracion de base de datos y las variables requeridas por la aplicacion.

## Build

```powershell
.\mvnw.cmd clean package
```

El artefacto ejecutable se genera en `target/`.

## Docker

```bash
docker build -t trackfile-backend .
docker run --rm -p 8080:8080 --env-file .env trackfile-backend
```

El contenedor expone el puerto `8080`; suministra las variables de entorno necesarias al iniciarlo.

## Despliegue en Render

Configura el servicio como Docker y registra las variables de `.env.example` desde el panel **Environment**. No definas `PORT`: Render proporciona esa variable automaticamente.

Para Neon, separa la cadena de conexion en tres variables. `DB_URL` debe comenzar por `jdbc:postgresql://` y no debe incluir usuario ni contrasena:

```env
DB_URL=jdbc:postgresql://ep-young-wave-ahdvmbc2.c-3.us-east-1.aws.neon.tech/neondb?sslmode=require
DB_USER=neondb_owner
DB_PASSWORD=your_neon_password
```

Para Brevo utiliza credenciales SMTP, no la API key HTTP:

```env
MAIL_HOST=smtp-relay.brevo.com
MAIL_PORT=587
MAIL_USERNAME=your_brevo_smtp_login
MAIL_PASSWORD=your_brevo_smtp_key
MAIL_FROM=your_verified_sender@example.com
```

## Repositorio

Repositorio personal independiente: [MelgarejoMaycol/BackTrackfile](https://github.com/MelgarejoMaycol/BackTrackfile).
