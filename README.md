# TaskFlow API

API REST para gestión colaborativa de proyectos y tareas. Desarrollada con Spring Boot 3.5, MongoDB y JWT.

## Tech Stack

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.15 |
| Spring Security | 6.x (JWT stateless) |
| Spring Data MongoDB | 4.x |
| MongoDB | 7+ (Atlas / local) |
| JWT (jjwt) | 0.12.6 |
| Maven | 3.9+ (con wrapper) |
| Docker | multi-stage build |
| Lombok | reducción de boilerplate |

## Requisitos previos

- **Java 21** (JDK)
- **Maven 3.9+** (o usar el wrapper `./mvnw`)
- **MongoDB** 7+ (Atlas o instancia local)
- **Docker** (opcional, para contenedor)

## Configuración del entorno

### 1. Crear el archivo `.env`

Copia el archivo de ejemplo y edítalo con tus datos:

```bash
cp backend/.env.example backend/.env
```

### 2. Variables de entorno

| Variable | Descripción | Ejemplo |
|---|---|---|
| `JWT_KEY` | Clave secreta para firmar tokens JWT (Base64) | `RZXuJdRi0LfaTr3lvevuIqZMCIefdpqi14nSyHj3YeA=` |
| `JWT_EXPIRATION` | Duración del token JWT en ms (por defecto 1h) | `3600000` |
| `JWT_INVITATION_EXPIRATION` | Duración del token de invitación en ms (por defecto 7d) | `604800000` |
| `MONGODB_URI` | URI de conexión a MongoDB | `mongodb+srv://user:pass@cluster.mongodb.net/TaskFlow` |
| `MAIL_HOST` | Servidor SMTP | `smtp.gmail.com` |
| `MAIL_PORT` | Puerto SMTP | `587` |
| `MAIL_USERNAME` | Correo para enviar invitaciones | `tucorreo@gmail.com` |
| `MAIL_PASSWORD` | Contraseña de aplicación de Gmail | *(ver abajo)* |
| `APP_BASE_URL` | URL base para enlaces de invitación | `http://localhost:8080` |

### 3. Configurar Gmail SMTP (para invitaciones por correo)

El servicio de invitaciones envía correos usando Gmail SMTP. Necesitas una **contraseña de aplicación**:

