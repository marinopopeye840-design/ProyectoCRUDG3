CREATE SCHEMA catalogos;

-- Creación de tabla de marcas
CREATE TABLE catalogos.marcas
(
    id_marca SERIAL PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    pais_origen VARCHAR(80) NOT NULL,
    estado SMALLINT NOT NULL DEFAULT 1,
    CONSTRAINT chk_marca_estado
        CHECK (estado IN (0,1))
);

-- Pruebas para validar los CONSTRAINTS

-- Prueba #1: NO permite valores nulos
/*
INSERT INTO catalogos.marcas (nombre, pais_origen, estado)
VALUES (NULL, 'Nicaragua', 1);
*/

-- Prueba #2: NO permite que la columna nombre tenga el mismo valor 2 veces
/*
INSERT INTO catalogos.marcas (nombre, pais_origen, estado)
VALUES ('Almohada', 'Nicaragua', 1);

INSERT INTO catalogos.marcas (nombre, pais_origen, estado)
VALUES ('Almohada', 'Nicaragua', 1);
*/

-- Prueba #3: Agregar el valor por defecto en la columna estado
/*
INSERT INTO catalogos.marcas (nombre, pais_origen)
VALUES ('Audifonos', 'Nicaragua');
*/


-- TABLA 2: Categorias
CREATE TABLE catalogos.categorias
(
    id_categoria SERIAL PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    estado SMALLINT NOT NULL DEFAULT 1,
    CONSTRAINT chk_categoria_estado
        CHECK (estado IN (0,1))
);

-- Pruebas para validar los CONSTRAINTS

-- Prueba #1: NO permite valores nulos
/*
INSERT INTO catalogos.categorias (nombre, descripcion, estado)
VALUES (NULL, 'Con detalles', 1);
*/

-- Prueba #2: NO permite que la columna nombre tenga el mismo valor 2 veces
/*
INSERT INTO catalogos.categorias (nombre, descripcion, estado)
VALUES ('Joyeria', 'Con detalles', 1);

INSERT INTO catalogos.categorias (nombre, descripcion, estado)
VALUES ('Joyeria', 'Con detalles', 1);
*/

-- Prueba #3: Agregar el valor por defecto en la columna estado
/*
INSERT INTO catalogos.categorias (nombre, descripcion)
VALUES ('Diversos tipos', 'Con tecnologia');
*/


------ TABLA 3 PRODUCTOS
CREATE TABLE catalogos.productos
(
    id_producto SERIAL PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL UNIQUE,
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
        CHECK (stock >= 0),

    CONSTRAINT chk_producto_estado
        CHECK (estado IN (0,1))
);


SELECT * FROM catalogos.categorias;
SELECT * FROM catalogos.marcas;


-- Prueba #1: NO permite valores nulos
/*
INSERT INTO catalogos.productos
(codigo, nombre, descripcion, precio, stock, id_categoria, id_marca, estado)
VALUES
('P001', NULL, 'Producto de prueba', 100.00, 10, 1, 1, 1);
*/


-- Prueba #2: NO permite códigos repetidos

/*
-- Primer registro: debe funcionar
INSERT INTO catalogos.productos
(codigo, nombre, descripcion, precio, stock, id_categoria, id_marca, estado)
VALUES
('P002', 'Audifonos Bluetooth', 'Audifonos inalambricos', 750.00, 15, 1, 1, 1);

-- Segundo registro: debe fallar porque P002 está repetido
INSERT INTO catalogos.productos
(codigo, nombre, descripcion, precio, stock, id_categoria, id_marca, estado)
VALUES
('P002', 'Audifonos Gamer', 'Audifonos para computadora', 900.00, 8, 1, 1, 1);
*/


-- Prueba #3: Usa valores por defecto
-- stock = 0
-- fecha_creacion = fecha actual
-- estado = 1

/*
INSERT INTO catalogos.productos
(codigo, nombre, descripcion, precio, id_categoria, id_marca)
VALUES
('P003', 'Teclado Mecanico', 'Teclado para computadora', 1200.00, 1, 1);
*/


-- CATEGORIAS ----

INSERT INTO catalogos.categorias
(nombre, descripcion, estado)
VALUES
('Joyeria', 'Con detalles', 1),

('Diversos tipos', 'Con tecnologia', 1),

('Telefonía', 'Celulares, accesorios y equipos móviles', 1),

