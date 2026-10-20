package util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import modelo.Usuario;

/** Acceso rapido a datos de sesion y de la peticion. */
public final class Sesion {

    public static final String USUARIO = "usuario";

    private Sesion() {
    }

    public static Usuario actual(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (Usuario) s.getAttribute(USUARIO);
    }

    public static String ip(HttpServletRequest req) {
        String ip = req.getRemoteAddr();
        return ip != null && ip.length() > 45 ? ip.substring(0, 45) : ip;
    }

    public static void mensaje(HttpServletRequest req, String texto) {
        req.getSession().setAttribute("mensaje", texto);
    }

    public static void error(HttpServletRequest req, String texto) {
        req.getSession().setAttribute("error", texto);
    }
}
