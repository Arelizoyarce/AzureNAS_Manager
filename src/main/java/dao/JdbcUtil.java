package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Ayudas JDBC para que los DAO sean cortos. Los enums se guardan por nombre. */
public final class JdbcUtil {

    @FunctionalInterface
    public interface RowMapper<T> {

        T map(ResultSet rs) throws SQLException;
    }

    private JdbcUtil() {
    }

    private static void enlazar(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object v = params[i];
            if (v instanceof Enum) {
                v = ((Enum<?>) v).name();
            }
            if (v instanceof LocalDateTime) {
                v = Timestamp.valueOf((LocalDateTime) v);
            }
            if (v == null) {
                ps.setNull(i + 1, Types.VARCHAR);
            } else {
                ps.setObject(i + 1, v);
            }
        }
    }

    public static <T> List<T> consultar(String sql, RowMapper<T> mapper, Object... params) {
        try (Connection c = ConexionBD.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            enlazar(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapper.map(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new DAOException("Error en consulta: " + sql, e);
        }
    }

    public static <T> T uno(String sql, RowMapper<T> mapper, Object... params) {
        List<T> l = consultar(sql, mapper, params);
        return l.isEmpty() ? null : l.get(0);
    }

    public static int ejecutar(String sql, Object... params) {
        try (Connection c = ConexionBD.obtener(); PreparedStatement ps = c.prepareStatement(sql)) {
            enlazar(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al ejecutar: " + sql, e);
        }
    }

    public static long insertar(String sql, Object... params) {
        try (Connection c = ConexionBD.obtener();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            enlazar(ps, params);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                return k.next() ? k.getLong(1) : -1L;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al insertar: " + sql, e);
        }
    }

    public static Integer getInt(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }

    public static Long getLong(ResultSet rs, String col) throws SQLException {
        long v = rs.getLong(col);
        return rs.wasNull() ? null : v;
    }

    public static LocalDateTime getFecha(ResultSet rs, String col) throws SQLException {
        Timestamp t = rs.getTimestamp(col);
        return t == null ? null : t.toLocalDateTime();
    }

public static <E extends Enum<E>> E getEnum(ResultSet rs, String col, Class<E> tipo) throws SQLException {
    String v = rs.getString(col);
    if (v == null) {
        return null;
    }
    try {
        return Enum.valueOf(tipo, v.trim().toUpperCase().replace(' ', '_'));
    } catch (IllegalArgumentException e) {
        java.util.logging.Logger.getLogger(JdbcUtil.class.getName()).warning(
                "Valor desconocido '" + v + "' para " + tipo.getSimpleName() + " (columna " + col + ")");
        return null;
    }
}
}
