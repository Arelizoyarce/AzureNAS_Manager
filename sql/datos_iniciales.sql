-- =====================================================================
-- Datos iniciales para AzureNAS Manager (SQL Server / SSMS)
-- Ejecutar DESPUES de crear las tablas de gestion_almacenamiento.
-- Los campos tipo enum se guardan como TEXTO con estos valores:
--   usuario.rol                  : ADMIN | OPERADOR | LECTOR
--   acceso_recurso.nivel         : LECTURA | ESCRITURA | ADMIN
--   cuenta.tipo_credencial       : ACCOUNT_KEY | SAS | CONNECTION_STRING
--   recurso.tipo                 : FILE_SHARE | BLOB_CONTAINER
--   transferencia.tipo           : SUBIDA | DESCARGA | SINCRONIZACION
--   transferencia.estado         : PENDIENTE | EN_CURSO | COMPLETADA | FALLIDA | CANCELADA
--   ejecucion_powershell.estado  : PENDIENTE | EXITOSO | FALLIDO
--   registro_auditoria.operacion : LOGIN | LOGOUT | SUBIDA | DESCARGA | SINCRONIZACION |
--                                  ELIMINACION | CONSULTA | COMANDO_POWERSHELL | ADMINISTRACION
--   registro_auditoria.resultado : EXITO | FALLO | DENEGADO
-- Si tus CHECK/tipos usan otros valores, cambia los enums en el paquete modelo.
-- =====================================================================
USE gestion_almacenamiento;
GO

IF NOT EXISTS (SELECT 1 FROM area WHERE nombre = N'Tecnologia')
    INSERT INTO area (nombre) VALUES (N'Tecnologia');
IF NOT EXISTS (SELECT 1 FROM area WHERE nombre = N'Finanzas')
    INSERT INTO area (nombre) VALUES (N'Finanzas');

DECLARE @area INT = (SELECT id FROM area WHERE nombre = N'Tecnologia');

-- Usuario: admin / Admin2026!   (cambia la contrasena al primer ingreso)
IF NOT EXISTS (SELECT 1 FROM usuario WHERE username = 'admin')
    INSERT INTO usuario (username, password_hash, nombre_completo, rol, area_id, activo, fecha_creacion)
    VALUES ('admin', 'a1b2c3d4e5f60718$6c48c44a7cac27704e379e458c272cc874c1d48b682b665b593b0adf80219a35', N'Administrador del sistema', 'ADMIN', @area, 1, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM cuenta_almacenamiento WHERE nombre = 'azurenasdemo')
    INSERT INTO cuenta_almacenamiento (nombre, region, tipo_credencial, credencial_cifrada, espacio_asignado_bytes)
    VALUES ('azurenasdemo', 'eastus', 'ACCOUNT_KEY', 'sin-credencial', 107374182400);

DECLARE @cuenta INT = (SELECT id FROM cuenta_almacenamiento WHERE nombre = 'azurenasdemo');

IF NOT EXISTS (SELECT 1 FROM recurso WHERE cuenta_id = @cuenta AND nombre = 'documentos')
    INSERT INTO recurso (cuenta_id, area_id, tipo, nombre, tamano_bytes, cuota_gb, ultima_modificacion, metadatos_actualizados_en)
    VALUES (@cuenta, @area, 'FILE_SHARE', 'documentos', 0, 50, SYSDATETIME(), SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM recurso WHERE cuenta_id = @cuenta AND nombre = 'respaldo')
    INSERT INTO recurso (cuenta_id, area_id, tipo, nombre, tamano_bytes, cuota_gb, ultima_modificacion, metadatos_actualizados_en)
    VALUES (@cuenta, @area, 'BLOB_CONTAINER', 'respaldo', 0, 50, SYSDATETIME(), SYSDATETIME());
GO
