package controlador;

import dao.DAOException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servicio.PowerShellExecutor;
import servicio.PowerShellServicio;
import servicio.ServicioException;
import util.Sesion;

@WebServlet(name = "PowerShellServlet", urlPatterns = {"/powershell"})
public class PowerShellServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(PowerShellServlet.class);
    private PowerShellServicio servicio;

    @Override
    public void init() {
        servicio = new PowerShellServicio(new PowerShellExecutor());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("ejecuciones", servicio.ultimas(50));
        req.getRequestDispatcher("/powershell.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            servicio.ejecutar(Sesion.actual(req), req.getParameter("comando"), Sesion.ip(req));
            Sesion.mensaje(req, "Comando ejecutado. Revisa el resultado en el historial.");
        } catch (ServicioException e) {
            Sesion.error(req, e.getMessage());
        } catch (DAOException e) {
            logger.error("Error al registrar ejecucion PowerShell", e);
            Sesion.error(req, "Error de base de datos");
        }
        resp.sendRedirect(req.getContextPath() + "/powershell");
    }
}
