-- =====================================================================
-- Sistema de Gestión de Sismos del Perú (CSSP) - Datos de ejemplo
-- Ejecutar después de 01_schema.sql. Los sismos son simulados con fines académicos.
-- =====================================================================

USE cssp_sismos;

-- Ubigeo (INEI): 12 departamentos, 18 provincias y 22 distritos
INSERT INTO departamento (id, codigo, nombre) VALUES
(1, '02', 'Áncash'),
(2, '04', 'Arequipa'),
(3, '07', 'Callao'),
(4, '08', 'Cusco'),
(5, '11', 'Ica'),
(6, '13', 'La Libertad'),
(7, '15', 'Lima'),
(8, '16', 'Loreto'),
(9, '18', 'Moquegua'),
(10, '20', 'Piura'),
(11, '21', 'Puno'),
(12, '23', 'Tacna');

INSERT INTO provincia (id, codigo, nombre, departamento_id) VALUES
(1, '0218', 'Santa', 1),
(2, '0401', 'Arequipa', 2),
(3, '0405', 'Caylloma', 2),
(4, '0701', 'Callao', 3),
(5, '0801', 'Cusco', 4),
(6, '0809', 'La Convención', 4),
(7, '1101', 'Ica', 5),
(8, '1103', 'Nasca', 5),
(9, '1301', 'Trujillo', 6),
(10, '1501', 'Lima', 7),
(11, '1505', 'Cañete', 7),
(12, '1601', 'Maynas', 8),
(13, '1801', 'Mariscal Nieto', 9),
(14, '1803', 'Ilo', 9),
(15, '2001', 'Piura', 10),
(16, '2101', 'Puno', 11),
(17, '2301', 'Tacna', 12),
(18, '2302', 'Candarave', 12);

INSERT INTO distrito (id, codigo, nombre, provincia_id) VALUES
(1, '021801', 'Chimbote', 1),
(2, '040101', 'Arequipa', 2),
(3, '040501', 'Chivay', 3),
(4, '040503', 'Cabanaconde', 3),
(5, '070101', 'Callao', 4),
(6, '080101', 'Cusco', 5),
(7, '080902', 'Echarati', 6),
(8, '110101', 'Ica', 7),
(9, '110301', 'Nasca', 8),
(10, '110304', 'Marcona', 8),
(11, '110305', 'Vista Alegre', 8),
(12, '130101', 'Trujillo', 9),
(13, '150101', 'Lima', 10),
(14, '150501', 'San Vicente de Cañete', 11),
(15, '160101', 'Iquitos', 12),
(16, '180101', 'Moquegua', 13),
(17, '180301', 'Ilo', 14),
(18, '200101', 'Piura', 15),
(19, '210101', 'Puno', 16),
(20, '230101', 'Tacna', 17),
(21, '230108', 'Pocollay', 17),
(22, '230201', 'Candarave', 18);

-- Estaciones de monitoreo (TAC-02 está inactiva: no puede recibir nuevos registros)
INSERT INTO estacion_monitoreo (id, codigo, nombre, distrito_id, latitud, longitud, fecha_instalacion, estado) VALUES
(1, 'ARE-01', 'Estación Chivay', 3, -15.6380, -71.6010, '2019-03-12', 'Activa'),
(2, 'LIM-01', 'Estación Lima Centro', 13, -12.0460, -77.0430, '2016-05-20', 'Activa'),
(3, 'LIM-02', 'Estación Cañete', 14, -13.0780, -76.3870, '2020-06-05', 'Activa'),
(4, 'CUS-01', 'Estación Echarati', 7, -12.7700, -72.5800, '2018-11-20', 'Activa'),
(5, 'ICA-01', 'Estación Nasca', 9, -14.8350, -74.9380, '2021-01-15', 'Activa'),
(6, 'TAC-01', 'Estación Pocollay', 21, -17.9950, -70.2210, '2017-08-30', 'Activa'),
(7, 'TAC-02', 'Estación Candarave', 22, -17.2680, -70.2500, '2015-04-02', 'Inactiva'),
(8, 'PIU-01', 'Estación Piura', 18, -5.1950, -80.6320, '2018-02-14', 'Activa'),
(9, 'MOQ-01', 'Estación Ilo', 17, -17.6400, -71.3380, '2019-09-09', 'Activa'),
(10, 'ANC-01', 'Estación Chimbote', 1, -9.0750, -78.5930, '2022-07-01', 'Activa');

