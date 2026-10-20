package util;

/** Utilidades de formato para mostrar datos en las vistas. */
public final class Formato {

    private Formato() {
    }

    public static String bytes(Long b) {
        if (b == null) {
            return "-";
        }
        if (b < 1024) {
            return b + " B";
        }
        String[] u = {"KB", "MB", "GB", "TB"};
        double v = b;
        int i = -1;
        do {
            v /= 1024;
            i++;
        } while (v >= 1024 && i < u.length - 1);
        return String.format("%.1f %s", v, u[i]);
    }
}
