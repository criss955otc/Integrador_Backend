-- ============================================================
-- Boquitas Sanas - esquema PostgreSQL normalizado para Supabase
-- ============================================================
-- Ejecutar en Supabase > SQL Editor sobre una base nueva/vacía.
-- Si ya tienes tablas con el esquema anterior, haz respaldo antes
-- y usa este archivo como referencia para la migración de columnas.
--
-- JPA/Hibernate está configurado con ddl-auto=validate: NO modifica
-- automáticamente la estructura de la base de datos.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS rol_usuario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS estado_horario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS estado_cita (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL
);

INSERT INTO rol_usuario (codigo, nombre) VALUES
    ('ADMIN', 'Administrador'),
    ('ODONTOLOGO', 'Odontólogo'),
    ('SECRETARIA', 'Secretaria')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO estado_horario (codigo, nombre) VALUES
    ('DISPONIBLE', 'Disponible'),
    ('OCUPADO', 'Ocupado'),
    ('BLOQUEADO', 'Bloqueado')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO estado_cita (codigo, nombre) VALUES
    ('PENDIENTE', 'Pendiente'),
    ('EN_ESPERA', 'En espera'),
    ('ATENDIDA', 'Atendida'),
    ('CANCELADA', 'Cancelada')
ON CONFLICT (codigo) DO NOTHING;

CREATE TABLE IF NOT EXISTS usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cedula VARCHAR(10) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    rol_usuario_id UUID NOT NULL REFERENCES rol_usuario(id),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pacientes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cedula VARCHAR(10) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(30),
    email VARCHAR(150),
    fecha_nacimiento DATE,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS servicios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    precio NUMERIC(12,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS horarios_disponibles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    odontologo_id UUID NOT NULL REFERENCES usuarios(id),
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado_horario_id UUID NOT NULL REFERENCES estado_horario(id),
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_horario_horas CHECK (hora_inicio < hora_fin)
);

CREATE TABLE IF NOT EXISTS citas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    paciente_id UUID NOT NULL REFERENCES pacientes(id),
    odontologo_id UUID REFERENCES usuarios(id),
    horario_id UUID NOT NULL UNIQUE REFERENCES horarios_disponibles(id),
    servicio_id UUID REFERENCES servicios(id),
    estado_cita_id UUID NOT NULL REFERENCES estado_cita(id),
    notas TEXT,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_usuarios_rol_id ON usuarios(rol_usuario_id);
CREATE INDEX IF NOT EXISTS idx_horarios_odontologo_fecha ON horarios_disponibles(odontologo_id, fecha);
CREATE INDEX IF NOT EXISTS idx_horarios_fecha_estado ON horarios_disponibles(fecha, estado_horario_id);
CREATE INDEX IF NOT EXISTS idx_citas_paciente ON citas(paciente_id);
CREATE INDEX IF NOT EXISTS idx_citas_odontologo ON citas(odontologo_id);
CREATE INDEX IF NOT EXISTS idx_citas_estado ON citas(estado_cita_id);

COMMENT ON TABLE rol_usuario IS 'Catálogo normalizado de roles de usuarios de la aplicación.';
COMMENT ON TABLE estado_horario IS 'Catálogo normalizado de estados de horarios.';
COMMENT ON TABLE estado_cita IS 'Catálogo normalizado de estados de citas.';
COMMENT ON COLUMN usuarios.password_hash IS 'Nunca guardar contraseñas en texto plano. El backend usa BCrypt.';
