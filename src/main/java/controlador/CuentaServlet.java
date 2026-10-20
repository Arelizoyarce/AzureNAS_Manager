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
import servicio.AlmacenamientoSimuladoClient;
import servicio.RecursoServicio;
import servicio.ServicioException;
import util.Param;
import util.Sesion;

/** Consola administrativa: cuentas de almacenamiento (Azure Storage Account simulada). */
@WebServlet(name = "CuentaServlet", urlPatterns = {"/cuentas"})
public class CuentaServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CuentaServlet.class);
    private RecursoServicio servicio;

    @Override
    public void init() {
        servicio = new RecursoServicio(new AlmacenamientoSimuladoClient());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("cuentas", servicio.listarCuentas());
        req.getRequestDispatcher("/cuentas.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            servicio.crearCuenta(Sesion.actual(req), Param.texto(req.getParameter("nombre")),
                    req.getParameter("region"), req.getParameter("tipoCredencial"),
                    req.getParameter("credencial"), Param.entero(req.getParameter("espacioGb")), Sesion.ip(req));
            Sesion.mensaje(req, "Cuenta de almacenamiento creada");
        } catch (ServicioException e) {
            Sesion.error(req, e.getMessage());
        } catch (DAOException e) {
            logger.error("Error al crear cuenta", e);
            Sesion.error(req, "No se pudo crear la cuenta (el nombre debe ser unico)");
        }
        resp.sendRedirect(req.getContextPath() + "/cuentas");
    }
}
