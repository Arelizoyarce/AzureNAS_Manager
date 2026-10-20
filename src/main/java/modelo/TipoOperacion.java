package modelo;

/** Valores del tipo TipoOperacion (se guardan como texto en la BD). */
public enum TipoOperacion {
    LOGIN,
    LOGOUT,
    SUBIDA,
    DESCARGA,
    SINCRONIZACION,
    ELIMINACION,
    CONSULTA,
    COMANDO_POWERSHELL,
    ADMINISTRACION
}
