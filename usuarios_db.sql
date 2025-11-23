-- --------------------------------------------------------
-- SCRIPT DE ESQUEMA DE BASE DE DATOS PARA SPRING BOOT
-- Base de datos: usuarios_db
-- --------------------------------------------------------

-- 1. Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS usuarios_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. Seleccionar la base de datos para usarla
USE usuarios_db;

-- 3. Borrar la tabla 'usuarios' si ya existe (para asegurar una instalación limpia)
DROP TABLE IF EXISTS usuarios;

-- 4. Crear la tabla 'usuarios' con todos los campos finales
CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    
    -- Campos de Autenticación
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    rol VARCHAR(50) NOT NULL,
    
    -- Campos de Perfil
    fecha_nacimiento DATE,
    avatar VARCHAR(255),
    
    -- Campos de Reglas de Negocio (Admin CRUD)
    enabled BOOLEAN NOT NULL DEFAULT TRUE, 
    password_reset_required BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id)
);