('Computación', 'Equipos y accesorios para computadoras', 1),

('Audio', 'Audífonos, parlantes y dispositivos de sonido', 1),

('Hogar', 'Productos y dispositivos para el hogar', 1),

('Videojuegos', 'Consolas, controles y accesorios gaming', 1);


---------- Marcas --------------

INSERT INTO catalogos.marcas
(nombre, pais_origen, estado)
VALUES
('Samsung', 'Corea del Sur', 1),

('Apple', 'Estados Unidos', 1),

('Lenovo', 'Suiza', 1),

('Sony', 'Japón', 1),

('Xiaomi', 'China', 1);


---------- Productos -------------

INSERT INTO catalogos.productos
(codigo, nombre, descripcion, precio, stock, id_categoria, id_marca, estado)
VALUES
('P001', 'Samsung Galaxy A55', 'Smartphone Android de gama media',
12500.00, 12, 3, 1, 1),

('P002', 'iPhone 15', 'Smartphone Apple de 128 GB',
28500.00, 8, 3, 2, 1),

('P003', 'Xiaomi Redmi Note 13', 'Smartphone Android con pantalla AMOLED',
8200.00, 15, 3, 5, 1),

('P004', 'Lenovo IdeaPad 3', 'Laptop para estudio y trabajo',
19500.00, 6, 4, 3, 1),

('P005', 'Lenovo ThinkPad E14', 'Laptop empresarial de 14 pulgadas',
26500.00, 4, 4, 3, 1),

('P006', 'Apple Magic Mouse', 'Mouse inalambrico para equipos Apple',
3200.00, 7, 4, 2, 1),

('P007', 'Sony WH-1000XM5', 'Audifonos inalambricos con cancelacion de ruido',
11500.00, 6, 5, 4, 1),

('P008', 'Samsung Galaxy Buds FE', 'Audifonos inalambricos Bluetooth',
3200.00, 11, 5, 1, 1),

('P009', 'Xiaomi Sound Pocket', 'Parlante Bluetooth portatil',
1500.00, 18, 5, 5, 1),

('P010', 'Samsung Smart TV 55', 'Televisor inteligente 4K de 55 pulgadas',
18500.00, 5, 6, 1, 1),

('P011', 'Xiaomi Robot Vacuum S10', 'Aspiradora robot inteligente para el hogar',
9500.00, 4, 6, 5, 1),

('P012', 'Sony Bravia 50', 'Televisor Smart TV 4K de 50 pulgadas',
21000.00, 3, 6, 4, 1),

('P013', 'PlayStation 5 Slim', 'Consola de videojuegos PlayStation 5',
21500.00, 6, 7, 4, 1),

('P014', 'DualSense Wireless Controller', 'Control inalambrico para PlayStation 5',
3200.00, 12, 7, 4, 1),

('P015', 'Sony Pulse 3D', 'Audifonos gaming para PlayStation 5',
4200.00, 9, 7, 4, 1);


SELECT id_categoria, nombre
FROM catalogos.categorias
ORDER BY id_categoria;


SELECT * FROM catalogos.marcas;


-- PRUEBA DE CATEGORIAS

SELECT *
FROM catalogos.categorias
ORDER BY id_categoria;


-- PRUEBA DE PRODUCTOS

SELECT *
FROM catalogos.productos
ORDER BY id_producto;


------ Productos con precio entre 10000 y 20000

SELECT nombre, precio
FROM catalogos.productos
WHERE precio BETWEEN 10000 AND 20000;


-- Mostrar productos con estado activo y stock mayor a 0

SELECT *
FROM catalogos.productos
WHERE estado = 1
AND stock > 0;


-----------------

-- Mostrar productos con su categoría y marca

SELECT
    P.codigo,
    P.nombre,
    C.nombre AS categoria,
    M.nombre AS marca,
    P.precio,
    P.stock,
    P.fecha_creacion
FROM catalogos.productos P
INNER JOIN catalogos.categorias C
    ON P.id_categoria = C.id_categoria
INNER JOIN catalogos.marcas M
    ON P.id_marca = M.id_marca
WHERE P.estado = 1
ORDER BY P.nombre;


-- Comprobar la base de datos actual

SELECT current_database();


-- Revisar si existen productos con fechas futuras

SELECT codigo, nombre, fecha_creacion
FROM catalogos.productos
WHERE fecha_creacion > CURRENT_DATE;