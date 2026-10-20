package servicio;

import dao.DAOException;
import dao.RecursoDAO;
import dao.TransferenciaDAO;
import interfaces.IAlmacenamientoClient;
import interfaces.INotificador;
import interfaces.IRecursoDAO;
import interfaces.ITransferenciaDAO;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import modelo.EstadoTransferencia;
import modelo.NivelAcceso;
import modelo.Recurso;
import modelo.ResultadoOperacion;
import modelo.TipoOperacion;
import modelo.TipoTransferencia;
import modelo.Transferencia;
import modelo.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Subidas, descargas y sincronizaciones. Cada operacion queda en transferencia y registro_auditoria. */
public class TransferenciaServicio {

    private static final Logger logger = LoggerFactory.getLogger(TransferenciaServicio.class);

    private final ITransferenciaDAO dao = new TransferenciaDAO();
    private final IRecursoDAO recursoDAO = new RecursoDAO();
    private final IAlmacenamientoClient cliente;
    private final RecursoServicio recursos;
    private final INotificador notificador;
    private final AuditoriaServicio auditoria = new AuditoriaServicio();

    public TransferenciaServicio(IAlmacenamientoClient cliente, RecursoServicio recursos, INotificador notificador) {
        this.cliente = cliente;
        this.recursos = recursos;
        this.notificador = notificador;
    }

    public List<Transferencia> listar(Usuario u, int limite) {
        return u.isAdmin() ? dao.listarRecientes(limite) : dao.listarPorUsuario(u.getId(), limite);
    }

