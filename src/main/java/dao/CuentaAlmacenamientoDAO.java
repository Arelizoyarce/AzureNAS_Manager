package dao;

import interfaces.ICuentaAlmacenamientoDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import modelo.CuentaAlmacenamiento;
import modelo.TipoCredencial;

public class CuentaAlmacenamientoDAO implements ICuentaAlmacenamientoDAO {

    private static final String COLS
            = "id, nombre, region, tipo_credencial, credencial_cifrada, espacio_asignado_bytes";

    private static CuentaAlmacenamiento map(ResultSet rs) throws SQLException {
        CuentaAlmacenamiento c = new CuentaAlmacenamiento();
        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre"));
        c.setRegion(rs.getString("region"));
        c.setTipoCredencial(JdbcUtil.getEnum(rs, "tipo_credencial", TipoCredencial.class));
        c.setCredencialCifrada(rs.getString("credencial_cifrada"));
        c.setEspacioAsignadoBytes(JdbcUtil.getLong(rs, "espacio_asignado_bytes"));
        return c;
    }

    @Override
    public List<CuentaAlmacenamiento> listar() {
        return JdbcUtil.consultar("SELECT " + COLS + " FROM cuenta_almacenamiento ORDER BY nombre",
                CuentaAlmacenamientoDAO::map);
    }

    @Override
    public CuentaAlmacenamiento buscarPorId(int id) {
        return JdbcUtil.uno("SELECT " + COLS + " FROM cuenta_almacenamiento WHERE id = ?",
                CuentaAlmacenamientoDAO::map, id);
    }

    @Override
    public int insertar(CuentaAlmacenamiento c) {
        return (int) JdbcUtil.insertar(
                "INSERT INTO cuenta_almacenamiento (nombre, region, tipo_credencial, credencial_cifrada, espacio_asignado_bytes) "
                + "VALUES (?, ?, ?, ?, ?)",
                c.getNombre(), c.getRegion(), c.getTipoCredencial(), c.getCredencialCifrada(), c.getEspacioAsignadoBytes());
    }
}
