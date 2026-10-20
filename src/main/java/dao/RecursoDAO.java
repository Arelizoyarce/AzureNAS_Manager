package dao;

import interfaces.IRecursoDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Recurso;
import modelo.TipoRecurso;

public class RecursoDAO implements IRecursoDAO {

    private static final String COLS
            = "id, cuenta_id, area_id, tipo, nombre, tamano_bytes, cuota_gb, ultima_modificacion, metadatos_actualizados_en";

    private static Recurso map(ResultSet rs) throws SQLException {
        Recurso r = new Recurso();
        r.setId(rs.getInt("id"));
        r.setCuentaId(rs.getInt("cuenta_id"));
        r.setAreaId(rs.getInt("area_id"));
        r.setTipo(JdbcUtil.getEnum(rs, "tipo", TipoRecurso.class));
        r.setNombre(rs.getString("nombre"));
        r.setTamanoBytes(JdbcUtil.getLong(rs, "tamano_bytes"));
        r.setCuotaGb(JdbcUtil.getInt(rs, "cuota_gb"));
        r.setUltimaModificacion(JdbcUtil.getFecha(rs, "ultima_modificacion"));
        r.setMetadatosActualizadosEn(JdbcUtil.getFecha(rs, "metadatos_actualizados_en"));
        return r;
    }

    @Override
    public List<Recurso> buscar(String texto, TipoRecurso tipo, Integer usuarioId, Integer areaId) {
        StringBuilder sql = new StringBuilder("SELECT " + COLS + " FROM recurso WHERE 1 = 1");
        List<Object> p = new ArrayList<>();
        if (texto != null && !texto.trim().isEmpty()) {
            sql.append(" AND nombre LIKE ?");
            p.add("%" + texto.trim() + "%");
        }
        if (tipo != null) {
            sql.append(" AND tipo = ?");
            p.add(tipo);
        }
        if (usuarioId != null) {
            sql.append(" AND (area_id = ? OR id IN (SELECT recurso_id FROM acceso_recurso WHERE usuario_id = ?))");
            p.add(areaId == null ? -1 : areaId);
            p.add(usuarioId);
        }
        sql.append(" ORDER BY nombre");
        return JdbcUtil.consultar(sql.toString(), RecursoDAO::map, p.toArray());
    }

    @Override
    public Recurso buscarPorId(int id) {
        return JdbcUtil.uno("SELECT " + COLS + " FROM recurso WHERE id = ?", RecursoDAO::map, id);
    }

    @Override
    public int insertar(Recurso r) {
        return (int) JdbcUtil.insertar(
                "INSERT INTO recurso (cuenta_id, area_id, tipo, nombre, tamano_bytes, cuota_gb, ultima_modificacion, metadatos_actualizados_en) "
                + "VALUES (?, ?, ?, ?, 0, ?, SYSDATETIME(), SYSDATETIME())",
                r.getCuentaId(), r.getAreaId(), r.getTipo(), r.getNombre(), r.getCuotaGb());
    }

    @Override
    public void actualizarTamano(int id, long tamanoBytes) {
        JdbcUtil.ejecutar("UPDATE recurso SET tamano_bytes = ?, ultima_modificacion = SYSDATETIME(), "
                + "metadatos_actualizados_en = SYSDATETIME() WHERE id = ?", tamanoBytes, id);
    }
}
