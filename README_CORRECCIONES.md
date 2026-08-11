# Correcciones aplicadas al backend OrtoCitas

## 1. Autenticación del personal

Los tres roles internos:

- `ADMIN`
- `ODONTOLOGO`
- `SECRETARIA`

inician sesión con:

```json
{
  "cedula": "0102030405",
  "password": "TuPassword"
}
```

Endpoint:

`POST /api/auth/login`

La contraseña se almacena únicamente como hash BCrypt. El backend devuelve un JWT y el cliente debe enviarlo en:

`Authorization: Bearer <TOKEN>`

El `subject` del JWT es la cédula del usuario.

## 2. Normalización

Se eliminaron los enums persistidos como texto y ahora existen catálogos:

- `rol_usuario`
- `estado_horario`
- `estado_cita`

Las tablas que los utilizan guardan únicamente la FK:

- `usuarios.rol_usuario_id`
- `horarios_disponibles.estado_horario_id`
- `citas.estado_cita_id`

Los catálogos se cargan con:

- ADMIN, ODONTOLOGO, SECRETARIA
- DISPONIBLE, OCUPADO, BLOQUEADO
- PENDIENTE, EN_ESPERA, ATENDIDA, CANCELADA

## 3. Nombres y apellidos

Se separaron los campos:

- `nombres`
- `apellidos`

Esto se aplica a `usuarios` y `pacientes`.

## 4. JPA

JPA ya estaba incluido en el proyecto mediante:

`spring-boot-starter-data-jpa`

Además está incluido el driver PostgreSQL.

No necesitas crear un `persistence.xml`: Spring Boot configura JPA mediante `application.yml`.

## 5. Supabase

Copia las credenciales desde **Supabase > Connect > Session pooler** y colócalas como variables de entorno.

Ejemplo:

```env
DB_URL=jdbc:postgresql://aws-0-REGION.pooler.supabase.com:5432/postgres?sslmode=require
DB_USERNAME=postgres.PROJECT_REF
DB_PASSWORD=TU_PASSWORD
```

Para un backend Spring persistente se usa aquí el Session Pooler (5432). El puerto 6543 corresponde al modo transaction pooler y tiene restricciones con prepared statements.

## 6. Crear la base

Ejecuta:

`backend/database/schema_normalizado.sql`

en **Supabase > SQL Editor**.

Después inicia el backend.

Hibernate está configurado con:

`ddl-auto: validate`

Esto es intencional: JPA valida que las tablas coincidan, pero no modifica la estructura de producción.

## 7. Crear el administrador inicial

Opcionalmente define:

```env
ADMIN_CEDULA=0102030405
ADMIN_NOMBRES=Cristian
ADMIN_APELLIDOS=Administrador
ADMIN_PASSWORD=UnaPasswordSegura123!
```

Al iniciar el backend, si esa cédula no existe, se crea el administrador y la contraseña se guarda con BCrypt.

## 8. Variables importantes

Nunca subas las contraseñas de Supabase, `JWT_SECRET` ni `ADMIN_PASSWORD` al repositorio.

Usa `.env.example` como plantilla para configurar tu entorno.
