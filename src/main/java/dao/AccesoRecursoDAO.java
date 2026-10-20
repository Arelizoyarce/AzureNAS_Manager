package dao;

import interfaces.IAccesoRecursoDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import modelo.AccesoRecurso;
import modelo.NivelAcceso;

public class AccesoRecursoDAO implements IAccesoRecursoDAO {

    private static AccesoRecurso map(ResultSet rs) throws SQLException {
        AccesoRecurso a = new AccesoRecurso();
        a.setUsuarioId(rs.getInt("usuario_id"));
        a.setRecursoId(rs.getInt("recurso_id"));
        a.setNivel(JdbcUtil.getEnum(rs, "nivel", NivelAcceso.class));
        a.setFechaAsignacion(JdbcUtil.getFecha(rs, "fecha_asignacion"));
        return a;
    }

    @Override
    public NivelAcceso nivelDe(int usuarioId, int recursoId) {
        AccesoRecurso a = JdbcUtil.uno(
                "SELECT usuario_id, recurso_id, nivel, fecha_asignacion FROM acceso_recurso WHERE usuario_id = ? AND recurso_id = ?",
                AccesoRecursoDAO::map, usuarioId, recursoId);
        return a == null ? null : a.getNivel();
    }

    @Override
    public List<AccesoRecurso> listarPorUsuario(int usuarioId) {
        return JdbcUtil.consultar(
                "SELECT usuario_id, recurso_id, nivel, fecha_asignacion FROM acceso_recurso WHERE usuario_id = ?",
                AccesoRecursoDAO::map, usuarioId);
    }

    @Override
    public void asignar(AccesoRecurso a) {
        int n = JdbcUtil.ejecutar("UPDATE acceso_recurso SET nivel = ?, fecha_asignacion = SYSDATETIME() "
                + "WHERE usuario_id = ? AND recurso_id = ?", a.getNivel(), a.getUsuarioId(), a.getRecursoId());
        if (n == 0) {
            JdbcUtil.ejecutar("INSERT INTO acceso_recurso (usuario_id, recurso_id, nivel, fecha_asignacion) "
                    + "VALUES (?, ?, ?, SYSDATETIME())", a.getUsuarioId(), a.getRecursoId(), a.getNivel());
        }
    }

    @Override
    public void revocar(int usuarioId, int recursoId) {
        JdbcUtil.ejecutar("DELETE FROM acceso_recurso WHERE usuario_id = ? AND recurso_id = ?", usuarioId, recursoId);
    }
}
