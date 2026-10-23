CREATE DATABASE IF NOT EXISTS clinica_db;
USE clinica_db;

-- 1. TABLA DE USUARIOS (Estructura base para los 3 roles)
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(100) NOT NULL,
    apellido_materno VARCHAR(100),
    dni VARCHAR(15) UNIQUE NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    rol ENUM('ADMIN', 'MEDICO', 'PACIENTE') NOT NULL,
    secret_2fa VARCHAR(255), -- Para guardar la clave secreta del QR de Google
    is_2fa_enabled BOOLEAN DEFAULT FALSE
);

-- 2. TABLA DE MÉDICOS (Extensión de usuarios para campos específicos)
CREATE TABLE medicos (
    usuario_id INT PRIMARY KEY,
    especialidad VARCHAR(100) NOT NULL,
    foto_url VARCHAR(255), -- Opcional, como pediste
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 3. TABLA DE DISPONIBILIDAD DE LOS MÉDICOS
CREATE TABLE disponibilidades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medico_id INT NOT NULL,
    dia_semana ENUM('Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo') NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado ENUM('PENDIENTE', 'APROBADO') DEFAULT 'PENDIENTE',
    FOREIGN KEY (medico_id) REFERENCES medicos(usuario_id) ON DELETE CASCADE
);

-- 4. TABLA DE CITAS
CREATE TABLE citas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    paciente_id INT NOT NULL,
    medico_id INT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado ENUM('PENDIENTE', 'REALIZADO', 'NO REALIZADO') DEFAULT 'PENDIENTE',
    FOREIGN KEY (paciente_id) REFERENCES usuarios(id),
    FOREIGN KEY (medico_id) REFERENCES medicos(usuario_id)
);