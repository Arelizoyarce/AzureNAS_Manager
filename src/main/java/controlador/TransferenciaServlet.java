package controlador;

import dao.DAOException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import modelo.Usuario;
import notificaciones.NotificadorEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servicio.AlmacenamientoSimuladoClient;
import servicio.RecursoServicio;
import servicio.ServicioException;
import servicio.TransferenciaServicio;
import util.Param;
import util.Sesion;

/** Carga, descarga y sincronizacion de archivos. */
@WebServlet(name = "TransferenciaServlet", urlPatterns = {"/transferencias"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 100L * 1024 * 1024, maxRequestSize = 105L * 1024 * 1024)
public class TransferenciaServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(TransferenciaServlet.class);
    private RecursoServicio recursos;
    private TransferenciaServicio servicio;

    @Override
    public void init() {
        AlmacenamientoSimuladoClient cliente = new AlmacenamientoSimuladoClient();
        recursos = new RecursoServicio(cliente);
        servicio = new TransferenciaServicio(cliente, recursos, new NotificadorEmail());
        logger.info("Servlet TransferenciaServlet inicializado correctamente");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = Sesion.actual(req);
        if ("descargar".equals(req.getParameter("accion"))) {
            descargar(req, resp, u);
            return;
        }
        try {
            req.setAttribute("transferencias", servicio.listar(u, 100));
            req.setAttribute("recursos", recursos.buscar(u, null, null));
        } catch (DAOException e) {
            logger.error("Error al listar transferencias", e);
            req.setAttribute("error", "No se pudieron cargar las transferencias");
        }
        req.getRequestDispatcher("/transferencias.jsp").forward(req, resp);
    }

    private void descargar(HttpServletRequest req, HttpServletResponse resp, Usuario u) throws IOException {
        Integer id = Param.entero(req.getParameter("recurso"));
        String ruta = Param.texto(req.getParameter("ruta"));
        try {
            if (id == null) {
                throw new ServicioException("Recurso invalido");
            }
            String nombre = ruta.replace('\\', '/');
            nombre = nombre.substring(nombre.lastIndexOf('/') + 1);
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''"
                    + URLEncoder.encode(nombre, "UTF-8").replace("+", "%20"));
            servicio.descargar(u, id, ruta, resp.getOutputStream(), Sesion.ip(req));
        } catch (ServicioException | DAOException e) {
            if (!resp.isCommitted()) {
                resp.reset();
                Sesion.error(req, e instanceof ServicioException ? e.getMessage() : "Error al descargar");
                resp.sendRedirect(req.getContextPath() + "/transferencias");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = Sesion.actual(req);
        String ip = Sesion.ip(req);
        String accion = Param.texto(req.getParameter("accion"));
        try {
            if ("subir".equals(accion)) {
                Integer recursoId = Param.entero(req.getParameter("recursoId"));
                Part archivo = req.getPart("archivo");
                if (recursoId == null || archivo == null || archivo.getSize() == 0) {
                    throw new ServicioException("Selecciona un recurso y un archivo");
                }
                String nombre = Paths.get(archivo.getSubmittedFileName()).getFileName().toString();
                try (InputStream in = archivo.getInputStream()) {
                    servicio.subir(u, recursoId, req.getParameter("carpeta"), nombre, in,
                            Param.marcado(req.getParameter("sobrescribir")), ip);
                }
                Sesion.mensaje(req, "Archivo subido correctamente");
            } else if ("sincronizar".equals(accion)) {
                Integer origen = Param.entero(req.getParameter("origenId"));
                Integer destino = Param.entero(req.getParameter("destinoId"));
                if (origen == null || destino == null) {
                    throw new ServicioException("Selecciona origen y destino");
                }
                servicio.sincronizar(u, origen, destino, Param.marcado(req.getParameter("sobrescribir")), ip);
                Sesion.mensaje(req, "Sincronizacion completada");
            }
        } catch (ServicioException e) {
            Sesion.error(req, e.getMessage());
        } catch (DAOException e) {
            logger.error("Error de base de datos en transferencia", e);
            Sesion.error(req, "Error de base de datos al registrar la transferencia");
        }
        resp.sendRedirect(req.getContextPath() + "/transferencias");
    }
}
