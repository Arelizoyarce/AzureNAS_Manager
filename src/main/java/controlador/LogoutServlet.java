package controlador;

import dao.UsuarioDAO;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import notificaciones.NotificadorEmail;
import servicio.UsuarioServicio;
import util.Sesion;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    private UsuarioServicio servicio;

    @Override
    public void init() {
        servicio = new UsuarioServicio(new UsuarioDAO(), new NotificadorEmail());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        servicio.cerrarSesion(Sesion.actual(req), Sesion.ip(req));
        HttpSession s = req.getSession(false);
        if (s != null) {
            s.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
