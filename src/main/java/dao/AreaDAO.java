package dao;

import interfaces.IAreaDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import modelo.Area;

public class AreaDAO implements IAreaDAO {

    private static Area map(ResultSet rs) throws SQLException {
        Area a = new Area();
        a.setId(rs.getInt("id"));
        a.setNombre(rs.getString("nombre"));
        return a;
    }

    @Override
    public List<Area> listar() {
        return JdbcUtil.consultar("SELECT id, nombre FROM area ORDER BY nombre", AreaDAO::map);
    }

    @Override
    public Area buscarPorId(int id) {
        return JdbcUtil.uno("SELECT id, nombre FROM area WHERE id = ?", AreaDAO::map, id);
    }

    @Override
    public int insertar(Area a) {
        return (int) JdbcUtil.insertar("INSERT INTO area (nombre) VALUES (?)", a.getNombre());
    }
}