1. Activa la [verificación en dos pasos](https://myaccount.google.com/security) en tu cuenta de Google
2. Ve a [Contraseñas de aplicación](https://myaccount.google.com/apppasswords)
3. Genera una contraseña para "Correo" y "Dispositivo"
4. Copia esa contraseña en `MAIL_PASSWORD` (sin espacios)

## Base de datos

El proyecto usa MongoDB con validación de esquemas a nivel de colección. Tienes tres opciones para la base de datos:

### Opción 1: MongoDB Atlas (nube)

1. Crea una cuenta en [MongoDB Atlas](https://www.mongodb.com/atlas)
2. Crea un clúster gratuito
3. Obtén la URI de conexión y pégala en `MONGODB_URI`

### Opción 2: MongoDB local (instalado en tu máquina)

1. Instala MongoDB Community Server
2. La URI sería: `mongodb://localhost:27017/TaskFlow`

### Opción 3: MongoDB en Docker

Se levanta automáticamente junto con el backend (ver sección [Cómo ejecutar con Docker](#con-docker-backend--mongodb)).

### Inicializar colecciones

Si usas Atlas o MongoDB local, ejecuta el script de inicialización con `mongosh`:

```bash
mongosh <database/init.js
```

O desde MongoDB Compass: abre el script `database/init.js` y ejecútalo en la shell.

Si usas MongoDB en Docker, el script se ejecuta automáticamente la primera vez que se levanta el contenedor (no necesitas hacer nada).

Esto crea las colecciones `Users`, `Projects`, `Tasks` y `Comments` con sus validaciones de esquema correspondientes.

## Cómo ejecutar

### Local con Maven

```bash
cd backend
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

### Con Docker (solo backend)

Usa esta opción si ya tienes MongoDB en Atlas o instalado localmente en tu máquina.

```bash
cd backend
docker compose up --build
```

> El backend se conectará a la URI que tengas configurada en `MONGODB_URI` del archivo `.env`.

### Con Docker (backend + MongoDB)

Usa esta opción si quieres un entorno completamente aislado con MongoDB en un contenedor.

```bash
cd backend
docker compose -f docker-compose.yml -f docker-compose.db.yml up --build
```

Esto levanta dos contenedores:
- **backend**: la API en `http://localhost:8080`
- **mongo**: MongoDB 7 en `mongodb://localhost:27017`

El script `database/init.js` se ejecuta automáticamente la primera vez que se inicia MongoDB, creando las colecciones con sus validaciones. Los datos persisten en un volumen Docker (`mongo_data`).

> **Nota:** Cuando usas esta opción, `MONGODB_URI` del `.env` se ignora y se usa automáticamente `mongodb://mongo:27017/TaskFlow`.

## Colección de Postman

La colección de Postman con todos los endpoints preconfigurados está en:

```
postman/TaskFlow-API.postman_collection.json
```

### Cómo usarla

1. Abre Postman
2. Ve a **File > Import** o arrastra el archivo JSON a Postman
3. Selecciona la colección **TaskFlow API**
4. Todas las peticiones están organizadas por controlador
5. Ejecuta primero **Auth > Register** o **Auth > Login** para obtener el token
6. El token JWT se envía automáticamente como `Authorization: Bearer <token>` en el resto de endpoints

## Endpoints de la API

> **Base URL:** `http://localhost:8080`
>
> Todos los endpoints marcados como **Autenticado** requieren el header `Authorization: Bearer <token>`. Los endpoints públicos no requieren autenticación.

### Auth — `/api/auth`

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `POST` | `/api/auth/register` | Público | Registrar un nuevo usuario |
| `POST` | `/api/auth/login` | Público | Iniciar sesión y obtener token JWT |

**RegisterRequest:**
```json
{
  "name": "Juan Pérez",
  "email": "juan@email.com",
  "password": "Password1!",
  "mobilePhone": "+123456789"
}
```

- `name`: 3-50 caracteres
- `email`: válido, 3-50 caracteres
- `password`: 8-20 caracteres, debe contener al menos una letra, un número y un carácter especial
- `mobilePhone`: 7-15 dígitos, puede incluir `+`

**LoginRequest:**
```json
{
  "email": "juan@email.com",
  "password": "Password1!"
}
```

**LoginResponse (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**RegisterResponse:** `201 Created` (sin cuerpo)

---

### Usuario — `/api/user`

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/api/user/profile` | Autenticado | Obtener perfil del usuario autenticado |

**ProfileResponse (200):**
```json
{
  "name": "Juan Pérez",
  "email": "juan@email.com",
  "mobilePhone": "+123456789"
}
```

---

### Proyectos — `/api/projects`

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `POST` | `/api/projects` | Autenticado | Crear un proyecto |
| `GET` | `/api/projects` | Autenticado | Listar proyectos del usuario |
| `GET` | `/api/projects/detail` | Autenticado | Obtener detalle de un proyecto |
| `PUT` | `/api/projects` | Autenticado | Actualizar un proyecto (solo OWNER) |
| `DELETE` | `/api/projects` | Autenticado | Eliminar un proyecto (solo OWNER) |

**CreateProjectRequest:**
```json
{
  "name": "Mi Proyecto",
  "description": "Descripción del proyecto"
}
```

- `name`: 3-50 caracteres
- `description`: 3-200 caracteres

**DetailProjectRequest:**
```json
{
  "projectId": "abc123"
}
```

**UpdateProjectRequest:** (todos los campos opcionales excepto `projectId`)
```json
{
  "projectId": "abc123",
  "name": "Nuevo nombre",
  "description": "Nueva descripción"
}
```

**DeleteProjectRequest:**
```json
{
  "projectId": "abc123"
}
```

**ProjectDetailResponse (200):**
```json
{
  "projectId": "abc123",
  "name": "Mi Proyecto",
  "description": "Descripción del proyecto",
  "ownerId": "userId123",
  "status": "ACTIVE",
  "members": [
    {
      "userId": "userId123",
      "role": "OWNER"
    }
  ]
}
```

- `status`: `ACTIVE` | `ARCHIVED`
- `role` (members): `OWNER` | `ADMIN` | `MEMBER`

> **Notas:**
> - Al crear un proyecto, el creador se agrega automáticamente como `OWNER`
> - Solo el `OWNER` puede actualizar o eliminar el proyecto
> - `GET /api/projects` devuelve todos los proyectos donde el usuario es miembro

---

### Tareas — `/api/tasks`

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `POST` | `/api/tasks` | Autenticado | Crear una tarea |
| `GET` | `/api/tasks/detail` | Autenticado | Obtener detalle de una tarea |
| `GET` | `/api/tasks` | Autenticado | Listar tareas con filtros y paginación |
| `PUT` | `/api/tasks` | Autenticado | Actualizar una tarea |
| `PUT` | `/api/tasks/assign` | Autenticado | Asignar tarea a un usuario (solo OWNER/ADMIN) |
| `DELETE` | `/api/tasks` | Autenticado | Eliminar una tarea (solo OWNER/ADMIN) |

**CreateTaskRequest:**
```json
{
  "projectId": "abc123",
  "title": "Implementar login",
  "description": "Crear el formulario y la lógica de autenticación",
  "priority": "HIGH",
  "dueDate": "2026-08-01T12:00:00Z",
  "assignedTo": "userId456"
}
```

- `projectId`: obligatorio
- `title`: 1-50 caracteres, obligatorio
- `description`: 3-200 caracteres, obligatorio
- `priority`: `LOW` | `MEDIUM` | `HIGH`
- `dueDate`: fecha futura en formato ISO-8601
- `assignedTo`: opcional, debe ser miembro del proyecto

**TaskDetailRequest:**
```json
{
  "taskId": "task123"
}
```

**ListTasksRequest:**
```json
{
  "projectId": "abc123",
  "status": "TO_DO",
  "priority": "HIGH",
  "assignedTo": "userId456",
  "title": "login",
  "page": 0,
  "size": 20,
  "sortBy": "createdAt",
  "sortDir": "desc"
}
```

- `projectId`: obligatorio
- `status`: opcional (`TO_DO` | `IN_PROGRESS` | `BLOCKED` | `DONE`)
- `priority`: opcional (`LOW` | `MEDIUM` | `HIGH`)
- `assignedTo`: opcional
- `title`: opcional (búsqueda por expresión regular)
- `page`: por defecto 0
- `size`: por defecto 20 (máx. 100)
- `sortBy`: por defecto `createdAt`
- `sortDir`: `asc` | `desc` (por defecto `desc`)

**UpdateTaskRequest:** (todos los campos opcionales excepto `taskId`)
```json
{
  "taskId": "task123",
  "title": "Nuevo título",
  "description": "Nueva descripción",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM",
  "dueDate": "2026-09-01T12:00:00Z"
}
```

**AssignTaskRequest:**
```json
{
  "taskId": "task123",
  "assignedTo": "userId789"
}
```

**DeleteTaskRequest:**
```json
{
  "taskId": "task123"
}
```

**TaskDetailResponse (200):**
```json
{
  "taskId": "task123",
  "projectId": "abc123",
  "title": "Implementar login",
  "description": "Crear el formulario y la lógica de autenticación",
  "createdBy": "userId123",
  "assignedTo": "userId456",
  "status": "TO_DO",
  "priority": "HIGH",
  "dueDate": "2026-08-01T12:00:00Z",
  "createdAt": "2026-07-04T10:00:00Z"
}
```

> **Notas:**
> - Para crear o consultar tareas, el usuario debe ser miembro del proyecto
> - `PUT /api/tasks`: los miembros con rol `MEMBER` solo pueden actualizar tareas asignadas a ellos; `OWNER`/`ADMIN` pueden actualizar cualquier tarea del proyecto
> - `PUT /api/tasks/assign`: solo `OWNER`/`ADMIN` pueden reasignar tareas

---

### Comentarios — `/api/comments`

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `POST` | `/api/comments` | Autenticado | Crear un comentario en una tarea |
| `GET` | `/api/comments/detail` | Autenticado | Obtener detalle de un comentario |
| `GET` | `/api/comments` | Autenticado | Listar comentarios de una tarea |
| `DELETE` | `/api/comments` | Autenticado | Eliminar un comentario |

**CreateCommentRequest:**
```json
{
  "taskId": "task123",
  "content": "Este es un comentario sobre la tarea"
}
```

- `taskId`: obligatorio
- `content`: 1-200 caracteres

**DetailCommentRequest:**
```json
{
  "commentId": "comment123"
}
```

**ListCommentsRequest:**
```json
{
  "taskId": "task123"
}
```

**DeleteCommentRequest:**
```json
{
  "commentId": "comment123"
}
```

**DetailCommentResponse (200):**
```json
{
  "commentId": "comment123",
  "taskId": "task123",
  "authorId": "userId123",
  "content": "Este es un comentario sobre la tarea",
  "createdAt": "2026-07-04T10:30:00Z"
}
```

> **Nota:** Solo el autor del comentario o un `ADMIN`/`OWNER` del proyecto pueden eliminar un comentario.

---

### Miembros del proyecto — `/api/projects/members`

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/api/projects/members` | Autenticado | Listar miembros de un proyecto |
| `PUT` | `/api/projects/members` | Autenticado | Cambiar rol de un miembro (solo OWNER) |
| `DELETE` | `/api/projects/members` | Autenticado | Eliminar un miembro del proyecto |

**GetMembersRequest:**
```json
{
  "projectId": "abc123"
}
```

**ChangeRoleRequest:**
```json
{
  "projectId": "abc123",
  "memberId": "userId456",
  "role": "ADMIN"
}
```

- `role`: `ADMIN` | `MEMBER` (no se puede asignar `OWNER`)

**DeleteMemberRequest:**
```json
{
  "projectId": "abc123",
  "memberId": "userId456"
}
```

**MemberDetailResponse (200):**
```json
{
  "userId": "userId456",
  "name": "María García",
  "email": "maria@email.com",
  "mobilePhone": "+987654321",
  "ROLE": "ADMIN"
}
```

> **Notas:**
> - Solo el `OWNER` puede cambiar roles
> - El `OWNER` no puede cambiarse su propio rol
> - El `OWNER` puede eliminar cualquier miembro
> - Un `ADMIN` solo puede eliminar miembros con rol `MEMBER`
> - Nadie puede eliminar al `OWNER`

---

### Invitaciones — `/api/invitations`

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `POST` | `/api/invitations` | Autenticado | Invitar a un usuario por email |
| `GET` | `/api/invitations/accept` | Público | Aceptar invitación (desde el enlace del correo) |
| `GET` | `/api/invitations/decline` | Público | Rechazar invitación (desde el enlace del correo) |

**InviteMemberRequest:**
```json
{
  "projectId": "abc123",
  "email": "invitado@email.com",
  "role": "MEMBER"
}
```

- `role`: `ADMIN` | `MEMBER`

**Accept/Decline:** Se llaman mediante el enlace que llega al correo:

```
http://localhost:8080/api/invitations/accept?token=eyJhbGciOiJIUzI1NiJ9...
http://localhost:8080/api/invitations/decline?token=eyJhbGciOiJIUzI1NiJ9...
```

> **Notas:**
> - Solo `OWNER` o `ADMIN` pueden invitar miembros
> - El usuario invitado debe existir en el sistema (estar registrado)
> - No se puede invitar a alguien que ya es miembro del proyecto
> - Se envía un correo HTML con enlaces de aceptar/rechazar

---

### Códigos de respuesta HTTP

| Código | Significado |
|---|---|
| `200 OK` | Solicitud exitosa |
| `201 Created` | Recurso creado exitosamente |
| `204 No Content` | Recurso eliminado exitosamente |
| `400 Bad Request` | Error de validación o solicitud inválida |
| `401 Unauthorized` | Credenciales inválidas |
| `404 Not Found` | Recurso no encontrado |
| `409 Conflict` | Conflicto (ej. usuario ya existe) |
| `500 Internal Server Error` | Error inesperado del servidor |

## Estructura del proyecto

```
taskflow-api/
├── README.md
├── LICENSE
├── database/
│   └── init.js                    # Script de inicialización de MongoDB
├── docs/
│   ├── diagrams/
│   │   ├── DiagramaContexto.drawio       # C4 - Nivel 1: Contexto
│   │   ├── DiagramaContenedor.drawio     # C4 - Nivel 2: Contenedores
│   │   └── DiagramaComponente.drawio     # C4 - Nivel 3: Componentes
│   └── requirements/
│       └── Documentacion_TaskFlow.docx   # Épicas y user stories
├── postman/
│   └── TaskFlow-API.postman_collection.json
├── backend/
│   ├── .env                       # Variables de entorno (no trackeado)
│   ├── .env.example               # Template del .env
│   ├── pom.xml
│   ├── Dockerfile
│   ├── docker-compose.yml           # Backend solo (para Atlas o MongoDB local)
│   ├── docker-compose.db.yml        # Backend + MongoDB en contenedor
│   └── src/
│       ├── main/
│       │   ├── java/com/taskflow/
│       │   │   ├── TaskFlowApplication.java
│       │   │   ├── config/           # SecurityConfig, ApplicationConfig
│       │   │   ├── controllers/      # Auth, User, Project, Task, Comment, ProjectMember, Invitation
│       │   │   ├── dtos/
│       │   │   │   ├── requests/     # Register, Login, CreateProject, CreateTask, etc.
│       │   │   │   └── responses/    # Profile, ProjectDetail, TaskDetail, etc.
│       │   │   ├── exceptions/       # GlobalExceptionHandler + excepciones personalizadas
│       │   │   ├── jwts/             # JwtService, JwtAuthenticationFilter
│       │   │   ├── models/           # User, Project, Task, Comment, Member + enums
│       │   │   ├── repositories/     # Interfaces Spring Data MongoDB
│       │   │   └── services/         # Interfaces + Implementaciones
│       │   └── resources/
│       │       └── application.properties
│       └── test/java/com/taskflow/
│           ├── TaskFlowApplicationTests.java
│           └── services/             # Tests unitarios de servicios
```

## Documentación

La documentación del proyecto se encuentra en la carpeta `docs/`.

### Diagramas C4 (`docs/diagrams/`)

Archivos en formato Draw.io (`.drawio`). Puedes abrirlos con:

- [draw.io](https://app.diagrams.net) (versión web, importando el archivo)
- VS Code con la extensión **Draw.io Integration**

| Diagrama | Nivel | Descripción |
|---|---|---|
| `DiagramaContexto.drawio` | 1 - Contexto | Visión general del sistema y sus actores |
| `DiagramaContenedor.drawio` | 2 - Contenedores | Frontend, backend y base de datos |
| `DiagramaComponente.drawio` | 3 - Componentes | Componentes internos del backend por dominio |

### Requisitos (`docs/requirements/`)

- `Documentacion_TaskFlow.docx` — Documento de Word con las épicas y user stories del proyecto.

## Frontend (próximamente)

Actualmente el proyecto es solo backend. Se planea agregar un frontend en el directorio `frontend/` del mismo repositorio.

## Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.
