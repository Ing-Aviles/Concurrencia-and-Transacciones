-- Active: 1712546310107@@127.0.0.1@5432
CREATE DATABASE tienda;

CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    precio NUMERIC(10, 2) NOT NULL,
    stock INT NOT NULL
);

INSERT INTO productos (nombre, descripcion, precio, stock) VALUES
('Producto 1', 'Descripción del producto 1', 10.99, 100),
('Producto 2', 'Descripción del producto 2', 20.50, 50),
('Producto 3', 'Descripción del producto 3', 15.75, 75);


CREATE TABLE usuarios (
    _id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    password VARCHAR(100) NOT NULL
);

INSERT INTO usuarios (nombre, password) VALUES
('rober', '3008'),
('alguien mas', '2');

