package controlador;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import servicio.AuditoriaServicio;

@WebServlet(name = "AuditoriaServlet", urlPatterns = {"/auditoria"})
public class AuditoriaServlet extends HttpServlet {

    private AuditoriaServicio servicio;

    @Override
    public void init() {
        servicio = new AuditoriaServicio();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("registros", servicio.ultimos(200));
        req.getRequestDispatcher("/auditoria.jsp").forward(req, resp);
    }
}
