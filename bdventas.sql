--  Crear la base de datos
CREATE DATABASE bdventas;

-- Crear el esquema --> Producto, Marcas y Categorias
CREATE SCHEMA catalogos;

-- Crear tabla de categorias
CREATE TABLE catalogos.categorias
(
	id_categoria SERIAL PRIMARY KEY,
	nombre VARCHAR(80) NOT NULL,
	descripcion VARCHAR(200) NOT NULL,
	estado SMALLINT NOT NULL DEFAULT 1,

	CONSTRAINT chk_categoria_estado
		CHECK (estado IN (0, 1))
);
-- Comprobacion registro con DEFAULT
INSERT INTO catalogos.categorias (nombre, descripcion, estado)
VALUES ('Cereales', 'Categoria adicional', 1);


-- Crear tabla de marcas
CREATE TABLE catalogos.marcas
(
	id_marca SERIAL PRIMARY KEY,
	nombre VARCHAR(80) NOT NULL UNIQUE,
	pais_origen VARCHAR(80),
	estado SMALLINT NOT NULL DEFAULT 1,

	CONSTRAINT chk_marca_estado
		CHECK(estado IN(0, 1))
);
-- Comprobacion
INSERT INTO catalogos.marcas (nombre, pais_origen, estado)
VALUES ('Nestle', 'Suiza', 1);

INSERT INTO catalogos.marcas (nombre, pais_origen, estado)
VALUES ('Kelloggs', 'Estados Unidos', 1);

INSERT INTO catalogos.marcas (nombre, pais_origen, estado)
VALUES ('Coca Cola', 'Estados Unidos', 1);

-- Crear tabla de productos
CREATE TABLE catalogos.productos
(
	id_producto SERIAL NOT NULL PRIMARY KEY,
	codigo VARCHAR(20) NOT NULL UNIQUE,
	nombre VARCHAR(100) NOT NULL,
	descripcion VARCHAR(200),
	precio NUMERIC(10, 2) NOT NULL,
	stock INTEGER NOT NULL DEFAULT 0,
	fecha_creacion DATE NOT NULL DEFAULT CURRENT_DATE,
	id_categoria INTEGER NOT NULL,
	id_marca INTEGER NOT NULL,
	estado SMALLINT NOT NULL DEFAULT 1,

	CONSTRAINT fk_producto_categoria
		FOREIGN KEY (id_categoria)
		REFERENCES catalogos.categorias(id_categoria),

	CONSTRAINT fk_producto_marca
		FOREIGN KEY (id_marca)
		REFERENCES catalogos.marcas(id_marca),

	CONSTRAINT chk_producto_precio
		CHECK (precio > 0),

	CONSTRAINT chk_producto_stock
		CHECK (stock > 0),

	CONSTRAINT chk_producto_estado
		CHECK (estado IN (0, 1))
);

-- Insertar productos
INSERT INTO catalogos.productos
(codigo, nombre, descripcion, precio, stock, id_categoria, id_marca, estado)
VALUES
('P001', 'Cereal Integral', 'Cereal de trigo integral', 85.50, 20, 5, 6, 1);

INSERT INTO catalogos.productos
(codigo, nombre, descripcion, precio, stock, id_categoria, id_marca, estado)
VALUES
('P002', 'Cereal Corn Flakes', 'Cereal de maiz', 95.00, 15, 5, 7, 1);

INSERT INTO catalogos.productos
(codigo, nombre, descripcion, precio, stock, id_categoria, id_marca, estado)
VALUES
('P003', 'Cereal Nesquik', 'Cereal de chocolate', 110.00, 12, 5, 6, 1);

-- Comprobar los datos
SELECT * FROM catalogos.categorias;

SELECT * FROM catalogos.marcas;

SELECT * FROM catalogos.productos;