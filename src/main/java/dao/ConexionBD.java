package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Conexion a SQL Server segun db.properties (src/main/resources). */
public final class ConexionBD {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("No se encontro db.properties en el classpath");
            }
            PROPS.load(in);
            Class.forName(PROPS.getProperty("db.driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private ConexionBD() {
    }

    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(
                PROPS.getProperty("db.url"),
                PROPS.getProperty("db.user"),
                PROPS.getProperty("db.password"));
    }

    /** Lee una propiedad reemplazando ${user.home} y otras propiedades del sistema. */
    public static String propiedad(String clave, String porDefecto) {
        String v = PROPS.getProperty(clave, porDefecto);
        if (v == null) {
            return null;
        }
        return v.replace("${user.home}", System.getProperty("user.home"));
    }
}
