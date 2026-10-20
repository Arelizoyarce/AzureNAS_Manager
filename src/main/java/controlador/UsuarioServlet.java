package controlador;

import dao.DAOException;
import dao.UsuarioDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import modelo.Usuario;
import notificaciones.NotificadorEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servicio.ServicioException;
import servicio.UsuarioServicio;
import util.Param;
import util.Sesion;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
public class UsuarioServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServlet.class);
    private UsuarioServicio servicio;

    @Override
    public void init() {
        servicio = new UsuarioServicio(new UsuarioDAO(), new NotificadorEmail());
        logger.info("Servlet UsuarioServlet inicializado correctamente");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("usuarios", servicio.listar());
        req.setAttribute("areas", servicio.listarAreas());
        req.getRequestDispatcher("/listaUsuarios.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario admin = Sesion.actual(req);
        String ip = Sesion.ip(req);
        try {
            String accion = Param.texto(req.getParameter("accion"));
            if ("registrar".equals(accion)) {
                servicio.registrarUsuario(admin, Param.texto(req.getParameter("username")),
                        req.getParameter("nombreCompleto"), req.getParameter("password"),
                        req.getParameter("rol"), Param.entero(req.getParameter("areaId")), ip);
                Sesion.mensaje(req, "Usuario registrado correctamente");
            } else if ("estado".equals(accion)) {
                Integer id = Param.entero(req.getParameter("id"));
                if (id == null) {
                    throw new ServicioException("Usuario invalido");
                }
                servicio.cambiarEstado(admin, id, Param.marcado(req.getParameter("activo")), ip);
                Sesion.mensaje(req, "Estado actualizado");
            }
        } catch (ServicioException e) {
            Sesion.error(req, e.getMessage());
        } catch (DAOException e) {
            logger.error("Error al registrar usuario", e);
            Sesion.error(req, "Error al registrar usuario");
        }
        resp.sendRedirect(req.getContextPath() + "/UsuarioServlet");
    }
}