-- 30 sismos simulados de 2026 (SIS-001, SIS-018 y SIS-026 están En seguimiento: no se pueden eliminar)
INSERT INTO sismo (id, codigo, fecha_hora, magnitud, profundidad, latitud, longitud, distrito_id, referencia, intensidad, estado, estacion_id) VALUES
(1, 'SIS-001', '2026-08-18 14:32:00', 5.6, 34, -15.7100, -71.6800, 3, '12 km al SO de Chivay', 'IV (MM)', 'En seguimiento', 1),
(2, 'SIS-002', '2026-08-02 03:15:00', 4.5, 48, -13.1500, -76.5200, 14, '18 km al O de San Vicente de Cañete', 'III (MM)', 'Registrado', 3),
(3, 'SIS-003', '2026-07-21 22:47:00', 4.8, 110, -12.8100, -72.6600, 7, 'Zona rural de Echarati', 'III (MM)', 'En evaluación', 4),
(4, 'SIS-004', '2026-07-09 10:05:00', 5.1, 28, -14.9200, -75.0800, 11, 'Litoral frente a Vista Alegre', 'V (MM)', 'Cerrado', 5),
(5, 'SIS-005', '2026-06-30 18:40:00', 4.2, 62, -18.0600, -70.3100, 21, '8 km al S de Pocollay', 'III (MM)', 'Registrado', 6),
(6, 'SIS-006', '2026-01-07 05:22:00', 4.0, 45, -12.2100, -77.2500, 5, '30 km al O del Callao', 'III (MM)', 'Cerrado', 2),
(7, 'SIS-007', '2026-01-19 16:48:00', 4.3, 105, -16.3100, -71.6200, 2, '15 km al N de Arequipa', 'III (MM)', 'Cerrado', 1),
(8, 'SIS-008', '2026-02-03 09:11:00', 5.4, 32, -15.4700, -75.2800, 10, 'Frente a la costa de Marcona', 'IV (MM)', 'Cerrado', 5),
(9, 'SIS-009', '2026-02-14 23:56:00', 3.9, 121, -3.9200, -73.4100, 15, '40 km al SO de Iquitos', 'II (MM)', 'Registrado', NULL),
(10, 'SIS-010', '2026-02-27 12:30:00', 4.6, 38, -5.4200, -80.9500, 18, '35 km al SO de Piura', 'III (MM)', 'En evaluación', 8),
(11, 'SIS-011', '2026-03-08 02:04:00', 4.1, 55, -17.7300, -71.5200, 17, '22 km al SO de Ilo', 'III (MM)', 'Cerrado', 9),
(12, 'SIS-012', '2026-03-15 19:25:00', 4.9, 18, -15.6600, -72.0500, 4, '10 km al O de Cabanaconde', 'IV (MM)', 'Cerrado', 1),
(13, 'SIS-013', '2026-03-29 07:40:00', 3.7, 40, -9.2100, -78.8100, 1, '25 km al SO de Chimbote', 'II (MM)', 'Registrado', 10),
(14, 'SIS-014', '2026-04-06 21:13:00', 5.0, 165, -15.9500, -70.1800, 19, '20 km al SE de Puno', 'III (MM)', 'En evaluación', NULL),
(15, 'SIS-015', '2026-04-17 11:58:00', 4.4, 52, -12.3500, -77.3800, 13, '45 km al SO de Lima', 'III (MM)', 'Cerrado', 2),
(16, 'SIS-016', '2026-04-25 04:21:00', 3.8, 75, -13.6100, -72.0900, 6, '12 km al O de Cusco', 'II (MM)', 'Registrado', NULL),
(17, 'SIS-017', '2026-05-02 15:07:00', 4.7, 41, -14.2100, -75.9800, 8, '30 km al SO de Ica', 'III (MM)', 'Cerrado', 5),
(18, 'SIS-018', '2026-05-14 08:33:00', 5.9, 26, -8.3500, -79.4100, 12, 'Frente a la costa de Trujillo', 'V (MM)', 'En seguimiento', NULL),
(19, 'SIS-019', '2026-05-28 20:49:00', 4.2, 98, -17.2800, -70.9600, 16, '15 km al N de Moquegua', 'III (MM)', 'En evaluación', 9),
(20, 'SIS-020', '2026-06-09 13:16:00', 3.8, 112, -16.4700, -71.4900, 2, '10 km al SE de Arequipa', 'II (MM)', 'Cerrado', 1),
(21, 'SIS-021', '2026-06-21 01:52:00', 4.5, 33, -17.3300, -70.3600, 22, 'Cerca de Candarave', 'III (MM)', 'Registrado', NULL),
(22, 'SIS-022', '2026-07-03 17:27:00', 4.9, 44, -12.1800, -77.3100, 5, '35 km al SO del Callao', 'IV (MM)', 'En evaluación', 2),
(23, 'SIS-023', '2026-07-30 06:09:00', 4.0, 58, -14.9100, -74.8500, 9, '10 km al E de Nasca', 'III (MM)', 'Registrado', 5),
(24, 'SIS-024', '2026-08-11 10:44:00', 5.2, 30, -5.6100, -81.1200, 18, 'Frente a la costa de Piura', 'IV (MM)', 'En evaluación', 8),
(25, 'SIS-025', '2026-08-26 22:18:00', 4.3, 140, -12.5500, -72.7100, 7, '30 km al S de Echarati', 'III (MM)', 'Registrado', 4),
(26, 'SIS-026', '2026-09-05 04:37:00', 6.1, 22, -15.8300, -71.8400, 3, '25 km al SO de Chivay', 'VI (MM)', 'En seguimiento', 1),
(27, 'SIS-027', '2026-09-12 14:02:00', 4.1, 47, -13.2100, -76.6100, 14, '25 km al SO de San Vicente de Cañete', 'III (MM)', 'Registrado', 3),
(28, 'SIS-028', '2026-09-24 09:55:00', 3.6, 60, -18.1200, -70.4500, 20, '15 km al SO de Tacna', 'II (MM)', 'Registrado', 6),
(29, 'SIS-029', '2026-10-01 19:31:00', 4.8, 36, -9.3400, -78.9500, 1, '40 km al SO de Chimbote', 'IV (MM)', 'Registrado', 10),
(30, 'SIS-030', '2026-10-08 07:14:00', 4.4, 95, -16.4200, -71.5800, 2, 'Arequipa ciudad', 'III (MM)', 'Registrado', 1);

-- Reporte de afectación y seguimiento del sismo SIS-001
INSERT INTO reporte_afectacion (id, sismo_id, ubicacion, fecha_hora, tipo_afectacion, descripcion, nivel, personas_afectadas, estado) VALUES
(1, 1, 'Chivay, Caylloma, Arequipa', '2026-08-18 16:10:00', 'Vivienda', 'Fisuras en muros de adobe de viviendas del casco urbano.', 'Medio', 35, 'Verificado');

INSERT INTO seguimiento_sismo (id, sismo_id, fecha_actualizacion, responsable, estado, observaciones, acciones_realizadas) VALUES
(1, 1, '2026-08-19 09:00:00', 'Analista de turno', 'En seguimiento', 'Se confirman daños leves en viviendas de adobe.', 'Coordinación con el gobierno local para la evaluación de viviendas.');
