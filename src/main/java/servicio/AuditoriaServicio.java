package servicio;

import dao.AuditoriaDAO;
import dao.DAOException;
import interfaces.IAuditoriaDAO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import modelo.RegistroAuditoria;
import modelo.ResultadoOperacion;
import modelo.TipoOperacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.LogUtil;
import util.PasswordUtil;

public class AuditoriaServicio {

    private static final Logger logger = LoggerFactory.getLogger(AuditoriaServicio.class);
    /** Logger aparte (archivo operaciones.log, ver logback.xml) para el monitoreo de la aplicacion. */
    private static final Logger operaciones = LoggerFactory.getLogger("operaciones");
    private final IAuditoriaDAO dao;

    public AuditoriaServicio() {
        this(new AuditoriaDAO());
    }

    public AuditoriaServicio(IAuditoriaDAO dao) {
        this.dao = dao;
    }

    public void registrar(Integer usuarioId, TipoOperacion op, Integer recursoId, String ruta,
            Long transferenciaId, ResultadoOperacion resultado, String ip, String detalle) {
        if (usuarioId == null) {
            logger.warn("Auditoria sin usuario conocido: {} {} desde {} ({})", op, resultado, ip, detalle);
            return;
        }
        RegistroAuditoria r = new RegistroAuditoria();
        r.setFechaHora(LocalDateTime.now().withNano(0));
        r.setUsuarioId(usuarioId);
        r.setOperacion(op);
        r.setRecursoId(recursoId);
        r.setRuta(recortar(ruta == null ? "" : ruta, 1024));
        r.setTransferenciaId(transferenciaId);
        r.setResultado(resultado);
        r.setIpOrigen(recortar(ip, 45));
        r.setDetalle(detalle);
        r.setHashIntegridad(calcularHash(r));
        try {
            dao.insertar(r);
        } catch (DAOException e) {
            logger.error("No se pudo registrar auditoria", e);
        }
        if (resultado == ResultadoOperacion.EXITO) {
            operaciones.info("{} usuario={} recurso={} ruta={} ip={} - {}", op, usuarioId, recursoId,
                    LogUtil.limpiar(ruta), ip, LogUtil.limpiar(detalle));
        } else {
            operaciones.warn("{} {} usuario={} recurso={} ruta={} ip={} - {}", op, resultado, usuarioId, recursoId,
                    LogUtil.limpiar(ruta), ip, LogUtil.limpiar(detalle));
        }
    }

    public List<RegistroAuditoria> ultimos(int limite) {
        List<RegistroAuditoria> lista = dao.listarUltimos(limite);
        for (RegistroAuditoria r : lista) {
            r.setIntegro(calcularHash(r).equals(r.getHashIntegridad()));
        }
        return lista;
    }

    /** SHA-256 de los campos del registro; permite detectar manipulacion posterior. */
    public static String calcularHash(RegistroAuditoria r) {
        String base = String.join("|",
                Objects.toString(r.getFechaHora(), ""),
                Objects.toString(r.getUsuarioId(), ""),
                Objects.toString(r.getOperacion(), ""),
                Objects.toString(r.getRecursoId(), ""),
                Objects.toString(r.getRuta(), ""),
                Objects.toString(r.getTransferenciaId(), ""),
                Objects.toString(r.getResultado(), ""),
                Objects.toString(r.getIpOrigen(), ""),
                Objects.toString(r.getDetalle(), ""));
        return PasswordUtil.sha256(base);
    }

    private static String recortar(String s, int max) {
        return s != null && s.length() > max ? s.substring(0, max) : s;
    }
}
