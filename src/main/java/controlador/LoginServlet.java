package controlador;

import dao.DAOException;
import dao.UsuarioDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.Usuario;
import notificaciones.NotificadorEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servicio.ServicioException;
import servicio.UsuarioServicio;
import util.Sesion;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private UsuarioServicio servicio;

    @Override
    public void init() {
        servicio = new UsuarioServicio(new UsuarioDAO(), new NotificadorEmail());
        logger.info("Servlet LoginServlet inicializado correctamente");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (Sesion.actual(req) != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        try {
            Usuario u = servicio.autenticar(username, password, Sesion.ip(req));
            u.setPasswordHash(null);
            HttpSession vieja = req.getSession(false);
            if (vieja != null) {
                vieja.invalidate();
            }
            req.getSession(true).setAttribute(Sesion.USUARIO, u);
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (ServicioException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("username", username);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } catch (DAOException e) {
            logger.error("Error de base de datos en login", e);
            req.setAttribute("error", "No se pudo conectar con la base de datos");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
