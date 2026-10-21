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
import util.LimiteIntentos;
import util.LogUtil;
import util.Sesion;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    /** Logger de seguridad: escribe en seguridad.log (ver logback.xml). */
    private static final Logger seguridad = LoggerFactory.getLogger("seguridad");
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
        String ip = Sesion.ip(req);
        String clave = LimiteIntentos.clave(ip, username);

        // Guava: bloqueo temporal despues de varios intentos fallidos
        if (LimiteIntentos.bloqueado(clave)) {
            seguridad.warn("Inicio de sesion bloqueado por exceso de intentos: usuario={} ip={}",
                    LogUtil.limpiar(username), ip);
            req.setAttribute("error", "Demasiados intentos fallidos. Espera " + LimiteIntentos.MINUTOS_BLOQUEO
                    + " minutos e intentalo de nuevo");
            req.setAttribute("username", username);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            Usuario u = servicio.autenticar(username, password, ip);
            LimiteIntentos.limpiar(clave);
            seguridad.info("Inicio de sesion correcto: usuario={} ip={}", LogUtil.limpiar(u.getUsername()), ip);
            u.setPasswordHash(null);
            HttpSession vieja = req.getSession(false);
            if (vieja != null) {
                vieja.invalidate();
            }
            req.getSession(true).setAttribute(Sesion.USUARIO, u);
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (ServicioException e) {
            LimiteIntentos.registrarFallo(clave);
            seguridad.warn("Inicio de sesion fallido ({}): usuario={} ip={}", e.getMessage(),
                    LogUtil.limpiar(username), ip);
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
