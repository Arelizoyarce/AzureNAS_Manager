package dao;

/** Error no controlado de acceso a datos. */
public class DAOException extends RuntimeException {

    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