    public Transferencia subir(Usuario u, int recursoId, String carpeta, String nombreArchivo,
            InputStream datos, boolean sobrescribir, String ip) {
        Recurso r = recursos.exigir(u, recursoId, NivelAcceso.ESCRITURA, ip);
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty() || nombreArchivo.length() > 255) {
            throw new ServicioException("Selecciona un archivo valido");
        }
        String ruta = limpiarRuta(carpeta, nombreArchivo);
        Transferencia t = nueva(u, TipoTransferencia.SUBIDA, r, ruta);
        t.setNombreArchivo(nombreArchivo);
        t.setSobrescribir(sobrescribir);
        t.setObjetosTotales(1);
        t.setId(dao.insertar(t));
        iniciar(t);
        try {
            long bytes = cliente.subir(r, ruta, datos, sobrescribir);
            t.setTamanoBytes(bytes);
            t.setObjetosProcesados(1);
            finalizarOk(t);
            recursoDAO.actualizarTamano(r.getId(), cliente.tamanoTotal(r));
            auditoria.registrar(u.getId(), TipoOperacion.SUBIDA, r.getId(), ruta, t.getId(), ResultadoOperacion.EXITO, ip,
                    bytes + " bytes");
            notificador.notificar(u.getUsername(), "Subida completada", ruta + " en " + r.getNombre());
        } catch (IOException | RuntimeException e) {
            finalizarError(t, e);
            auditoria.registrar(u.getId(), TipoOperacion.SUBIDA, r.getId(), ruta, t.getId(), ResultadoOperacion.FALLO, ip,
                    e.getMessage());
            throw new ServicioException("La subida fallo: " + e.getMessage(), e);
        }
        return t;
    }

    /** Escribe el archivo en 'salida'. Lanza ServicioException si no existe o no hay permiso. */
    public Transferencia descargar(Usuario u, int recursoId, String ruta, OutputStream salida, String ip) {
        Recurso r = recursos.exigir(u, recursoId, NivelAcceso.LECTURA, ip);
        String limpia = limpiarRuta(ruta);
        if (limpia.isEmpty()) {
            throw new ServicioException("Indica el archivo a descargar");
        }
        Transferencia t = nueva(u, TipoTransferencia.DESCARGA, r, limpia);
        t.setNombreArchivo(limpia.substring(limpia.lastIndexOf('/') + 1));
        t.setObjetosTotales(1);
        t.setId(dao.insertar(t));
        iniciar(t);
        try {
            long bytes = cliente.descargar(r, limpia, salida);
            t.setTamanoBytes(bytes);
            t.setObjetosProcesados(1);
            finalizarOk(t);
            auditoria.registrar(u.getId(), TipoOperacion.DESCARGA, r.getId(), limpia, t.getId(), ResultadoOperacion.EXITO, ip,
                    bytes + " bytes");
        } catch (IOException | RuntimeException e) {
            finalizarError(t, e);
            auditoria.registrar(u.getId(), TipoOperacion.DESCARGA, r.getId(), limpia, t.getId(), ResultadoOperacion.FALLO, ip,
                    e.getMessage());
            throw new ServicioException("La descarga fallo: " + e.getMessage(), e);
        }
        return t;
    }

    public Transferencia sincronizar(Usuario u, int origenId, int destinoId, boolean sobrescribir, String ip) {
        if (origenId == destinoId) {
            throw new ServicioException("El origen y el destino deben ser distintos");
        }
        Recurso o = recursos.exigir(u, origenId, NivelAcceso.LECTURA, ip);
        Recurso d = recursos.exigir(u, destinoId, NivelAcceso.ESCRITURA, ip);
        Transferencia t = nueva(u, TipoTransferencia.SINCRONIZACION, o, "/");
        t.setRecursoDestinoId(d.getId());
        t.setRutaDestino("/");
        t.setSobrescribir(sobrescribir);
        t.setId(dao.insertar(t));
        iniciar(t);
        try {
            int[] res = cliente.sincronizar(o, d, sobrescribir);
            t.setObjetosTotales(res[0]);
            t.setObjetosProcesados(res[1]);
            t.setTamanoBytes(cliente.tamanoTotal(o));
            finalizarOk(t);
            recursoDAO.actualizarTamano(d.getId(), cliente.tamanoTotal(d));
            auditoria.registrar(u.getId(), TipoOperacion.SINCRONIZACION, o.getId(), "/", t.getId(), ResultadoOperacion.EXITO, ip,
                    res[1] + " de " + res[0] + " objetos hacia " + d.getNombre());
            notificador.notificar(u.getUsername(), "Sincronizacion completada", o.getNombre() + " -> " + d.getNombre());
        } catch (IOException | RuntimeException e) {
            finalizarError(t, e);
            auditoria.registrar(u.getId(), TipoOperacion.SINCRONIZACION, o.getId(), "/", t.getId(), ResultadoOperacion.FALLO, ip,
                    e.getMessage());
            throw new ServicioException("La sincronizacion fallo: " + e.getMessage(), e);
        }
        return t;
    }

    // ---------- auxiliares ----------
    private Transferencia nueva(Usuario u, TipoTransferencia tipo, Recurso r, String ruta) {
        Transferencia t = new Transferencia();
        t.setUsuarioId(u.getId());
        t.setTipo(tipo);
        t.setRecursoId(r.getId());
        t.setRutaRemota(ruta);
        t.setTamanoBytes(0L);
        t.setProgreso(0);
        t.setEstado(EstadoTransferencia.PENDIENTE);
        t.setFechaSolicitud(LocalDateTime.now());
        return t;
    }

    private void iniciar(Transferencia t) {
        t.setEstado(EstadoTransferencia.EN_CURSO);
        t.setFechaInicio(LocalDateTime.now());
        dao.actualizar(t);
    }

    private void finalizarOk(Transferencia t) {
        t.setEstado(EstadoTransferencia.COMPLETADA);
        t.setProgreso(100);
        t.setFechaFin(LocalDateTime.now());
        dao.actualizar(t);
    }

    private void finalizarError(Transferencia t, Exception e) {
        logger.error("Transferencia {} fallida", t.getId(), e);
        t.setEstado(EstadoTransferencia.FALLIDA);
        String m = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        t.setMensajeError(m.length() > 1000 ? m.substring(0, 1000) : m);
        t.setFechaFin(LocalDateTime.now());
        try {
            dao.actualizar(t);
        } catch (DAOException ex) {
            logger.error("No se pudo guardar el error de la transferencia", ex);
        }
    }

    /** Une partes de ruta con "/", rechaza ".." y segmentos vacios. */
    static String limpiarRuta(String... partes) {
        List<String> seg = new ArrayList<>();
        for (String parte : partes) {
            if (parte == null) {
                continue;
            }
            for (String s : parte.replace('\\', '/').split("/")) {
                String x = s.trim();
                if (x.isEmpty() || x.equals(".")) {
                    continue;
                }
                if (x.equals("..")) {
                    throw new ServicioException("Ruta no permitida");
                }
                seg.add(x);
            }
        }
        String ruta = String.join("/", seg);
        if (ruta.length() > 1024) {
            throw new ServicioException("La ruta es demasiado larga");
        }
        return ruta;
    }
}
