package dao;

import interfaces.IAuditoriaDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import modelo.RegistroAuditoria;
import modelo.ResultadoOperacion;
import modelo.TipoOperacion;

public class AuditoriaDAO implements IAuditoriaDAO {

    private static final String COLS
            = "id, fecha_hora, usuario_id, operacion, recurso_id, ruta, transferencia_id, resultado, ip_origen, detalle, hash_integridad";

    private static RegistroAuditoria map(ResultSet rs) throws SQLException {
        RegistroAuditoria r = new RegistroAuditoria();
        r.setId(rs.getLong("id"));
        r.setFechaHora(JdbcUtil.getFecha(rs, "fecha_hora"));
        r.setUsuarioId(rs.getInt("usuario_id"));
        r.setOperacion(JdbcUtil.getEnum(rs, "operacion", TipoOperacion.class));
        r.setRecursoId(JdbcUtil.getInt(rs, "recurso_id"));
        r.setRuta(rs.getString("ruta"));
        r.setTransferenciaId(JdbcUtil.getLong(rs, "transferencia_id"));
        r.setResultado(JdbcUtil.getEnum(rs, "resultado", ResultadoOperacion.class));
        r.setIpOrigen(rs.getString("ip_origen"));
        r.setDetalle(rs.getString("detalle"));
        r.setHashIntegridad(rs.getString("hash_integridad"));
        return r;
    }

    @Override
    public long insertar(RegistroAuditoria r) {
        return JdbcUtil.insertar(
                "INSERT INTO registro_auditoria (fecha_hora, usuario_id, operacion, recurso_id, ruta, transferencia_id, "
                + "resultado, ip_origen, detalle, hash_integridad) VALUES (?,?,?,?,?,?,?,?,?,?)",
                r.getFechaHora(), r.getUsuarioId(), r.getOperacion(), r.getRecursoId(), r.getRuta(),
                r.getTransferenciaId(), r.getResultado(), r.getIpOrigen(), r.getDetalle(), r.getHashIntegridad());
    }

    @Override
    public List<RegistroAuditoria> listarUltimos(int limite) {
        return JdbcUtil.consultar("SELECT TOP (" + Math.max(1, limite) + ") " + COLS
                + " FROM registro_auditoria ORDER BY fecha_hora DESC, id DESC", AuditoriaDAO::map);
    }
}
