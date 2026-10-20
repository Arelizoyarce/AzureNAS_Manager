package util;

/** Lectura segura de parametros de formulario. */
public final class Param {

    private Param() {
    }

    public static Integer entero(String s) {
        try {
            return s == null || s.trim().isEmpty() ? null : Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean marcado(String s) {
        return s != null && (s.equals("on") || s.equals("true") || s.equals("1"));
    }

    public static String texto(String s) {
        return s == null ? "" : s.trim();
    }
}
