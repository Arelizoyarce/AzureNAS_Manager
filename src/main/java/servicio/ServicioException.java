package servicio;

/** Error de negocio cuyo mensaje se puede mostrar al usuario. */
public class ServicioException extends RuntimeException {

    public ServicioException(String mensaje) {
        super(mensaje);
    }

    public ServicioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
