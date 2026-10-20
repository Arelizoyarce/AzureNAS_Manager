package controlador;

import dao.DAOException;
import dao.UsuarioDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import modelo.Recurso;
import modelo.TipoRecurso;
import modelo.Usuario;
import notificaciones.NotificadorEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servicio.AlmacenamientoSimuladoClient;
import servicio.RecursoServicio;
import servicio.ServicioException;
import servicio.UsuarioServicio;
import util.Param;
import util.Sesion;

/** Recursos (File Shares / Blob Containers): busqueda avanzada, explorador y administracion. */
@WebServlet(name = "RecursoServlet", urlPatterns = {"/recursos"})
public class RecursoServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RecursoServlet.class);
    private RecursoServicio servicio;
    private UsuarioServicio usuarios;

    @Override
    public void init() {
        servicio = new RecursoServicio(new AlmacenamientoSimuladoClient());
        usuarios = new UsuarioServicio(new UsuarioDAO(), new NotificadorEmail());
        logger.info("Servlet RecursoServlet inicializado correctamente");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = Sesion.actual(req);
        try {
            if ("explorar".equals(req.getParameter("accion"))) {
                explorar(req, resp, u);
                return;
            }
            String q = Param.texto(req.getParameter("q"));
            TipoRecurso tipo = null;
            try {
                String t = req.getParameter("tipo");
                tipo = t == null || t.isEmpty() ? null : TipoRecurso.valueOf(t);
            } catch (IllegalArgumentException e) {
                tipo = null;
            }
            req.setAttribute("q", q);
            req.setAttribute("tipoSel", tipo == null ? "" : tipo.name());
            req.setAttribute("recursos", servicio.buscar(u, q, tipo));
            if (u.isAdmin()) {
                req.setAttribute("cuentas", servicio.listarCuentas());
                req.setAttribute("areas", usuarios.listarAreas());
                req.setAttribute("usuarios", usuarios.listar());
            }
        } catch (ServicioException | DAOException e) {
            logger.error("Error en recursos", e);
            req.setAttribute("error", e instanceof ServicioException ? e.getMessage() : "Error al consultar recursos");
        }
        req.getRequestDispatcher("/recursos.jsp").forward(req, resp);
    }

    private void explorar(HttpServletRequest req, HttpServletResponse resp, Usuario u) throws ServletException, IOException {
        Integer id = Param.entero(req.getParameter("id"));
        String ruta = Param.texto(req.getParameter("ruta"));
        try {
            if (id == null) {
                throw new ServicioException("Recurso invalido");
            }
            Recurso r = servicio.exigir(u, id, modelo.NivelAcceso.LECTURA, Sesion.ip(req));
            req.setAttribute("recurso", r);
            req.setAttribute("ruta", ruta);
            req.setAttribute("padre", padre(ruta));
            req.setAttribute("archivos", servicio.explorar(u, id, ruta, Sesion.ip(req)));
        } catch (ServicioException e) {
            Sesion.error(req, e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/recursos");
            return;
        }
        req.getRequestDispatcher("/explorador.jsp").forward(req, resp);
    }

    /** "a/b/c/" -> "a/b/" ; "a/" -> "" */
    private static String padre(String ruta) {
        String r = ruta.replace('\\', '/');
        while (r.endsWith("/")) {
            r = r.substring(0, r.length() - 1);
        }
        int i = r.lastIndexOf('/');
        return i < 0 ? "" : r.substring(0, i + 1);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario admin = Sesion.actual(req);
        String ip = Sesion.ip(req);
        try {
            String accion = Param.texto(req.getParameter("accion"));
            if ("crearRecurso".equals(accion)) {
                servicio.crearRecurso(admin, Param.entero(req.getParameter("cuentaId")),
                        Param.entero(req.getParameter("areaId")), req.getParameter("tipo"),
                        Param.texto(req.getParameter("nombre")), Param.entero(req.getParameter("cuotaGb")), ip);
                Sesion.mensaje(req, "Recurso creado");
            } else if ("asignarAcceso".equals(accion)) {
                servicio.asignarAcceso(admin, Param.entero(req.getParameter("usuarioId")),
                        Param.entero(req.getParameter("recursoId")), req.getParameter("nivel"), ip);
                Sesion.mensaje(req, "Acceso asignado");
            }
        } catch (ServicioException e) {
            Sesion.error(req, e.getMessage());
        } catch (DAOException e) {
            logger.error("Error al guardar recurso/acceso", e);
            Sesion.error(req, "No se pudo guardar (revisa que no exista un duplicado)");
        }
        resp.sendRedirect(req.getContextPath() + "/recursos");
    }
}
