package dao;

import interfaces.IEjecucionPowerShellDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import modelo.EjecucionPowerShell;
import modelo.EstadoComando;

public class EjecucionPowerShellDAO implements IEjecucionPowerShellDAO {

    private static EjecucionPowerShell map(ResultSet rs) throws SQLException {
        EjecucionPowerShell e = new EjecucionPowerShell();
        e.setId(rs.getLong("id"));
        e.setAdministradorId(rs.getInt("administrador_id"));
        e.setComando(rs.getString("comando"));
        e.setSalida(rs.getString("salida"));
        e.setEstado(JdbcUtil.getEnum(rs, "estado", EstadoComando.class));
        e.setFechaHora(JdbcUtil.getFecha(rs, "fecha_hora"));
        e.setIpOrigen(rs.getString("ip_origen"));
        return e;
    }

    @Override
    public long insertar(EjecucionPowerShell e) {
        return JdbcUtil.insertar(
                "INSERT INTO ejecucion_powershell (administrador_id, comando, salida, estado, fecha_hora, ip_origen) "
                + "VALUES (?,?,?,?,?,?)",
                e.getAdministradorId(), e.getComando(), e.getSalida() == null ? "" : e.getSalida(),
                e.getEstado(), e.getFechaHora(), e.getIpOrigen());
    }

    @Override
    public void actualizarResultado(EjecucionPowerShell e) {
        JdbcUtil.ejecutar("UPDATE ejecucion_powershell SET salida = ?, estado = ? WHERE id = ?",
                e.getSalida(), e.getEstado(), e.getId());
    }

    @Override
    public List<EjecucionPowerShell> listarUltimas(int limite) {
        return JdbcUtil.consultar("SELECT TOP (" + Math.max(1, limite) + ") id, administrador_id, comando, salida, "
                + "estado, fecha_hora, ip_origen FROM ejecucion_powershell ORDER BY fecha_hora DESC, id DESC",
                EjecucionPowerShellDAO::map);
    }
}
