# AzureNAS Manager (NetBeans / Maven / Servlets + JSP)

Proyecto web MVC con la misma estructura de GestorUsuariosMVC y las 4 capas de la arquitectura.

## Abrir en NetBeans
1. File > Open Project > carpeta `AzureNASManager` (proyecto Maven, empaquetado WAR).
2. Servidor: Apache Tomcat 9 (usa `javax.servlet`). NetBeans 12 no reconoce Tomcat 10.
3. Java 17 o superior.

## Configurar
1. SSMS: crea las tablas de `gestion_almacenamiento` y ejecuta `sql/datos_iniciales.sql`.
2. Edita `src/main/resources/db.properties` (usuario/clave de SQL Server, `app.clave`, ruta `storage.base`).
3. SQL Server debe aceptar TCP/IP en el puerto 1433 y autenticacion SQL.
4. Run (F6). Ingreso: `admin` / `Admin2026!`.

## Paquetes (Source Packages)
| Paquete | Contenido |
|---|---|
| controlador | Servlets: Login, Logout, Dashboard, Usuario, Area, Cuenta, Recurso, Transferencia, Auditoria, PowerShell |
| dao | ConexionBD, JdbcUtil y un DAO por tabla (8 tablas) |
| interfaces | Contratos de DAO, IAlmacenamientoClient, IComandoExecutor, INotificador |
| modelo | 8 entidades (una por tabla) y 9 enums |
| notificaciones | NotificadorEmail (simulado en log) |
| servicio | Logica de negocio, cliente Azure simulado, ejecutor PowerShell |
| filtro | AuthFilter (sesion, roles admin, CSRF) |
| util | Hash de contrasenas, cifrado AES-GCM, formato, parametros, sesion |

## Capas
- Capa 1 Presentacion: JSP + HTML/CSS (Web Pages)
- Capa 2 Aplicacion: servlets + servicios
- Capa 3 Integracion/persistencia: IAlmacenamientoClient (simulado), PowerShellExecutor, DAOs JDBC
- Capa 4 Datos: carpetas locales `storage.base` (Azure simulado), PowerShell local, SQL Server

## Notas
- El almacenamiento simulado crea carpetas en `~/azurenas-simulado/{cuentaId}/{recurso}/`.
- PowerShell solo permite cmdlets `Get-*` simples; se puede apagar con `powershell.habilitado=false`.
- Las contrasenas se guardan como `salt$sha256`; las credenciales de cuenta, cifradas con AES-GCM.
