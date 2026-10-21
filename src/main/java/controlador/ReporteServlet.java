package controlador;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import dao.DAOException;
import dao.UsuarioDAO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import modelo.Area;
import modelo.RegistroAuditoria;
import modelo.Recurso;
import modelo.ResultadoOperacion;
import modelo.TipoOperacion;
import modelo.TipoRecurso;
import modelo.Transferencia;
import modelo.Usuario;
import notificaciones.NotificadorEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servicio.AlmacenamientoSimuladoClient;
import servicio.AuditoriaServicio;
import servicio.RecursoServicio;
import servicio.ServicioException;
import servicio.TransferenciaServicio;
import servicio.UsuarioServicio;
import util.ExcelExportador;
import util.Param;
import util.Sesion;

/**
 * Exporta reportes a Excel (.xlsx) con Apache POI.
 * /reportes?tipo=auditoria | areas (solo ADMIN) | recursos | transferencias
 * Los reportes de recursos y transferencias respetan los permisos del usuario.
 */
@WebServlet(name = "ReporteServlet", urlPatterns = {"/reportes"})
public class ReporteServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ReporteServlet.class);
    private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final DateTimeFormatter FECHA_ARCHIVO = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
    private static final double BYTES_POR_GB = 1024d * 1024d * 1024d;

    private RecursoServicio recursos;
    private TransferenciaServicio transferencias;
    private UsuarioServicio usuarios;
    private final AuditoriaServicio auditoria = new AuditoriaServicio();

    /** Datos de un reporte: nombre de la hoja, encabezados y filas. */
    private static final class Reporte {

        final String hoja;
        final List<String> encabezados;
        final List<List<Object>> filas;

        Reporte(String hoja, List<String> encabezados, List<List<Object>> filas) {
            this.hoja = hoja;
            this.encabezados = encabezados;
            this.filas = filas;
        }
    }

    @Override
    public void init() {
        AlmacenamientoSimuladoClient cliente = new AlmacenamientoSimuladoClient();
        NotificadorEmail notificador = new NotificadorEmail();
        recursos = new RecursoServicio(cliente);
        transferencias = new TransferenciaServicio(cliente, recursos, notificador);
        usuarios = new UsuarioServicio(new UsuarioDAO(), notificador);
        logger.info("Servlet ReporteServlet inicializado correctamente");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario u = Sesion.actual(req);
        String ip = Sesion.ip(req);
        String tipo = Param.texto(req.getParameter("tipo"));
        try {
            Reporte rep;
            switch (tipo) {
                case "auditoria":
                    exigirAdmin(u, tipo, ip);
                    rep = reporteAuditoria();
                    break;
                case "areas":
                    exigirAdmin(u, tipo, ip);
                    rep = reporteAreas(u);
                    break;
                case "recursos":
                    rep = reporteRecursos(u, req);
                    break;
                case "transferencias":
                    rep = reporteTransferencias(u);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo de reporte no valido");
                    return;
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ExcelExportador.escribir(rep.hoja, rep.encabezados, rep.filas, buffer);

            auditoria.registrar(u.getId(), TipoOperacion.CONSULTA, null, "reporte/" + tipo, null,
                    ResultadoOperacion.EXITO, ip, "Reporte exportado a Excel: " + tipo + " (" + rep.filas.size() + " filas)");

            String archivo = tipo + "_" + LocalDateTime.now().format(FECHA_ARCHIVO) + ".xlsx";
            resp.setContentType(XLSX);
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + archivo + "\"");
            resp.setContentLength(buffer.size());
            buffer.writeTo(resp.getOutputStream());
        } catch (ServicioException | DAOException e) {
            logger.error("Error al generar el reporte {}", tipo, e);
            Sesion.error(req, e instanceof ServicioException ? e.getMessage() : "No se pudo generar el reporte");
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        }
    }

    private void exigirAdmin(Usuario u, String tipo, String ip) {
        if (u == null || !u.isAdmin()) {
            if (u != null) {
                auditoria.registrar(u.getId(), TipoOperacion.CONSULTA, null, "reporte/" + tipo, null,
                        ResultadoOperacion.DENEGADO, ip, "Reporte restringido a administradores");
            }
            throw new ServicioException("Acceso denegado: se requiere rol administrador");
        }
    }

    private Reporte reporteAuditoria() {
        List<List<Object>> filas = new ArrayList<>();
        for (RegistroAuditoria r : auditoria.ultimos(1000)) {
            filas.add(Arrays.<Object>asList(r.getId(), r.getFechaHoraTexto(), r.getUsuarioId(), r.getOperacion(),
                    r.getRecursoId(), r.getRuta(), r.getTransferenciaId(), r.getResultado(), r.getIpOrigen(),
                    r.getDetalle(), r.isIntegro() ? "OK" : "ALTERADO"));
        }
        return new Reporte("Auditoria", ImmutableList.of("ID", "Fecha", "Usuario", "Operación", "Recurso", "Ruta",
                "Transferencia", "Resultado", "IP", "Detalle", "Integridad"), filas);
    }

    private Reporte reporteRecursos(Usuario u, HttpServletRequest req) {
        String q = Param.texto(req.getParameter("q"));
        TipoRecurso tipo = null;
        String t = Param.texto(req.getParameter("tr"));
        if (!t.isEmpty()) {
            try {
                tipo = TipoRecurso.valueOf(t);
            } catch (IllegalArgumentException e) {
                tipo = null;
            }
        }
        List<List<Object>> filas = new ArrayList<>();
        for (Recurso r : recursos.buscar(u, q, tipo)) {
            filas.add(Arrays.<Object>asList(r.getId(), r.getNombre(), r.getTipo(), r.getCuentaId(), r.getAreaId(),
                    r.getTamanoBytes(), r.getTamanoLegible(), r.getCuotaGb(), r.getUltimaModificacionTexto()));
        }
        return new Reporte("Recursos", ImmutableList.of("ID", "Nombre", "Tipo", "Cuenta", "Área", "Tamaño (bytes)",
                "Tamaño", "Cuota (GB)", "Última modificación"), filas);
    }

    private Reporte reporteTransferencias(Usuario u) {
        List<List<Object>> filas = new ArrayList<>();
        for (Transferencia t : transferencias.listar(u, 500)) {
            filas.add(Arrays.<Object>asList(t.getId(), t.getTipo(), t.getEstado(), t.getRecursoId(),
                    t.getRutaRemota(), t.getNombreArchivo(), t.getTamanoLegible(), t.getProgreso(),
                    t.getFechaSolicitudTexto(), t.getFechaFinTexto(), t.getMensajeError()));
        }
        return new Reporte("Transferencias", ImmutableList.of("ID", "Tipo", "Estado", "Recurso", "Ruta", "Archivo",
                "Tamaño", "Progreso (%)", "Solicitud", "Fin", "Error"), filas);
    }

    /** Uso de almacenamiento por area: cantidad de recursos, espacio usado y cuota. Agrupa con Guava Multimaps. */
    private Reporte reporteAreas(Usuario admin) {
        List<Recurso> conArea = new ArrayList<>();
        for (Recurso r : recursos.buscar(admin, "", null)) {
            if (r.getAreaId() != null) {
                conArea.add(r);
            }
        }
        ImmutableListMultimap<Integer, Recurso> porArea = Multimaps.index(conArea, Recurso::getAreaId);

        List<List<Object>> filas = new ArrayList<>();
        int totalRecursos = 0;
        long totalUsado = 0;
        long totalCuotaGb = 0;
        for (Area a : usuarios.listarAreas()) {
            List<Recurso> delArea = porArea.get(a.getId());
            long usado = 0;
            long cuotaGb = 0;
            for (Recurso r : delArea) {
                usado += r.getTamanoBytes() == null ? 0L : r.getTamanoBytes();
                cuotaGb += r.getCuotaGb() == null ? 0L : r.getCuotaGb();
            }
            filas.add(fila(a.getNombre(), delArea.size(), usado, cuotaGb));
            totalRecursos += delArea.size();
            totalUsado += usado;
            totalCuotaGb += cuotaGb;
        }
        filas.add(fila("TOTAL", totalRecursos, totalUsado, totalCuotaGb));
        return new Reporte("Uso por area", ImmutableList.of("Área", "Recursos", "Espacio usado (bytes)",
                "Espacio usado", "Cuota total (GB)", "% de la cuota usado"), filas);
    }

    private static List<Object> fila(String nombre, int recursos, long usado, long cuotaGb) {
        Double porcentaje = cuotaGb > 0 ? Math.round(usado * 1000.0 / (cuotaGb * BYTES_POR_GB)) / 10.0 : null;
        return Arrays.<Object>asList(nombre, recursos, usado, util.Formato.bytes(usado), cuotaGb, porcentaje);
    }
}
