package controlador;

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
import servicio.AlmacenamientoSimuladoClient;
import servicio.RecursoServicio;
import servicio.TransferenciaServicio;
import servicio.UsuarioServicio;
import util.Sesion;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServlet.class);
    private RecursoServicio recursos;
    private TransferenciaServicio transferencias;
    private UsuarioServicio usuarios;

    @Override
    public void init() {
        AlmacenamientoSimuladoClient cliente = new AlmacenamientoSimuladoClient();
        NotificadorEmail notificador = new NotificadorEmail();
        recursos = new RecursoServicio(cliente);
        transferencias = new TransferenciaServicio(cliente, recursos, notificador);
        usuarios = new UsuarioServicio(new UsuarioDAO(), notificador);
        logger.info("Servlet DashboardServlet inicializado correctamente");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = Sesion.actual(req);
        try {
            req.setAttribute("totalRecursos", recursos.buscar(u, null, null).size());
            req.setAttribute("transferencias", transferencias.listar(u, 10));
            if (u.isAdmin()) {
                req.setAttribute("totalUsuarios", usuarios.contar());
                req.setAttribute("totalCuentas", recursos.listarCuentas().size());
            }
        } catch (RuntimeException e) {
            logger.error("Error al cargar el dashboard", e);
            req.setAttribute("error", "No se pudieron cargar los datos del panel");
        }
        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }
}
