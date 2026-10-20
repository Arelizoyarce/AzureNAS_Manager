package dao;

import interfaces.ITransferenciaDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import modelo.EstadoTransferencia;
import modelo.TipoTransferencia;
import modelo.Transferencia;

public class TransferenciaDAO implements ITransferenciaDAO {

    private static final String COLS
            = "id, usuario_id, tipo, recurso_id, ruta_remota, nombre_archivo, recurso_destino_id, ruta_destino, "
            + "tamano_bytes, sobrescribir, objetos_totales, objetos_procesados, progreso, estado, mensaje_error, "
            + "fecha_solicitud, fecha_inicio, fecha_fin";

    private static Transferencia map(ResultSet rs) throws SQLException {
        Transferencia t = new Transferencia();
        t.setId(rs.getLong("id"));
        t.setUsuarioId(rs.getInt("usuario_id"));
        t.setTipo(JdbcUtil.getEnum(rs, "tipo", TipoTransferencia.class));
        t.setRecursoId(rs.getInt("recurso_id"));
        t.setRutaRemota(rs.getString("ruta_remota"));
        t.setNombreArchivo(rs.getString("nombre_archivo"));
        t.setRecursoDestinoId(JdbcUtil.getInt(rs, "recurso_destino_id"));
        t.setRutaDestino(rs.getString("ruta_destino"));
        t.setTamanoBytes(JdbcUtil.getLong(rs, "tamano_bytes"));
        t.setSobrescribir(rs.getBoolean("sobrescribir"));
        t.setObjetosTotales(JdbcUtil.getInt(rs, "objetos_totales"));
        t.setObjetosProcesados(JdbcUtil.getInt(rs, "objetos_procesados"));
        t.setProgreso(rs.getInt("progreso"));
        t.setEstado(JdbcUtil.getEnum(rs, "estado", EstadoTransferencia.class));
        t.setMensajeError(rs.getString("mensaje_error"));
        t.setFechaSolicitud(JdbcUtil.getFecha(rs, "fecha_solicitud"));
        t.setFechaInicio(JdbcUtil.getFecha(rs, "fecha_inicio"));
        t.setFechaFin(JdbcUtil.getFecha(rs, "fecha_fin"));
        return t;
    }

    @Override
    public long insertar(Transferencia t) {
        return JdbcUtil.insertar(
                "INSERT INTO transferencia (usuario_id, tipo, recurso_id, ruta_remota, nombre_archivo, recurso_destino_id, "
                + "ruta_destino, tamano_bytes, sobrescribir, objetos_totales, objetos_procesados, progreso, estado, "
                + "mensaje_error, fecha_solicitud, fecha_inicio, fecha_fin) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                t.getUsuarioId(), t.getTipo(), t.getRecursoId(), t.getRutaRemota(), t.getNombreArchivo(),
                t.getRecursoDestinoId(), t.getRutaDestino(), t.getTamanoBytes() == null ? 0L : t.getTamanoBytes(),
                t.isSobrescribir(), t.getObjetosTotales(), t.getObjetosProcesados(), t.getProgreso(), t.getEstado(),
                t.getMensajeError(), t.getFechaSolicitud(), t.getFechaInicio(), t.getFechaFin());
    }

    @Override
    public void actualizar(Transferencia t) {
        JdbcUtil.ejecutar("UPDATE transferencia SET tamano_bytes = ?, objetos_totales = ?, objetos_procesados = ?, "
                + "progreso = ?, estado = ?, mensaje_error = ?, fecha_inicio = ?, fecha_fin = ? WHERE id = ?",
                t.getTamanoBytes() == null ? 0L : t.getTamanoBytes(), t.getObjetosTotales(), t.getObjetosProcesados(),
                t.getProgreso(), t.getEstado(), t.getMensajeError(), t.getFechaInicio(), t.getFechaFin(), t.getId());
    }

    @Override
    public Transferencia buscarPorId(long id) {
        return JdbcUtil.uno("SELECT " + COLS + " FROM transferencia WHERE id = ?", TransferenciaDAO::map, id);
    }

    @Override
    public List<Transferencia> listarPorUsuario(int usuarioId, int limite) {
        return JdbcUtil.consultar("SELECT TOP (" + Math.max(1, limite) + ") " + COLS
                + " FROM transferencia WHERE usuario_id = ? ORDER BY fecha_solicitud DESC, id DESC",
                TransferenciaDAO::map, usuarioId);
    }

    @Override
    public List<Transferencia> listarRecientes(int limite) {
        return JdbcUtil.consultar("SELECT TOP (" + Math.max(1, limite) + ") " + COLS
                + " FROM transferencia ORDER BY fecha_solicitud DESC, id DESC", TransferenciaDAO::map);
    }
}
