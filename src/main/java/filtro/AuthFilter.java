package filtro;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.Usuario;
import util.Sesion;

/** Control de sesion, rol de administrador y token CSRF para todas las peticiones. */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    private static final Set<String> PUBLICAS = new HashSet<>(Arrays.asList(
            "/", "/index.html", "/login", "/login.jsp", "/error.jsp"));
    private static final Set<String> SOLO_ADMIN = new HashSet<>(Arrays.asList(
            "/UsuarioServlet", "/areas", "/cuentas", "/auditoria", "/powershell"));

    @Override
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) rq;
        HttpServletResponse resp = (HttpServletResponse) rs;
        req.setCharacterEncoding("UTF-8");
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "DENY");

        String path = req.getServletPath();
        if (PUBLICAS.contains(path) || path.startsWith("/css/")) {
            chain.doFilter(rq, rs);
            return;
        }

        Usuario u = Sesion.actual(req);
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        // Las paginas JSP solo se muestran a traves de un servlet
        if (path.endsWith(".jsp")) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        if (SOLO_ADMIN.contains(path) && !u.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Se requiere rol administrador");
            return;
        }

        HttpSession s = req.getSession();
        if (s.getAttribute("csrf") == null) {
            s.setAttribute("csrf", UUID.randomUUID().toString());
        }
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String token = req.getParameter("_csrf");
            if (token == null || !token.equals(s.getAttribute("csrf"))) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Token CSRF invalido");
                return;
            }
        }
        chain.doFilter(rq, rs);
    }
}
