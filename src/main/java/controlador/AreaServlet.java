package controlador;

import dao.DAOException;
import dao.UsuarioDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import notificaciones.NotificadorEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servicio.ServicioException;
import servicio.UsuarioServicio;
import util.Sesion;

@WebServlet(name = "AreaServlet", urlPatterns = {"/areas"})
public class AreaServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AreaServlet.class);
    private UsuarioServicio servicio;

    @Override
    public void init() {
        servicio = new UsuarioServicio(new UsuarioDAO(), new NotificadorEmail());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("areas", servicio.listarAreas());
        req.getRequestDispatcher("/areas.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            servicio.crearArea(Sesion.actual(req), req.getParameter("nombre"), Sesion.ip(req));
            Sesion.mensaje(req, "Area creada correctamente");
        } catch (ServicioException e) {
            Sesion.error(req, e.getMessage());
        } catch (DAOException e) {
            logger.error("Error al crear area", e);
            Sesion.error(req, "No se pudo crear el area (el nombre debe ser unico)");
        }
        resp.sendRedirect(req.getContextPath() + "/areas");
    }
}
